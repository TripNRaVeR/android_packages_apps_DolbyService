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

package com.dolby.dax.dap;

import com.dolby.dax.dap.translator.ParamChangeObserver;
import com.dolby.dax.dap.translator.ParameterCombinationStrategy;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.Tuning;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceParameters extends ParameterValues
{
    public static final Set<Parameter> validParams;
    ParamChangeObserver paramChangeObserver;
    Profile profile;
    ProfileEndpoint profileEndpoint;
    ProfilePort profilePort;
    Tuning tuning;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.audio_optimizer_enable, Parameter.audio_optimizer_frequency, Parameter.audio_optimizer_gain_c, Parameter.audio_optimizer_gain_l, Parameter.audio_optimizer_gain_lfe, Parameter.audio_optimizer_gain_lrs, Parameter.audio_optimizer_gain_ls, Parameter.audio_optimizer_gain_ltm, Parameter.audio_optimizer_gain_r, Parameter.audio_optimizer_gain_rrs, Parameter.audio_optimizer_gain_rs, Parameter.audio_optimizer_gain_rtm, Parameter.bass_enhancer_boost, Parameter.bass_enhancer_cutoff_frequency, Parameter.bass_enhancer_enable, Parameter.bass_enhancer_width, Parameter.bass_extraction_cutoff_frequency, Parameter.bass_extraction_enable, Parameter.calibration_boost, Parameter.dialog_enhancer_amount, Parameter.dialog_enhancer_ducking, Parameter.dialog_enhancer_enable, Parameter.height_filter_mode, Parameter.output_mix_matrix, Parameter.process_optimizer_enable, Parameter.process_optimizer_frequency, Parameter.process_optimizer_gain_c, Parameter.process_optimizer_gain_l, Parameter.process_optimizer_gain_lfe, Parameter.process_optimizer_gain_lrs, Parameter.process_optimizer_gain_ls, Parameter.process_optimizer_gain_ltm, Parameter.process_optimizer_gain_r, Parameter.process_optimizer_gain_rrs, Parameter.process_optimizer_gain_rs, Parameter.process_optimizer_gain_rtm, Parameter.regulator_enable, Parameter.regulator_frequency, Parameter.regulator_isolated_band, Parameter.regulator_overdrive, Parameter.regulator_relaxation_amount, Parameter.regulator_speaker_dist_enable, Parameter.regulator_threshold_high, Parameter.regulator_threshold_low, Parameter.regulator_timbre_preservation, Parameter.surround_boost, Parameter.surround_decoder_enable, Parameter.virtual_bass_enable, Parameter.virtual_bass_mix_freq_high, Parameter.virtual_bass_mix_freq_low, Parameter.virtual_bass_mode, Parameter.virtual_bass_overall_gain, Parameter.virtual_bass_slope_gain, Parameter.virtual_bass_src_freq_high, Parameter.virtual_bass_src_freq_low, Parameter.virtual_bass_subgains, Parameter.virtualizer_enable, Parameter.virtualizer_front_speaker_angle, Parameter.virtualizer_height_speaker_angle, Parameter.virtualizer_speaker_angle, Parameter.virtualizer_speaker_start_freq, Parameter.virtualizer_surround_speaker_angle, Parameter.volmax_boost, Parameter.volume_leveler_amount, Parameter.volume_leveler_enable, Parameter.volume_leveler_in_target, Parameter.volume_leveler_out_target, Parameter.volume_modeler_calibration, Parameter.volume_modeler_enable);
    }
    
    public DeviceParameters(final ParamChangeObserver paramChangeObserver) {
        (this.paramChangeObserver = paramChangeObserver).setSource(this, null);
    }
    
    public Profile getProfile() {
        return this.profile;
    }
    
    public ProfileEndpoint getProfileEndpoint() {
        return this.profileEndpoint;
    }
    
    public Tuning getTuning() {
        return this.tuning;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return DeviceParameters.validParams;
    }
    
    public void setProfile(final Profile profile) {
        this.updateParams(this.profile = profile, ParameterCombinationStrategy.Profile);
    }
    
    public void setProfileEndpoint(final ProfileEndpoint profileEndpoint) {
        this.updateParams(this.profileEndpoint = profileEndpoint, ParameterCombinationStrategy.ProfileEndpoint);
    }
    
    public void setProfilePort(final ProfilePort profilePort) {
        this.updateParams(this.profilePort = profilePort, ParameterCombinationStrategy.Null);
    }
    
    public void setTuning(final Tuning tuning) {
        this.tuning = tuning;
        this.paramChangeObserver.setSource(this, tuning.getEndpoint());
        this.updateParams(tuning, ParameterCombinationStrategy.Tuning);
    }
    
    protected void updateParams(final ParameterValues parameterValues, final ParameterCombinationStrategy parameterCombinationStrategy) {
        for (final Map.Entry<Parameter, int[]> entry : parameterValues.entrySet()) {
            final Parameter parameter = entry.getKey();
            if (this.checkAndUpdate(parameter, parameterCombinationStrategy.getValue(this, parameter, entry.getValue()))) {
                this.paramChangeObserver.onParameterChanged(parameter);
            }
        }
    }
}
