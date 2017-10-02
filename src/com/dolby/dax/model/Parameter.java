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

public enum Parameter
{
    audio_optimizer_enable("audio_optimizer_enable", 0), 
    audio_optimizer_frequency("audio_optimizer_frequency", 1), 
    audio_optimizer_gain_c("audio_optimizer_gain_c", 2), 
    audio_optimizer_gain_l("audio_optimizer_gain_l", 3), 
    audio_optimizer_gain_lfe("audio_optimizer_gain_lfe", 4), 
    audio_optimizer_gain_lrs("audio_optimizer_gain_lrs", 5), 
    audio_optimizer_gain_ls("audio_optimizer_gain_ls", 6), 
    audio_optimizer_gain_ltm("audio_optimizer_gain_ltm", 7), 
    audio_optimizer_gain_r("audio_optimizer_gain_r", 8), 
    audio_optimizer_gain_rrs("audio_optimizer_gain_rrs", 9), 
    audio_optimizer_gain_rs("audio_optimizer_gain_rs", 10), 
    audio_optimizer_gain_rtm("audio_optimizer_gain_rtm", 11), 
    bass_enhancer_boost("bass_enhancer_boost", 12), 
    bass_enhancer_cutoff_frequency("bass_enhancer_cutoff_frequency", 13), 
    bass_enhancer_enable("bass_enhancer_enable", 14), 
    bass_enhancer_width("bass_enhancer_width", 15), 
    bass_extraction_cutoff_frequency("bass_extraction_cutoff_frequency", 16), 
    bass_extraction_enable("bass_extraction_enable", 17), 
    calibration_boost("calibration_boost", 18), 
    dialog_enhancer_amount("dialog_enhancer_amount", 19), 
    dialog_enhancer_ducking("dialog_enhancer_ducking", 20), 
    dialog_enhancer_enable("dialog_enhancer_enable", 21), 
    geq_frequency("geq_frequency", 22), 
    geq_gain("geq_gain", 23), 
    graphic_equalizer_enable("graphic_equalizer_enable", 24), 
    height_filter_mode("height_filter_mode", 25), 
    ieq_amount("ieq_amount", 26), 
    ieq_enable("ieq_enable", 27), 
    ieq_frequency("ieq_frequency", 28), 
    ieq_target("ieq_target", 29), 
    mi_dialog_enhancer_steering_enable("mi_dialog_enhancer_steering_enable", 30), 
    mi_dv_leveler_steering_enable("mi_dv_leveler_steering_enable", 31), 
    mi_ieq_steering_enable("mi_ieq_steering_enable", 32), 
    mi_surround_compressor_steering_enable("mi_surround_compressor_steering_enable", 33), 
    output_mix_matrix("output_mix_matrix", 34), 
    process_optimizer_enable("process_optimizer_enable", 35), 
    process_optimizer_frequency("process_optimizer_frequency", 36), 
    process_optimizer_gain_c("process_optimizer_gain_c", 37), 
    process_optimizer_gain_l("process_optimizer_gain_l", 38), 
    process_optimizer_gain_lfe("process_optimizer_gain_lfe", 39), 
    process_optimizer_gain_lrs("process_optimizer_gain_lrs", 40), 
    process_optimizer_gain_ls("process_optimizer_gain_ls", 41), 
    process_optimizer_gain_ltm("process_optimizer_gain_ltm", 42), 
    process_optimizer_gain_r("process_optimizer_gain_r", 43), 
    process_optimizer_gain_rrs("process_optimizer_gain_rrs", 44), 
    process_optimizer_gain_rs("process_optimizer_gain_rs", 45), 
    process_optimizer_gain_rtm("process_optimizer_gain_rtm", 46), 
    regulator_enable("regulator_enable", 47), 
    regulator_frequency("regulator_frequency", 48), 
    regulator_isolated_band("regulator_isolated_band", 49), 
    regulator_overdrive("regulator_overdrive", 50), 
    regulator_relaxation_amount("regulator_relaxation_amount", 51), 
    regulator_speaker_dist_enable("regulator_speaker_dist_enable", 52), 
    regulator_threshold_high("regulator_threshold_high", 53), 
    regulator_threshold_low("regulator_threshold_low", 54), 
    regulator_timbre_preservation("regulator_timbre_preservation", 55), 
    surround_boost("surround_boost", 56), 
    surround_decoder_enable("surround_decoder_enable", 57), 
    virtual_bass_enable("virtual_bass_enable", 58), 
    virtual_bass_mix_freq_high("virtual_bass_mix_freq_high", 59), 
    virtual_bass_mix_freq_low("virtual_bass_mix_freq_low", 60), 
    virtual_bass_mode("virtual_bass_mode", 61), 
    virtual_bass_overall_gain("virtual_bass_overall_gain", 62), 
    virtual_bass_slope_gain("virtual_bass_slope_gain", 63), 
    virtual_bass_src_freq_high("virtual_bass_src_freq_high", 64), 
    virtual_bass_src_freq_low("virtual_bass_src_freq_low", 65), 
    virtual_bass_subgains("virtual_bass_subgains", 66), 
    virtualizer_enable("virtualizer_enable", 67), 
    virtualizer_front_speaker_angle("virtualizer_front_speaker_angle", 68), 
    virtualizer_headphone_reverb_gain("virtualizer_headphone_reverb_gain", 69), 
    virtualizer_height_speaker_angle("virtualizer_height_speaker_angle", 70), 
    virtualizer_speaker_angle("virtualizer_speaker_angle", 71), 
    virtualizer_speaker_start_freq("virtualizer_speaker_start_freq", 72), 
    virtualizer_surround_speaker_angle("virtualizer_surround_speaker_angle", 73), 
    volmax_boost("volmax_boost", 74), 
    volume_leveler_amount("volume_leveler_amount", 75), 
    volume_leveler_enable("volume_leveler_enable", 76), 
    volume_leveler_in_target("volume_leveler_in_target", 77), 
    volume_leveler_out_target("volume_leveler_out_target", 78), 
    volume_modeler_calibration("volume_modeler_calibration", 79), 
    volume_modeler_enable("volume_modeler_enable", 80);
    
    private Parameter(final String s, final int n) {
    }
}
