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

package com.dolby.dax.xml.model;

import com.dolby.dax.model.DeviceTuning;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.Tuning;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;

public class DeviceData
{
    public AuthorizedTechnologies authorizedTechnologies;
    public final List<DeviceTuning> deviceTunings;
    public Version formatVersion;
    public final List<GeqPreset> geqPresets;
    public final List<IeqPreset> ieqPresets;
    public final List<ProfileEndpoint> profileEndpoints;
    public final List<ProfilePort> profilePorts;
    public final List<Profile> profiles;
    private String signature;
    public Version toolVersion;
    public final List<Tuning> tunings;
    
    public DeviceData() {
        this.ieqPresets = new ArrayList<IeqPreset>();
        this.geqPresets = new ArrayList<GeqPreset>();
        this.profiles = new ArrayList<Profile>();
        this.profileEndpoints = new ArrayList<ProfileEndpoint>();
        this.profilePorts = new ArrayList<ProfilePort>();
        this.tunings = new ArrayList<Tuning>();
        this.deviceTunings = new ArrayList<DeviceTuning>();
    }
    
    public String getSignature() {
        return this.signature;
    }
    
    protected String listToString(final Iterable<?> iterable) {
        return "[\n\t" + Joiner.on("\n\t").join(iterable) + "\n]";
    }
    
    public void setSignature(final String signature) {
        this.signature = signature;
    }
    
    @Override
    public String toString() {
        return "DeviceData{\nformatVersion=" + this.formatVersion + "\ntoolVersion=" + this.toolVersion + "\nieqPresets=" + this.listToString(this.ieqPresets) + "\ngeqPresets=" + this.listToString(this.geqPresets) + "\nprofiles=" + this.listToString(this.profiles) + "\nprofileEndpoints=" + this.listToString(this.profileEndpoints) + "\nprofilePorts=" + this.listToString(this.profilePorts) + "\ntunings=" + this.listToString(this.tunings) + "\ndeviceTunings=" + this.listToString(this.deviceTunings) + "\nauthorizedTechnologies=" + this.authorizedTechnologies + "\n}";
    }
}
