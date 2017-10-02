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

import com.dolby.dax.db.Provider;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileType;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class ProfileContext
{
    private Defaults defaults;
    List<ProfileEndpoint> endpoints;
    List<ProfilePort> ports;
    List<GeqPreset> presets;
    Profile profile;
    final ProfileType profileType;
    final Provider provider;
    
    public ProfileContext(final Provider provider, final ProfileType profileType) {
        this.provider = provider;
        this.profileType = profileType;
    }
    
    GeqPreset getGeqPreset(final PresetType presetType) {
        for (final GeqPreset geqPreset : this.presets) {
            if (geqPreset.getType() == presetType) {
                return geqPreset;
            }
        }
        return null;
    }
    
    public Profile getProfile() {
        return this.profile;
    }
    
    public ProfileEndpoint getProfileEndpoint(final Endpoint endpoint) {
        for (final ProfileEndpoint profileEndpoint : this.endpoints) {
            if (profileEndpoint.getEndpoint() == endpoint) {
                return profileEndpoint;
            }
        }
        return null;
    }
    
    ProfilePort getProfilePort(final Port port) {
        for (final ProfilePort profilePort : this.ports) {
            if (profilePort.getPort() == port) {
                return profilePort;
            }
        }
        return null;
    }
    
    public GeqPreset getSelectedGeqPreset() {
        return this.getGeqPreset(this.profile.getSelectedGeqPreset());
    }
    
    public IeqPreset getSelectedIeqPreset() {
        boolean b = false;
        if (this.profile.get(Parameter.ieq_enable)[0] != 0) {
            b = true;
        }
        if (b) {
            return this.provider.loadIeqPreset(this.profile.getSelectedIeqPreset());
        }
        return new IeqPreset(PresetType.off);
    }
    
    ProfileType getType() {
        return this.profileType;
    }
    
    boolean isModified() {
        if (!this.profile.equals(this.defaults.profile)) {
            return true;
        }
        for (final ProfileEndpoint profileEndpoint : this.defaults.endpoints) {
            if (!this.getProfileEndpoint(profileEndpoint.getEndpoint()).equals(profileEndpoint)) {
                return true;
            }
        }
        for (final ProfilePort profilePort : this.defaults.ports) {
            if (!this.getProfilePort(profilePort.getPort()).equals(profilePort)) {
                return true;
            }
        }
        for (final GeqPreset geqPreset : this.defaults.presets) {
            if (!this.getGeqPreset(geqPreset.getType()).equals(geqPreset)) {
                return true;
            }
        }
        return false;
    }
    
    void load() {
        this.profile = this.provider.loadProfile(this.profileType);
        this.endpoints = (List<ProfileEndpoint>)Lists.newArrayList((Iterator<?>)this.provider.loadProfileEndpoints(this.profileType));
        this.ports = (List<ProfilePort>)Lists.newArrayList((Iterator<?>)this.provider.loadProfilePorts(this.profileType));
        this.presets = (List<GeqPreset>)Lists.newArrayList((Iterator<?>)this.provider.loadGeqPresets(this.profileType));
        if (this.defaults == null) {
            this.defaults = new Defaults(this.provider, this.profileType);
        }
    }
    
    void reset() {
        this.provider.beginTransaction();
        this.profile = this.provider.reset(this.profile);
        for (int i = 0; i < this.endpoints.size(); ++i) {
            this.endpoints.set(i, this.provider.reset(this.endpoints.get(i)));
        }
        for (int j = 0; j < this.ports.size(); ++j) {
            this.ports.set(j, this.provider.reset(this.ports.get(j)));
        }
        for (int k = 0; k < this.presets.size(); ++k) {
            this.presets.set(k, this.provider.reset(this.presets.get(k)));
        }
        this.provider.commitTransaction();
    }
    
    void save() {
        this.provider.save(this.profile);
        final Iterator<ProfileEndpoint> iterator = this.endpoints.iterator();
        while (iterator.hasNext()) {
            this.provider.save(iterator.next());
        }
        final Iterator<ProfilePort> iterator2 = this.ports.iterator();
        while (iterator2.hasNext()) {
            this.provider.save(iterator2.next());
        }
        final Iterator<GeqPreset> iterator3 = this.presets.iterator();
        while (iterator3.hasNext()) {
            this.provider.save(iterator3.next());
        }
    }
    
    private static class Defaults
    {
        List<ProfileEndpoint> endpoints;
        List<ProfilePort> ports;
        List<GeqPreset> presets;
        Profile profile;
        
        Defaults(final Provider provider, final ProfileType profileType) {
            this.profile = provider.loadDefaultProfile(profileType);
            this.endpoints = (List<ProfileEndpoint>)Lists.newArrayList((Iterator<?>)provider.loadDefaultProfileEndpoints(profileType));
            this.ports = (List<ProfilePort>)Lists.newArrayList((Iterator<?>)provider.loadDefaultProfilePorts(profileType));
            this.presets = (List<GeqPreset>)Lists.newArrayList((Iterator<?>)provider.loadDefaultGeqPresets(profileType));
        }
    }
}
