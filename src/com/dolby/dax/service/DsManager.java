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

package com.dolby.dax.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioSystem;
import android.os.DeadObjectException;
import android.util.Log;
import com.dolby.api.DsParams;
import com.dolby.dax.dap.Dap;
import com.dolby.dax.dap.DapController;
import com.dolby.dax.dap.DapEffect;
import com.dolby.dax.dap.commands.GetDapVersionCommand;
import com.dolby.dax.dap.commands.GetVisualizerDataCommand;
import com.dolby.dax.dap.commands.GetVisualizerEnableCommand;
import com.dolby.dax.dap.commands.SetVisualizerEnableCommand;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.service.adapter.ConvertEnum;
import com.dolby.dax.service.adapter.ParameterAdapter;
import com.dolby.dax.state.DsContext;
import com.dolby.dax.state.DsContextFactory;
import com.dolby.dax.state.DsContextImpl;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import tripndroid.dolby.audio.api.DsLog;
import tripndroid.dolby.audio.api.DsProfileName;

public class DsManager
{
    private static final GetDapVersionCommand getDapVersionCommand_;
    private static final GetVisualizerDataCommand getVisualizerDataCommand_;
    private static final GetVisualizerEnableCommand getVisualizerEnableCommand_;
    public static final BiMap<Integer, PresetType> mapPresetIdx;
    public static final BiMap<Integer, ProfileType> mapProfileIdx;
    private static final SetVisualizerEnableCommand setVisualizerEnableCommand_;
    private int audioSessionId_;
    private DapController dapController_;
    private Dap dapEffect_;
    private DsContext dsContext_;
    private DsProperty dsProperty_;
    private boolean isDsSuspended_;
    private Context mContext_;
    private ParameterAdapter parameterAdapter_;
    
    static {
        mapProfileIdx = HashBiMap.create();
        mapPresetIdx = HashBiMap.create();
        getVisualizerEnableCommand_ = new GetVisualizerEnableCommand();
        setVisualizerEnableCommand_ = new SetVisualizerEnableCommand();
        getVisualizerDataCommand_ = new GetVisualizerDataCommand();
        getDapVersionCommand_ = new GetDapVersionCommand();
        int n = 0;
        final Iterator iterator = DsContextImpl.SupportedProfiles.iterator();
        while (iterator.hasNext()) {
            DsManager.mapProfileIdx.put(n, (ProfileType)iterator.next());
            ++n;
        }
        final PresetType[] values = PresetType.values();
        for (int i = 0, length = values.length, n2 = 0; i < length; ++i, ++n2) {
            DsManager.mapPresetIdx.put(n2, values[i]);
        }
    }
    
    public DsManager(final Context mContext_) {
        this.isDsSuspended_ = false;
        this.dsContext_ = DsContextFactory.getInstance(mContext_);
        this.dsProperty_ = new DsProperty();
        this.dapEffect_ = this.createDapEffect(0);
        try {
            DsManager.getDapVersionCommand_.execute(this.dapEffect_);
            final String dapVersion = DsManager.getDapVersionCommand_.getDapVersion();
            DsLog.log1("DsManager", "Created DAP effect version [" + dapVersion + "]");
            if (dapVersion != null) {
                DsLog.log1("DsManager", "Created DAP effect version [" + dapVersion + "]");
                if (dapVersion.startsWith("DAP1")) {
                    DsLog.log1("DsManager", "Instantiating DAP1 Controller");
                    this.dapController_ = new DapController(1, this.dapEffect_);
                }
                else {
                    DsLog.log1("DsManager", "Instantiating DAP2 Controller");
                    this.dapController_ = new DapController(2, this.dapEffect_);
                }
                this.dsContext_.registerObserver(this.dsProperty_);
                this.dsContext_.registerObserver(this.dapController_);
                this.parameterAdapter_ = new ParameterAdapter(this.dsContext_);
                this.mContext_ = mContext_;
            }
        }
        catch (Exception ex) {
            Log.e("DsManager", "DsManager() FAILED! Exception");
            ex.printStackTrace();
        }
    }
    
    private Dap createDapEffect(final int audioSessionId_) {
        DsLog.log1("DsManager", "Creating DAP effect on audioSessionId = " + audioSessionId_);
        if (audioSessionId_ < 0) {
            Log.e("DsManager", "DAP effect with specified session Id (" + audioSessionId_ + ") is less than zero");
            throw new IllegalArgumentException("Bad session Id: " + audioSessionId_);
        }
        try {
            this.audioSessionId_ = audioSessionId_;
            return new DapEffect(audioSessionId_);
        }
        catch (Exception ex) {
            Log.e("DsManager", "createDsEffect() FAILED! Exception");
            ex.printStackTrace();
            return null;
        }
    }
    
