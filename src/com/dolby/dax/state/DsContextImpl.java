/*
 * Copyright (C) 2017 TripNDroid Mobile Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dolby.dax.state;

import android.media.AudioSystem;
import com.dolby.dax.db.Provider;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.model.Tuning;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DsContextImpl implements DsContext
{
    final Map<Port, String> devices;
    boolean isDapOn;
    final DsContextChangeObservable observable;
    final List<ProfileContext> profiles;
    final Provider provider;
    ProfileContext selectedContext;
    final Map<Port, Tuning> tunings;
    
    public DsContextImpl(final Provider provider, final DsContextChangeObservable observable) {
        this.provider = provider;
        this.observable = observable;
        this.profiles = new ArrayList<ProfileContext>();

        for (ProfileType pt : SupportedProfiles) {
            this.profiles.add(new ProfileContext(provider, pt));
        }
        this.devices = new EnumMap<Port, String>(Port.class);
        this.tunings = new EnumMap<Port, Tuning>(Port.class);
        this.selectedContext = null;
    }
    
    ProfileContext getContext(final ProfileType profileType) {
        if (this.isSelected(profileType)) {
            return this.selectedContext;
        }
        for (final ProfileContext profileContext : this.profiles) {
            if (profileContext.getType() == profileType) {
                return profileContext;
            }
        }
        throw new RuntimeException();
    }
    
    @Override
    public boolean getDapOn() {
        return this.isDapOn;
    }
    
    @Override
    public Profile getDefaultProfile(final ProfileType profileType) {
        return this.provider.loadDefaultProfile(profileType);
    }
    
    @Override
    public String getDefaultTuningDevice(final Port port) {
        return port.toString();
    }
    
    @Override
    public Profile getProfile(final ProfileType profileType) {
        return this.getContext(profileType).getProfile();
    }
    
    @Override
    public ProfileEndpoint getProfileEndpoint(final ProfileType profileType, final Endpoint endpoint) {
        return this.getContext(profileType).getProfileEndpoint(endpoint);
    }
    
    @Override
    public int[] getProfileGeqParameter(final ProfileType profileType, final Parameter parameter) {
        return this.getContext(profileType).getSelectedGeqPreset().get(parameter);
    }
    
    @Override
    public IeqPreset getProfileIeq(final ProfileType profileType) {
        return this.getContext(profileType).getSelectedIeqPreset();
    }
    
    @Override
    public int[] getProfileParameter(final ProfileType profileType, final Endpoint endpoint, final Parameter parameter) {
        return this.getContext(this.getContext(profileType).getProfile().getType()).getProfileEndpoint(endpoint).get(parameter);
    }
    
    @Override
    public int[] getProfileParameter(final ProfileType profileType, final Parameter parameter) {
        return this.getContext(profileType).getProfile().get(parameter);
    }
    
    @Override
    public int[] getProfileParameter(final ProfileType profileType, final Port port, final Parameter parameter) {
        return this.getContext(this.getContext(profileType).getProfile().getType()).getProfilePort(port).get(parameter);
    }
    
    @Override
    public Provider getProvider() {
        return this.provider;
    }
    
    @Override
    public Profile getSelectedProfile() {
        return this.selectedContext.getProfile();
    }
    
    @Override
    public ProfileEndpoint getSelectedProfileEndpoint(final Port port) {
        return this.selectedContext.getProfileEndpoint(this.getSelectedTuning(port).getEndpoint());
    }
    
    @Override
    public GeqPreset getSelectedProfileGeq() {
        return this.selectedContext.getSelectedGeqPreset();
    }
    
    @Override
    public IeqPreset getSelectedProfileIeq() {
        return this.selectedContext.getSelectedIeqPreset();
    }
    
    @Override
    public ProfilePort getSelectedProfilePort(final Port port) {
        return this.selectedContext.getProfilePort(port);
    }
    
    @Override
    public Tuning getSelectedTuning(final Port port) {
        final Tuning tuning = this.tunings.get(port);
        if (tuning == null) {
            throw new RuntimeException();
        }
        return tuning;
    }
    
    @Override
    public String getSelectedTuningDevice(final Port port) {
        return this.devices.get(port);
    }
    
    @Override
    public int[] getSelectedTuningParameter(final Port port, final Parameter parameter) {
        return this.getSelectedTuning(port).get(parameter);
    }
    
    @Override
    public boolean isProfileModified(final ProfileType profileType) {
        return this.getContext(profileType).isModified();
    }
    
    boolean isSelected(final ProfileType profileType) {
        return this.selectedContext.getType() == profileType;
    }
    
    @Override
    public void load() {
        if (Strings.isNullOrEmpty(this.provider.getDefaultXmlSignature())) {
            return;
        }
        final ProfileType selectedProfile = this.provider.getSelectedProfile();
        for (final ProfileContext selectedContext : this.profiles) {
            selectedContext.load();
            if (selectedContext.getType() == selectedProfile) {
                this.selectedContext = selectedContext;
            }
        }
        for (final Port port : DsContextImpl.SupportedPorts) {
            final String defaultTuningDevice = this.getDefaultTuningDevice(port);
            this.devices.put(port, defaultTuningDevice);
            this.tunings.put(port, this.provider.loadTuningForDevice(defaultTuningDevice));
        }
        this.isDapOn = this.provider.isDapOn();
        this.observable.onLoad(this);
        if (this.isDapOn) {
            AudioSystem.setParameters("dolby_init=true");
            return;
        }
        AudioSystem.setParameters("dolby_init=false");
    }
    
    @Override
    public void registerObserver(final OnDsContextChange onDsContextChange) {
        this.observable.registerObserver(onDsContextChange);
        onDsContextChange.onLoad(this);
    }
    
    @Override
    public void reloadAllProfile() {
        final ProfileType selectedProfile = this.provider.getSelectedProfile();
        for (final ProfileContext selectedContext : this.profiles) {
            selectedContext.load();
            if (selectedContext.getType() == selectedProfile) {
                this.selectedContext = selectedContext;
            }
        }
        this.observable.onSelectedProfileChanged(this);
        this.isDapOn = this.provider.isDapOn();
        this.observable.onDapOnChanged(this, this.isDapOn);
    }
    
    @Override
    public void resetProfile(final ProfileType profileType) {
        this.getContext(profileType).reset();
        if (this.isSelected(profileType)) {
            this.observable.onSelectedProfileChanged(this);
        }
    }
    
    @Override
    public void save() {
        this.provider.beginTransaction();
        final Iterator<ProfileContext> iterator = this.profiles.iterator();
        while (iterator.hasNext()) {
            iterator.next().save();
        }
        this.provider.setSelectedProfile(this.selectedContext.getType());
        this.provider.setDapOn(this.isDapOn);
        this.provider.commitTransaction();
    }
    
    @Override
    public void saveDsProfileSettings() {
        this.provider.beginTransaction();
        final Iterator<ProfileContext> iterator = this.profiles.iterator();
        while (iterator.hasNext()) {
            iterator.next().save();
        }
        this.provider.setSelectedProfile(this.selectedContext.getType());
        this.provider.commitTransaction();
    }
    
    @Override
    public void saveDsState() {
        this.provider.beginTransaction();
        this.provider.setDapOn(this.isDapOn);
        this.provider.commitTransaction();
    }
    
    @Override
    public void selectDefaultTuning(final Port port) {
        this.setSelectedTuning(port, this.getDefaultTuningDevice(port));
    }
    
    @Override
    public void setDapOn(final boolean isDapOn) {
        if (this.isDapOn != isDapOn) {
            this.isDapOn = isDapOn;
            this.observable.onDapOnChanged(this, isDapOn);
        }
    }
    
    boolean setParameter(final ParameterValues parameterValues, final Parameter parameter, final int[] array) {
        if (!Arrays.equals(parameterValues.get(parameter), array)) {
            parameterValues.set(parameter, array);
            return true;
        }
        return false;
    }
    
    @Override
    public void setProfileGeq(final ProfileType profileType, final PresetType selectedGeqPreset) {
        final Profile profile = this.getContext(profileType).getProfile();
        if (profile.getSelectedGeqPreset() != selectedGeqPreset) {
            profile.setSelectedGeqPreset(selectedGeqPreset);
            if (this.isSelected(profileType)) {
                this.observable.onSelectedProfileGeqChanged(this, this.selectedContext.getSelectedGeqPreset());
            }
        }
    }
    
    @Override
    public void setProfileGeqParameter(final ProfileType profileType, final Parameter parameter, final int[] array) {
        final GeqPreset selectedGeqPreset = this.getContext(profileType).getSelectedGeqPreset();
        if (this.setParameter(selectedGeqPreset, parameter, array) && this.isSelected(profileType)) {
            this.observable.onSelectedProfileGeqChanged(this, selectedGeqPreset);
        }
    }
    
    @Override
    public void setProfileIeq(final ProfileType profileType, final PresetType selectedIeqPreset) {
        final Profile profile = this.getContext(profileType).getProfile();
        if (profile.getSelectedIeqPreset() != selectedIeqPreset) {
            if (selectedIeqPreset == PresetType.off) {
                this.setProfileParameter(profileType, Parameter.ieq_enable, new int[] { 0 });
            }
            else {
                this.setProfileParameter(profileType, Parameter.ieq_enable, new int[] { 1 });
            }
            profile.setSelectedIeqPreset(selectedIeqPreset);
            if (this.isSelected(profileType)) {
                this.observable.onSelectedProfileIeqChanged(this, this.selectedContext.getSelectedIeqPreset());
            }
        }
    }
    
    @Override
    public void setProfileName(final ProfileType profileType, final String name) {
        final Profile profile = this.getContext(profileType).getProfile();
        profile.setName(name);
        if (this.isSelected(profileType)) {
            this.observable.onSelectedProfileNameChanged(this, profile);
        }
    }
    
    @Override
    public void setProfileParameter(final ProfileType profileType, final Endpoint endpoint, final Parameter parameter, final int[] array) {
        final ProfileEndpoint profileEndpoint = this.getContext(profileType).getProfileEndpoint(endpoint);
        if (this.setParameter(profileEndpoint, parameter, array) && this.isSelected(profileType)) {
            for (final Map.Entry<Port, Tuning> entry : this.tunings.entrySet()) {
                if (entry.getValue().getEndpoint() == endpoint) {
                    this.observable.onSelectedProfileEndpointChanged(this, entry.getKey(), profileEndpoint);
                }
            }
        }
    }
    
    @Override
    public void setProfileParameter(final ProfileType profileType, final Parameter parameter, final int[] array) {
        final Profile profile = this.getContext(profileType).getProfile();
        if (this.setParameter(profile, parameter, array) && this.isSelected(profileType)) {
            this.observable.onSelectedProfileParameterChanged(this, profile);
        }
    }
    
    @Override
    public void setSelectedProfile(final ProfileType profileType) {
        if (this.selectedContext.getType() != profileType) {
            this.selectedContext = this.getContext(profileType);
            this.observable.onSelectedProfileChanged(this);
        }
    }
    
    @Override
    public boolean setSelectedTuning(final Port port, final String s) {
        if (s.equals(this.devices.get(port))) {
            return true;
        }
        final Tuning loadTuningForDevice = this.provider.loadTuningForDevice(s);
        if (loadTuningForDevice == null) {
            return false;
        }
        this.devices.put(port, s);
        final Tuning tuning = this.tunings.put(port, loadTuningForDevice);
        if (tuning != null && tuning.getName().equals(loadTuningForDevice.getName())) {
            return true;
        }
        this.observable.onSelectedTuningChanged(this, port, loadTuningForDevice);
        return true;
    }
}
