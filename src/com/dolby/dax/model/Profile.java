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

package com.dolby.dax.model;

import com.google.common.collect.Sets;
import java.util.Set;

public class Profile extends ParameterValues implements Model
{
    public static final Set<Parameter> validParams;
    protected final long id;
    protected String name;
    protected PresetType selectedGeqPreset;
    protected PresetType selectedIeqPreset;
    protected final ProfileType type;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.audio_optimizer_enable, Parameter.bass_enhancer_enable, Parameter.bass_extraction_enable, Parameter.graphic_equalizer_enable, Parameter.ieq_amount, Parameter.ieq_enable, Parameter.mi_dialog_enhancer_steering_enable, Parameter.mi_dv_leveler_steering_enable, Parameter.mi_ieq_steering_enable, Parameter.mi_surround_compressor_steering_enable, Parameter.process_optimizer_enable, Parameter.regulator_enable, Parameter.regulator_speaker_dist_enable, Parameter.surround_decoder_enable, Parameter.virtual_bass_enable, Parameter.virtualizer_headphone_reverb_gain, Parameter.volume_modeler_enable);
    }
    
    public Profile(final long id, final ProfileType type, final String name, final PresetType selectedIeqPreset, final PresetType selectedGeqPreset) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.selectedIeqPreset = selectedIeqPreset;
        this.selectedGeqPreset = selectedGeqPreset;
    }
    
    public Profile(final ProfileType type, final String name, final PresetType selectedIeqPreset, final PresetType selectedGeqPreset) {
        this.id = -1L;
        this.type = type;
        this.name = name;
        this.selectedIeqPreset = selectedIeqPreset;
        this.selectedGeqPreset = selectedGeqPreset;
        this.dirty = true;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        final Profile profile = (Profile)o;
        boolean equals = b;
        if (this.selectedGeqPreset == profile.selectedGeqPreset) {
            equals = b;
            if (this.selectedIeqPreset == profile.selectedIeqPreset) {
                equals = b;
                if (this.type == profile.type) {
                    equals = super.equals(o);
                }
            }
        }
        return equals;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public PresetType getSelectedGeqPreset() {
        return this.selectedGeqPreset;
    }
    
    public PresetType getSelectedIeqPreset() {
        return this.selectedIeqPreset;
    }
    
    public ProfileType getType() {
        return this.type;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return Profile.validParams;
    }
    
    @Override
    public int hashCode() {
        return (((super.hashCode() * 31 + this.type.hashCode()) * 31 + this.name.hashCode()) * 31 + this.selectedIeqPreset.hashCode()) * 31 + this.selectedGeqPreset.hashCode();
    }
    
    public void setName(final String name) {
        this.name = name;
        this.dirty = true;
    }
    
    public void setSelectedGeqPreset(final PresetType selectedGeqPreset) {
        this.selectedGeqPreset = selectedGeqPreset;
        this.dirty = true;
    }
    
    public void setSelectedIeqPreset(final PresetType selectedIeqPreset) {
        this.selectedIeqPreset = selectedIeqPreset;
        this.dirty = true;
    }
    
    @Override
    public String toString() {
        return "Profile{id=" + this.id + ", type=" + this.type + ", name='" + this.name + '\'' + ", selectedIeqPreset=" + this.selectedIeqPreset + ", selectedGeqPreset=" + this.selectedGeqPreset + ", " + this.valuesToString() + '}';
    }
}
