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

package com.dolby.dax.dap.translator;

import android.util.Log;
import com.dolby.dax.dap.DapParameter;
import com.dolby.dax.model.Parameter;
import java.util.HashMap;
import java.util.Map;

public class Translators
{
    static final Map<Parameter, Translator> parameterTranslators;
    
    static {
        parameterTranslators = new HashMap<Parameter, Translator>();
        final Translator[] array = { new OutputModeTranslator(), new IeqTranslator(), new GeqTranslator(), new SimpleTranslator(DapParameter.DAP_IEON, Parameter.ieq_enable), new SimpleTranslator(DapParameter.DAP_IEA, Parameter.ieq_amount), new SimpleTranslator(DapParameter.DAP_GEON, Parameter.graphic_equalizer_enable), new SimpleTranslator(DapParameter.DAP2_MDEE, Parameter.mi_dialog_enhancer_steering_enable), new SimpleTranslator(DapParameter.DAP2_MDLE, Parameter.mi_dv_leveler_steering_enable), new SimpleTranslator(DapParameter.DAP2_MIEE, Parameter.mi_ieq_steering_enable), new SimpleTranslator(DapParameter.DAP2_MSCE, Parameter.mi_surround_compressor_steering_enable), new SimpleTranslator(DapParameter.DAP_AOON, Parameter.audio_optimizer_enable), new SimpleTranslator(DapParameter.DAP2_BEON, Parameter.bass_enhancer_enable), new SimpleTranslator(DapParameter.DAP_NGON, Parameter.surround_decoder_enable), new SimpleTranslator(DapParameter.DAP_DHRG, Parameter.virtualizer_headphone_reverb_gain), new AudioOptimizerTranslator(), new SimpleTranslator(DapParameter.DAP2_BEB, Parameter.bass_enhancer_boost), new SimpleTranslator(DapParameter.DAP2_BECF, Parameter.bass_enhancer_cutoff_frequency), new SimpleTranslator(DapParameter.DAP2_BEW, Parameter.bass_enhancer_width), new SimpleTranslator(DapParameter.DAP_PLB, Parameter.calibration_boost), new SimpleTranslator(DapParameter.DAP_DEA, Parameter.dialog_enhancer_amount), new SimpleTranslator(DapParameter.DAP_DED, Parameter.dialog_enhancer_ducking), new SimpleTranslator(DapParameter.DAP_DEON, Parameter.dialog_enhancer_enable), new AudioRegulatorEnableTranslator(), new AudioRegulatorTranslator(), new SimpleTranslator(DapParameter.DAP2_ARDE, Parameter.regulator_speaker_dist_enable), new SimpleTranslator(DapParameter.DAP_AROD, Parameter.regulator_overdrive), new SimpleTranslator(DapParameter.DAP2_ARRA, Parameter.regulator_relaxation_amount), new SimpleTranslator(DapParameter.DAP_ARTP, Parameter.regulator_timbre_preservation), new SurroundBoostTranslator(), new VirtualBassSrcFreqTranslator(), new VirtualBassMixFreqTranslator(), new SimpleTranslator(DapParameter.DAP2_VBM, Parameter.virtual_bass_mode), new SimpleTranslator(DapParameter.DAP2_VBOG, Parameter.virtual_bass_overall_gain), new SimpleTranslator(DapParameter.DAP2_VBSG, Parameter.virtual_bass_slope_gain), new SimpleTranslator(DapParameter.DAP2_VBHG, Parameter.virtual_bass_subgains), new SimpleTranslator(DapParameter.DAP1_DSSA, Parameter.virtualizer_speaker_angle), new SimpleTranslator(DapParameter.DAP1_DSSF, Parameter.virtualizer_speaker_start_freq), new SimpleTranslator(DapParameter.DAP_VMB, Parameter.volmax_boost), new SimpleTranslator(DapParameter.DAP_DVLA, Parameter.volume_leveler_amount), new SimpleTranslator(DapParameter.DAP_DVLE, Parameter.volume_leveler_enable), new SimpleTranslator(DapParameter.DAP_DVLI, Parameter.volume_leveler_in_target), new SimpleTranslator(DapParameter.DAP_DVLO, Parameter.volume_leveler_out_target), new SimpleTranslator(DapParameter.DAP_DVME, Parameter.volume_modeler_enable), new SimpleTranslator(DapParameter.DAP_DVMC, Parameter.volume_modeler_calibration), new SimpleTranslator(DapParameter.DAP2_DHFM, Parameter.height_filter_mode), new SimpleTranslator(DapParameter.DAP2_DFSA, Parameter.virtualizer_front_speaker_angle), new SimpleTranslator(DapParameter.DAP2_DSA, Parameter.virtualizer_surround_speaker_angle), new SimpleTranslator(DapParameter.DAP2_DHSA, Parameter.virtualizer_height_speaker_angle) };
        for (int length = array.length, i = 0; i < length; ++i) {
            final Translator translator = array[i];
            final Parameter[] srcParams = translator.srcParams();
            for (int length2 = srcParams.length, j = 0; j < length2; ++j) {
                final Parameter parameter = srcParams[j];
                final Translator translator2 = Translators.parameterTranslators.put(parameter, translator);
                if (translator2 != null) {
                    Log.wtf("Translators", "Parameter " + parameter + " is associated with translators " + translator + " and " + translator2);
                }
            }
        }
    }
    
    public static Translator get(final Parameter parameter) {
        return Translators.parameterTranslators.get(parameter);
    }
}