    private boolean isProfileNameModified(final int n) {
        final SharedPreferences sharedPreferences = this.mContext_.getSharedPreferences("profile_default_name", 0);
        String s = "";
        String s2;
        if (n == 4) {
            s2 = "Custom 1";
        }
        else {
            s2 = "Custom 2";
        }
        if (n == 4) {
            s = sharedPreferences.getString("Custom1DisplayName", "");
            s2 = sharedPreferences.getString("Custom1DefaultName", "Custom 1");
        }
        else if (n == 5) {
            s = sharedPreferences.getString("Custom2DisplayName", "");
            s2 = sharedPreferences.getString("Custom2DefaultName", "Custom 2");
        }
        return !s.equals("") && !s.equals(s2);
    }
    
    private boolean recreateDsEffect() {
        DsLog.log1("DsManager", "recreateDsEffect");
        try {
            if (this.dapEffect_ != null) {
                this.dapEffect_.release();
            }
            this.dapEffect_ = this.createDapEffect(this.audioSessionId_);
            this.dapController_.setDapEffect(this.dapEffect_, this.dsContext_);
            return true;
        }
        catch (Exception ex) {
            Log.e("DsManager", "Exception in recreateDsEffect.", (Throwable)ex);
            return false;
        }
    }
    
    private void setCustomProfileName(final int n, final DsProfileName dsProfileName) {
        final ProfileType profileType = DsManager.mapProfileIdx.get(n);
        final SharedPreferences sharedPreferences = this.mContext_.getSharedPreferences("profile_default_name", 0);
        if (dsProfileName.getDefaultName() != null) {
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            if (n == 4) {
                edit.putString("Custom1DefaultName", dsProfileName.getDefaultName());
            }
            else if (n == 5) {
                edit.putString("Custom2DefaultName", dsProfileName.getDefaultName());
            }
            edit.commit();
        }
        if (dsProfileName.getCurrentName() != null) {
            final SharedPreferences.Editor edit2 = sharedPreferences.edit();
            if (n == 4) {
                edit2.putString("Custom1DisplayName", dsProfileName.getCurrentName());
            }
            else if (n == 5) {
                edit2.putString("Custom2DisplayName", dsProfileName.getCurrentName());
            }
            edit2.commit();
            this.dsContext_.setProfileName(profileType, dsProfileName.getCurrentName());
        }
    }
    
