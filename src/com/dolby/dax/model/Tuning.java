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

public class Tuning extends ParameterValues implements Model
{
    public static final Set<Parameter> validParams;
    protected final Endpoint endpoint;
    protected final long id;
    protected final boolean mono;
    protected final String name;
    protected final boolean readonly;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.audio_optimizer_enable, Parameter.audio_optimizer_frequency, Parameter.audio_optimizer_gain_c, Parameter.audio_optimizer_gain_l, Parameter.audio_optimizer_gain_lfe, Parameter.audio_optimizer_gain_lrs, Parameter.audio_optimizer_gain_ls, Parameter.audio_optimizer_gain_ltm, Parameter.audio_optimizer_gain_r, Parameter.audio_optimizer_gain_rrs, Parameter.audio_optimizer_gain_rs, Parameter.audio_optimizer_gain_rtm, Parameter.bass_enhancer_boost, Parameter.bass_enhancer_cutoff_frequency, Parameter.bass_enhancer_enable, Parameter.bass_enhancer_width, Parameter.bass_extraction_enable, Parameter.bass_extraction_cutoff_frequency, Parameter.height_filter_mode, Parameter.output_mix_matrix, Parameter.process_optimizer_enable, Parameter.process_optimizer_frequency, Parameter.process_optimizer_gain_c, Parameter.process_optimizer_gain_l, Parameter.process_optimizer_gain_lfe, Parameter.process_optimizer_gain_lrs, Parameter.process_optimizer_gain_ls, Parameter.process_optimizer_gain_ltm, Parameter.process_optimizer_gain_r, Parameter.process_optimizer_gain_rrs, Parameter.process_optimizer_gain_rs, Parameter.process_optimizer_gain_rtm, Parameter.regulator_enable, Parameter.regulator_frequency, Parameter.regulator_isolated_band, Parameter.regulator_overdrive, Parameter.regulator_relaxation_amount, Parameter.regulator_speaker_dist_enable, Parameter.regulator_threshold_high, Parameter.regulator_threshold_low, Parameter.regulator_timbre_preservation, Parameter.surround_decoder_enable, Parameter.virtual_bass_enable, Parameter.virtual_bass_mix_freq_high, Parameter.virtual_bass_mix_freq_low, Parameter.virtual_bass_mode, Parameter.virtual_bass_overall_gain, Parameter.virtual_bass_slope_gain, Parameter.virtual_bass_src_freq_high, Parameter.virtual_bass_src_freq_low, Parameter.virtual_bass_subgains, Parameter.virtualizer_enable, Parameter.virtualizer_front_speaker_angle, Parameter.virtualizer_height_speaker_angle, Parameter.virtualizer_speaker_angle, Parameter.virtualizer_speaker_start_freq, Parameter.virtualizer_surround_speaker_angle, Parameter.volume_modeler_calibration, Parameter.volume_modeler_enable);
    }
    
    public Tuning(final long id, final String name, final Endpoint endpoint, final boolean mono, final boolean readonly) {
        this.id = id;
        this.name = name;
        this.endpoint = endpoint;
        this.mono = mono;
        this.readonly = readonly;
    }
    
    public Tuning(final String name, final Endpoint endpoint, final boolean mono) {
        this.id = -1L;
        this.name = name;
        this.endpoint = endpoint;
        this.mono = mono;
        this.readonly = true;
        this.dirty = true;
    }
    
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return Tuning.validParams;
    }
    
    public boolean isMono() {
        return this.mono;
    }
    
    public boolean isReadonly() {
        return this.readonly;
    }
    
    @Override
    public String toString() {
        return "Tuning{id=" + this.id + ", name='" + this.name + '\'' + ", endpoint=" + this.endpoint + ", mono=" + this.mono + ", readonly=" + this.readonly + ", " + this.valuesToString() + '}';
    }
}
