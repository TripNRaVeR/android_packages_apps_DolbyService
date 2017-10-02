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

import com.dolby.dax.model.DeviceTuning;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.Tuning;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TuningParser
{
    private final List<DeviceTuning> deviceTunings;
    private final TagIterator ti;
    private final List<Tuning> tunings;
    
    public TuningParser(final TagIterator ti, final List<Tuning> tunings, final List<DeviceTuning> deviceTunings) {
        this.ti = ti;
        this.tunings = tunings;
        this.deviceTunings = deviceTunings;
    }
    
    private void parseTuningDevices(final String s) throws ValidationException {
        while (this.ti.atStartTag("device_id")) {
            final DeviceTuning deviceTuning = new DeviceTuning(this.ti.getStringAttribute("id"), this.ti.getStringAttribute("endpoint_port"), s);
            this.ti.next();
            this.ti.consumeEndTag("device_id");
            this.deviceTunings.add(deviceTuning);
        }
    }
    
    private int[][] parseTuningOptimizerBands(final Tuning tuning, final String s) throws ValidationException {
        this.ti.consumeStartTag(s);
        final ArrayList<Integer> list = new ArrayList<Integer>(20);
        final ArrayList<Integer> list2 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list3 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list4 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list5 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list6 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list7 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list8 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list9 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list10 = new ArrayList<Integer>(20);
        final ArrayList<Integer> list11 = new ArrayList<Integer>(20);
        while (this.ti.atStartTag("band_optimizer")) {
            list.add(this.ti.getIntAttribute("frequency"));
            list2.add(this.ti.getIntAttribute("gain_left"));
            list3.add(this.ti.getIntAttribute("gain_right"));
            list4.add(this.ti.getIntAttribute("gain_center"));
            list5.add(this.ti.getIntAttribute("gain_lfe"));
            list6.add(this.ti.getIntAttribute("gain_left_surround"));
            list7.add(this.ti.getIntAttribute("gain_right_surround"));
            list8.add(this.ti.getIntAttribute("gain_left_rear_surround"));
            list9.add(this.ti.getIntAttribute("gain_right_rear_surround"));
            list10.add(this.ti.getIntAttribute("gain_left_top_middle"));
            list11.add(this.ti.getIntAttribute("gain_right_top_middle"));
            this.ti.next();
            this.ti.consumeEndTag("band_optimizer");
        }
        this.ti.consumeEndTag(s);
        if (list.size() > 20) {
            throw new ValidationException(this.ti, String.format("Audio optimizer band count %d is higher than maximum %d", list.size(), 20));
        }
        return new int[][] { ParserUtils.asIntArray(list), ParserUtils.asIntArray(list2), ParserUtils.asIntArray(list3), ParserUtils.asIntArray(list4), ParserUtils.asIntArray(list5), ParserUtils.asIntArray(list6), ParserUtils.asIntArray(list7), ParserUtils.asIntArray(list8), ParserUtils.asIntArray(list9), ParserUtils.asIntArray(list10), ParserUtils.asIntArray(list11) };
    }
    
    private void parseTuningOutputMatrix(final Tuning tuning) throws ValidationException {
        this.ti.consumeStartTag("intermediate_tuning_partial_output-mode");
        this.ti.consumeIntValueTag("output_channels");
        this.ti.consumeStartTag("mix_matrix");
        final ArrayList<Integer> list = new ArrayList<Integer>();
        while (this.ti.atStartTag("element")) {
            list.add(this.ti.consumeIntValueTag("element"));
        }
        this.ti.consumeEndTag("mix_matrix");
        this.ti.consumeEndTag("intermediate_tuning_partial_output-mode");
        tuning.set(Parameter.output_mix_matrix, ParserUtils.asIntArray(list));
    }
    
    private void parseTuningParameters(final Tuning tuning) throws ValidationException {
        this.ti.consumeStartTag("data");
        final int[][] tuningOptimizerBands = this.parseTuningOptimizerBands(tuning, "audio-optimizer-bands");
        tuning.set(Parameter.audio_optimizer_frequency, tuningOptimizerBands[0]);
        tuning.set(Parameter.audio_optimizer_gain_l, tuningOptimizerBands[1]);
        tuning.set(Parameter.audio_optimizer_gain_r, tuningOptimizerBands[2]);
        tuning.set(Parameter.audio_optimizer_gain_c, tuningOptimizerBands[3]);
        tuning.set(Parameter.audio_optimizer_gain_lfe, tuningOptimizerBands[4]);
        tuning.set(Parameter.audio_optimizer_gain_ls, tuningOptimizerBands[5]);
        tuning.set(Parameter.audio_optimizer_gain_rs, tuningOptimizerBands[6]);
        tuning.set(Parameter.audio_optimizer_gain_lrs, tuningOptimizerBands[7]);
        tuning.set(Parameter.audio_optimizer_gain_rrs, tuningOptimizerBands[8]);
        tuning.set(Parameter.audio_optimizer_gain_ltm, tuningOptimizerBands[9]);
        tuning.set(Parameter.audio_optimizer_gain_rtm, tuningOptimizerBands[10]);
        final int[][] tuningOptimizerBands2 = this.parseTuningOptimizerBands(tuning, "process-optimizer-bands");
        tuning.set(Parameter.process_optimizer_frequency, tuningOptimizerBands2[0]);
        tuning.set(Parameter.process_optimizer_gain_l, tuningOptimizerBands2[1]);
        tuning.set(Parameter.process_optimizer_gain_r, tuningOptimizerBands2[2]);
        tuning.set(Parameter.process_optimizer_gain_c, tuningOptimizerBands2[3]);
        tuning.set(Parameter.process_optimizer_gain_lfe, tuningOptimizerBands2[4]);
        tuning.set(Parameter.process_optimizer_gain_ls, tuningOptimizerBands2[5]);
        tuning.set(Parameter.process_optimizer_gain_rs, tuningOptimizerBands2[6]);
        tuning.set(Parameter.process_optimizer_gain_lrs, tuningOptimizerBands2[7]);
        tuning.set(Parameter.process_optimizer_gain_rrs, tuningOptimizerBands2[8]);
        tuning.set(Parameter.process_optimizer_gain_ltm, tuningOptimizerBands2[9]);
        tuning.set(Parameter.process_optimizer_gain_rtm, tuningOptimizerBands2[10]);
        this.parseTuningRegulatorBands(tuning);
        tuning.set(Parameter.bass_enhancer_boost, ParserUtils.asIntArray(this.ti.consumeIntValueTag("bass-enhancer-boost")));
        tuning.set(Parameter.bass_enhancer_cutoff_frequency, ParserUtils.asIntArray(this.ti.consumeIntValueTag("bass-enhancer-cutoff-frequency")));
        tuning.set(Parameter.bass_enhancer_width, ParserUtils.asIntArray(this.ti.consumeIntValueTag("bass-enhancer-width")));
        tuning.set(Parameter.bass_extraction_cutoff_frequency, ParserUtils.asIntArray(this.ti.consumeIntValueTag("bass-extraction-cutoff-frequency")));
        tuning.set(Parameter.height_filter_mode, ParserUtils.asIntArray(this.ti.consumeIntValueTag("height-filter-mode")));
        tuning.set(Parameter.regulator_overdrive, ParserUtils.asIntArray(this.ti.consumeIntValueTag("regulator-overdrive")));
        tuning.set(Parameter.regulator_timbre_preservation, ParserUtils.asIntArray(this.ti.consumeIntValueTag("regulator-timbre-preservation")));
        tuning.set(Parameter.regulator_relaxation_amount, ParserUtils.asIntArray(this.ti.consumeIntValueTag("regulator-relaxation-amount")));
        tuning.set(Parameter.virtual_bass_overall_gain, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtual-bass-overall-gain")));
        tuning.set(Parameter.virtual_bass_slope_gain, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtual-bass-slope-gain")));
        this.ti.consumeStartTag("virtual-bass-mix-freqs");
        tuning.set(Parameter.virtual_bass_mix_freq_low, ParserUtils.asIntArray(this.ti.getIntAttribute("frequency_low")));
        tuning.set(Parameter.virtual_bass_mix_freq_high, ParserUtils.asIntArray(this.ti.getIntAttribute("frequency_high")));
        this.ti.consumeEndTag("virtual-bass-mix-freqs");
        this.ti.consumeStartTag("virtual-bass-src-freqs");
        tuning.set(Parameter.virtual_bass_src_freq_low, ParserUtils.asIntArray(this.ti.getIntAttribute("frequency_low")));
        tuning.set(Parameter.virtual_bass_src_freq_high, ParserUtils.asIntArray(this.ti.getIntAttribute("frequency_high")));
        this.ti.consumeEndTag("virtual-bass-src-freqs");
        this.ti.consumeStartTag("virtual-bass-subgains");
        final int[] array = new int[3];
        if (this.ti.getIntAttribute("num_gains") != array.length) {
            throw new ValidationException(this.ti, String.format("Expected %d Number of virtual bass subgains, found %d", array.length, this.ti.getIntAttribute("num_gains")));
        }
        array[0] = this.ti.getIntAttribute("harmonic_2");
        array[1] = this.ti.getIntAttribute("harmonic_3");
        array[2] = this.ti.getIntAttribute("harmonic_4");
        tuning.set(Parameter.virtual_bass_subgains, array);
        this.ti.consumeEndTag("virtual-bass-subgains");
        tuning.set(Parameter.virtualizer_speaker_angle, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtualizer-speaker-angle")));
        tuning.set(Parameter.virtualizer_speaker_start_freq, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtualizer-speaker-start-freq")));
        tuning.set(Parameter.virtualizer_front_speaker_angle, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtualizer-front-speaker-angle")));
        tuning.set(Parameter.virtualizer_height_speaker_angle, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtualizer-height-speaker-angle")));
        tuning.set(Parameter.virtualizer_surround_speaker_angle, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtualizer-surround-speaker-angle")));
        tuning.set(Parameter.volume_modeler_calibration, ParserUtils.asIntArray(this.ti.consumeIntValueTag("volume-modeler-calibration")));
        tuning.set(Parameter.audio_optimizer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_audio-optimizer-enable")));
        tuning.set(Parameter.bass_enhancer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_bass-enhancer-enable")));
        tuning.set(Parameter.bass_extraction_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_bass-extraction-enable")));
        tuning.set(Parameter.process_optimizer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_process-optimizer-enable")));
        tuning.set(Parameter.regulator_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_regulator-enable")));
        tuning.set(Parameter.regulator_speaker_dist_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_regulator-speaker-dist-enable")));
        tuning.set(Parameter.surround_decoder_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_surround-decoder-enable")));
        tuning.set(Parameter.volume_modeler_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_volume-modeler-enable")));
        this.parseTuningOutputMatrix(tuning);
        tuning.set(Parameter.virtual_bass_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_tuning_partial_virtual_bass_enable")));
        tuning.set(Parameter.virtual_bass_mode, ParserUtils.asIntArray(this.ti.consumeIntValueTag("intermediate_tuning_partial_virtual-bass-mode")));
        final boolean consumeBoolValueTag = this.ti.consumeBoolValueTag("intermediate_tuning_partial_virtualizer_enable");
        final Parameter virtualizer_enable = Parameter.virtualizer_enable;
        int[] array2;
        if (tuning.isMono()) {
            array2 = ParserUtils.asIntArray(0);
        }
        else {
            array2 = ParserUtils.asIntArray(consumeBoolValueTag);
        }
        tuning.set(virtualizer_enable, array2);
        this.ti.consumeEndTag("data");
    }
    
    private void parseTuningRegulatorBands(final Tuning tuning) throws ValidationException {
        this.ti.consumeStartTag("regulator-tuning");
        final ArrayList<Integer> list = new ArrayList<Integer>();
        final ArrayList<Integer> list2 = new ArrayList<Integer>();
        final ArrayList<Integer> list3 = new ArrayList<Integer>();
        final ArrayList<Integer> list4 = new ArrayList<Integer>();
        while (this.ti.atStartTag("band_regulator")) {
            list.add(this.ti.getIntAttribute("frequency"));
            list2.add(this.ti.getIntAttribute("threshold_low"));
            list3.add(this.ti.getIntAttribute("threshold_high"));
            int n;
            if (this.ti.getBoolAttribute("isolated_band")) {
                n = 1;
            }
            else {
                n = 0;
            }
            list4.add(n);
            this.ti.next();
            this.ti.consumeEndTag("band_regulator");
        }
        this.ti.consumeEndTag("regulator-tuning");
        tuning.set(Parameter.regulator_frequency, ParserUtils.asIntArray(list));
        tuning.set(Parameter.regulator_threshold_low, ParserUtils.asIntArray(list2));
        tuning.set(Parameter.regulator_threshold_high, ParserUtils.asIntArray(list3));
        tuning.set(Parameter.regulator_isolated_band, ParserUtils.asIntArray(list4));
    }
    
    void parse() throws ValidationException {
        while (this.ti.atStartTag("tuning")) {
            final Tuning tuning = new Tuning(this.ti.getStringAttribute("name"), Endpoint.valueOf(this.ti.getStringAttribute("endpoint_type")), this.ti.getBoolAttribute("mono_device"));
            this.ti.next();
            this.parseTuningDevices(tuning.getName());
            this.parseTuningParameters(tuning);
            this.ti.consumeEndTag("tuning");
            this.tunings.add(tuning);
        }
    }
}