    public String getDapVersion() throws DeadObjectException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        DsManager.getDapVersionCommand_.execute(this.dapEffect_);
        return DsManager.getDapVersionCommand_.getDapVersion();
    }
    
    public boolean getDsOn() throws DeadObjectException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (this.isDsSuspended_) {
            DsLog.log1("DsManager", "DS effect is now suspended");
            return false;
        }
        return this.dsContext_.getDapOn();
    }
    
    public boolean getDsSuspended() throws DeadObjectException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        return this.isDsSuspended_;
    }
    
    public String getDsVersion() {
        return "DAX2.3.1.30_r1";
    }
    
    public int getIeqPreset(final int n) throws IllegalArgumentException {
        int intValue = -1;
        if (DsManager.mapProfileIdx.containsKey(n)) {
            intValue = DsManager.mapPresetIdx.inverse().get(this.dsContext_.getProfileIeq(DsManager.mapProfileIdx.get(n)).getType());
        }
        return intValue;
    }
    
    public int getOffType() throws DeadObjectException {
        return 0;
    }
    
    public int getParamLength(final int n) {
        final DsParams fromInt = DsParams.FromInt(n);
        if (fromInt == null) {
            return 0;
        }
        return this.parameterAdapter_.getParameterLength(fromInt);
    }
    
    public int[] getParameter(final int n, final int n2) {
        int[] parameter = null;
        if (DsManager.mapProfileIdx.containsKey(n)) {
            parameter = this.parameterAdapter_.getParameter(DsManager.mapProfileIdx.get(n), n2);
        }
        return parameter;
    }
    
    public int getProfileCount() {
        return 6;
    }
    
    public int[] getProfileEndpointParameter(final String s, final String s2, final String s3) {
        final Parameter parameter = ConvertEnum.parameter(s3);
        final Endpoint endpoint = ConvertEnum.endpoint(s2);
        final ProfileType supportedProfile = ConvertEnum.supportedProfile(s);
        if (parameter != null && endpoint != null && supportedProfile != null) {
            return this.dsContext_.getProfileParameter(supportedProfile, endpoint, parameter);
        }
        return null;
    }
    
    public DsProfileName getProfileName(final int n) throws IllegalArgumentException {
        final DsProfileName dsProfileName = new DsProfileName();
        if (!DsManager.mapProfileIdx.containsKey(n)) {
            Log.e("DsManager", "getProfileName: Invalid profile input");
            throw new IllegalArgumentException();
        }
        final ProfileType profileType = DsManager.mapProfileIdx.get(n);
        if (n >= 4 && n < 6) {
            final SharedPreferences sharedPreferences = this.mContext_.getSharedPreferences("profile_default_name", 0);
            if (n == 4) {
                final String string = sharedPreferences.getString("Custom1DisplayName", "");
                if (string.equals("")) {
                    dsProfileName.setCurrentName(sharedPreferences.getString("Custom1DefaultName", "Custom 1"));
                }
                else {
                    dsProfileName.setCurrentName(string);
                }
                dsProfileName.setDefaultName(sharedPreferences.getString("Custom1DefaultName", "Custom 1"));
            }
            else if (n == 5) {
                final String string2 = sharedPreferences.getString("Custom2DisplayName", "");
                if (string2.equals("")) {
                    dsProfileName.setCurrentName(sharedPreferences.getString("Custom2DefaultName", "Custom 2"));
                }
                else {
                    dsProfileName.setCurrentName(string2);
                }
                dsProfileName.setDefaultName(sharedPreferences.getString("Custom2DefaultName", "Custom 2"));
                return dsProfileName;
            }
            return dsProfileName;
        }
        dsProfileName.setCurrentName(this.dsContext_.getProfile(profileType).getName());
        dsProfileName.setDefaultName(this.dsContext_.getDefaultProfile(profileType).getName());
        return dsProfileName;
    }
    
    public int[] getProfileParameter(final String s, final String s2) {
        final Parameter parameter = ConvertEnum.parameter(s2);
        final ProfileType supportedProfile = ConvertEnum.supportedProfile(s);
        if (parameter != null && supportedProfile != null) {
            return this.dsContext_.getProfileParameter(supportedProfile, parameter);
        }
        return null;
    }
    
    public int[] getProfilePortParameter(final String s, final String s2, final String s3) {
        final Parameter parameter = ConvertEnum.parameter(s3);
        final ProfileType supportedProfile = ConvertEnum.supportedProfile(s);
        final Port port = ConvertEnum.port(s2);
        if (parameter != null && supportedProfile != null && port != null) {
            return this.dsContext_.getProfileParameter(supportedProfile, port, parameter);
        }
        return null;
    }
    
    public int getSelectedProfile() {
        return DsManager.mapProfileIdx.inverse().get(this.dsContext_.getSelectedProfile().getType());
    }
    
    public int[] getTuningParameter(final String s, final String s2) {
        final Parameter parameter = ConvertEnum.parameter(s2);
        final Port port = ConvertEnum.port(s);
        if (port != null && parameter != null) {
            return this.dsContext_.getSelectedTuningParameter(port, parameter);
        }
        return null;
    }
    
    public int getVisualizerData(final float[] array, final float[] array2) throws DeadObjectException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (DsManager.getVisualizerDataCommand_.execute(this.dapEffect_) != 0) {
            return 0;
        }
        System.arraycopy(DsManager.getVisualizerDataCommand_.getGains(), 0, array, 0, array.length);
        System.arraycopy(DsManager.getVisualizerDataCommand_.getExcitations(), 0, array2, 0, array2.length);
        return array.length + array2.length;
    }
    
    public boolean isMonoInternalSpeaker() {
        return this.dsContext_.getSelectedTuning(Port.internal_speaker).isMono();
    }
    
    public boolean isProfileModified(final int n) {
        if (DsManager.mapProfileIdx.containsKey(n)) {
            final ProfileType profileType = DsManager.mapProfileIdx.get(n);
            boolean profileNameModified = false;
            if (n >= 4) {
                profileNameModified = profileNameModified;
                if (n < 6) {
                    profileNameModified = this.isProfileNameModified(n);
                }
            }
            if (this.dsContext_.isProfileModified(profileType) || profileNameModified) {
                return true;
            }
        }
        return false;
    }
    
    public boolean resetProfile(final int n) throws DeadObjectException, UnsupportedOperationException, IllegalArgumentException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (DsManager.mapProfileIdx.containsKey(n)) {
            this.dsContext_.resetProfile(DsManager.mapProfileIdx.get(n));
            if (n >= 4 && n < 6) {
                final SharedPreferences.Editor edit = this.mContext_.getSharedPreferences("profile_default_name", 0).edit();
                if (n == 4) {
                    edit.putString("Custom1DisplayName", "");
                }
                else if (n == 5) {
                    edit.putString("Custom2DisplayName", "");
                }
                edit.commit();
            }
            return true;
        }
        return false;
    }
    
    public void restoreCurrentProfiles() {
        DsLog.log1("DsManager", "Ds restoreCurrentProfiles");
        this.dsContext_.reloadAllProfile();
    }
    
    public void saveDsProfileSettings() {
        DsLog.log1("DsManager", "saveDsProfileSettings");
        this.dsContext_.saveDsProfileSettings();
    }
    
    public void saveDsState() {
        DsLog.log1("DsManager", "saveDsState");
        this.dsContext_.saveDsState();
    }
    
    public void saveDsStateAndSettings() {
        DsLog.log1("DsManager", "saveDsStateAndSettings");
        this.dsContext_.save();
    }
    
    public void setDsOn(final boolean dapOn) throws DeadObjectException {
        DsLog.log1("DsManager", "setDsOn: \"" + dapOn + "\"");
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        DsLog.log1("DsManager", "Ds.setDsOn(" + dapOn + ")");
        if (dapOn) {
            AudioSystem.setParameters("dolby_power=true");
        }
        else if (!dapOn) {
            AudioSystem.setParameters("dolby_power=false");
        }
        this.dsContext_.setDapOn(dapOn);
    }
    
    public void setDsSuspended(final boolean isDsSuspended_) throws DeadObjectException {
        DsLog.log1("DsManager", "setDsSuspended(" + isDsSuspended_ + ")");
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (this.isDsSuspended_ != isDsSuspended_) {
            this.isDsSuspended_ = isDsSuspended_;
            if (this.dsContext_.getDapOn()) {
                this.dsProperty_.setStateProperty(!isDsSuspended_);
            }
        }
    }
    
    public boolean setIeqPreset(final int n, final int n2) throws DeadObjectException, IllegalArgumentException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (DsManager.mapProfileIdx.containsKey(n) && DsManager.mapPresetIdx.containsKey(n2)) {
            final ProfileType profileType = DsManager.mapProfileIdx.get(n);
            final PresetType presetType = DsManager.mapPresetIdx.get(n2);
            this.dsContext_.setProfileIeq(profileType, presetType);
            this.dsContext_.setProfileGeq(profileType, presetType);
            return true;
        }
        return false;
    }
    
    public boolean setParameter(final int n, final int n2, final int[] array) throws DeadObjectException, UnsupportedOperationException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (DsManager.mapProfileIdx.containsKey(n)) {
            this.parameterAdapter_.setParameter(DsManager.mapProfileIdx.get(n), n2, array);
        }
        return true;
    }
    
    public boolean setProfileName(final int n, final DsProfileName dsProfileName) throws UnsupportedOperationException, IllegalArgumentException {
        boolean b = false;
        if (DsManager.mapProfileIdx.containsKey(n)) {
            if (dsProfileName != null) {
                if (dsProfileName.getDefaultName() != null && (dsProfileName.getDefaultName().length() > 20 || dsProfileName.getDefaultName().length() == 0)) {
                    Log.e("DsManager", "setProfileName: Invalid profile default name length input");
                    throw new IllegalArgumentException();
                }
                if (dsProfileName.getCurrentName() != null && (dsProfileName.getCurrentName().length() > 20 || dsProfileName.getCurrentName().length() == 0)) {
                    Log.e("DsManager", "setProfileName: Invalid profile current name length input");
                    throw new IllegalArgumentException();
                }
                if (n < 4 || n >= 6) {
                    Log.e("DsManager", "setProfileName: Name of this profile is not settable");
                    throw new UnsupportedOperationException();
                }
                this.setCustomProfileName(n, dsProfileName);
                b = true;
            }
            return b;
        }
        Log.e("DsManager", "setProfileName: Invalid profile input");
        throw new IllegalArgumentException();
    }
    
    public boolean setSelectedProfile(final int n) throws DeadObjectException, IllegalArgumentException {
        if (!this.validateDsEffect()) {
            throw new DeadObjectException();
        }
        if (DsManager.mapProfileIdx.containsKey(n)) {
            this.dsContext_.setSelectedProfile(DsManager.mapProfileIdx.get(n));
            return true;
        }
        return false;
    }
    
    public int setVisualizerOn(final boolean enabled) throws DeadObjectException {
        DsManager.setVisualizerEnableCommand_.setEnabled(enabled);
        return DsManager.setVisualizerEnableCommand_.execute(this.dapEffect_);
    }
    
    public boolean validateDsEffect() {
        boolean b;
        if (!(b = this.dapEffect_.hasControl())) {
            Log.e("DsManager", "Cannot control the DsEffect, trying to recreate...");
            b = this.recreateDsEffect();
        }
        return b;
    }
}
