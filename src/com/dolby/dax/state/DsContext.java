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
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.model.Port;
import com.google.common.collect.Sets;
import java.util.Set;

public interface DsContext
{
    public static final Set<Port> SupportedPorts = Sets.immutableEnumSet(Port.internal_speaker, Port.headphone_port, Port.bluetooth, Port.hdmi, Port.miracast, Port.usb);
    public static final Set<ProfileType> SupportedProfiles = Sets.immutableEnumSet(ProfileType.movie, ProfileType.music, ProfileType.game, ProfileType.voice, ProfileType.user1, ProfileType.user2, ProfileType.off);
    
    boolean getDapOn();
    
    Profile getDefaultProfile(final ProfileType p0);
    
    String getDefaultTuningDevice(final Port p0);
    
    Profile getProfile(final ProfileType p0);
    
    ProfileEndpoint getProfileEndpoint(final ProfileType p0, final Endpoint p1);
    
    int[] getProfileGeqParameter(final ProfileType p0, final Parameter p1);
    
    IeqPreset getProfileIeq(final ProfileType p0);
    
    int[] getProfileParameter(final ProfileType p0, final Endpoint p1, final Parameter p2);
    
    int[] getProfileParameter(final ProfileType p0, final Parameter p1);
    
    int[] getProfileParameter(final ProfileType p0, final Port p1, final Parameter p2);
    
    Provider getProvider();
    
    Profile getSelectedProfile();
    
    ProfileEndpoint getSelectedProfileEndpoint(final Port p0);
    
    GeqPreset getSelectedProfileGeq();
    
    IeqPreset getSelectedProfileIeq();
    
    ProfilePort getSelectedProfilePort(final Port p0);
    
    Tuning getSelectedTuning(final Port p0);
    
    String getSelectedTuningDevice(final Port p0);
    
    int[] getSelectedTuningParameter(final Port p0, final Parameter p1);
    
    boolean isProfileModified(final ProfileType p0);
    
    void load();
    
    void registerObserver(final OnDsContextChange p0);
    
    void reloadAllProfile();
    
    void resetProfile(final ProfileType p0);
    
    void save();
    
    void saveDsProfileSettings();
    
    void saveDsState();
    
    void selectDefaultTuning(final Port p0);
    
    void setDapOn(final boolean p0);
    
    void setProfileGeq(final ProfileType p0, final PresetType p1);
    
    void setProfileGeqParameter(final ProfileType p0, final Parameter p1, final int[] p2);
    
    void setProfileIeq(final ProfileType p0, final PresetType p1);
    
    void setProfileName(final ProfileType p0, final String p1);
    
    void setProfileParameter(final ProfileType p0, final Endpoint p1, final Parameter p2, final int[] p3);
    
    void setProfileParameter(final ProfileType p0, final Parameter p1, final int[] p2);
    
    void setSelectedProfile(final ProfileType p0);
    
    boolean setSelectedTuning(final Port p0, final String p1);
}
