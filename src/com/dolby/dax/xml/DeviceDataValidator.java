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

package com.dolby.dax.xml;

import android.util.Log;
import com.dolby.dax.model.DeviceTuning;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.xml.model.DeviceData;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DeviceDataValidator
{
    final DeviceData data;
    
    public DeviceDataValidator(final DeviceData data) {
        this.data = data;
    }
    
    private void validateIeqPreset(final IeqPreset ieqPreset) throws ValidationException {
        final int[] value = ieqPreset.get(Parameter.ieq_frequency);
        final int[] value2 = ieqPreset.get(Parameter.ieq_target);
        if (value.length != value2.length) {
            throw new ValidationException("IEQ preset " + ieqPreset.getType() + " has different " + "number of values for frequencies (" + value.length + ") and targets (" + value2.length + ")");
        }
    }
    
    private void validateProfile(final Profile profile) throws ValidationException {
        boolean b = false;
        if (profile.get(Parameter.ieq_enable)[0] != 0) {
            b = true;
        }
        if (!b) {
            profile.setSelectedIeqPreset(PresetType.off);
        }
        if (profile.getSelectedGeqPreset() != profile.getSelectedIeqPreset()) {
            Log.w("DeviceDataValidator", "Profile " + profile.getType() + " has selected different ieq (" + profile.getSelectedIeqPreset() + ") and geq (" + profile.getSelectedGeqPreset() + ") presets. Setting geq to " + profile.getSelectedIeqPreset());
            profile.setSelectedGeqPreset(profile.getSelectedIeqPreset());
        }
        for (final Map.Entry<Parameter, int[]> entry : profile.entrySet()) {
            if (entry.getValue().length == 0) {
                throw new ValidationException("Profile " + profile.getType() + " has parameter " + entry.getKey() + " with no values");
            }
        }
    }
    
    private void validateTuning(final Tuning tuning) throws ValidationException {
        for (final Map.Entry<Parameter, int[]> entry : tuning.entrySet()) {
            if ((entry.getKey() != Parameter.output_mix_matrix || (tuning.getEndpoint() == Endpoint.speaker && tuning.get(Parameter.virtualizer_enable)[0] != 0)) && entry.getValue().length == 0) {
                throw new ValidationException("Tuning " + tuning.getName() + " has parameter " + entry.getKey() + " with no values");
            }
        }
    }
    
    public void validate() throws ValidationException {
        final Iterator<IeqPreset> iterator = this.data.ieqPresets.iterator();
        while (iterator.hasNext()) {
            this.validateIeqPreset(iterator.next());
        }
        final Iterator<Profile> iterator2 = this.data.profiles.iterator();
        while (iterator2.hasNext()) {
            this.validateProfile(iterator2.next());
        }
        final HashSet<String> set = new HashSet<String>();
        for (final Tuning tuning : this.data.tunings) {
            this.validateTuning(tuning);
            if (!set.add(tuning.getName())) {
                throw new ValidationException("Tuning " + tuning.getName() + " is not unique");
            }
        }
        final HashSet<String> set2 = new HashSet<String>();
        for (final DeviceTuning deviceTuning : this.data.deviceTunings) {
            if (!set2.add(deviceTuning.getDevice())) {
                throw new ValidationException("Device " + deviceTuning.getDevice() + " is not unique.");
            }
        }
    }
}
