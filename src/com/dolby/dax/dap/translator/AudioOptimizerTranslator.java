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

import com.dolby.dax.dap.DapParameter;
import com.dolby.dax.dap.commands.SetParamCommand;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.xml.ParserUtils;
import com.google.common.primitives.Ints;

public class AudioOptimizerTranslator implements Translator
{
    static final Parameter[] srcParams;
    
    static {
        srcParams = new Parameter[] { Parameter.audio_optimizer_frequency, Parameter.audio_optimizer_gain_c, Parameter.audio_optimizer_gain_l, Parameter.audio_optimizer_gain_lfe, Parameter.audio_optimizer_gain_lrs, Parameter.audio_optimizer_gain_ls, Parameter.audio_optimizer_gain_r, Parameter.audio_optimizer_gain_rrs, Parameter.audio_optimizer_gain_rs, Parameter.audio_optimizer_gain_ltm, Parameter.audio_optimizer_gain_rtm };
    }
    
    @Override
    public void dap1Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int[] value = parameterValues.get(Parameter.audio_optimizer_frequency);
        final int[] concat = Ints.concat(new int[][] { ParserUtils.asIntArray(2), parameterValues.get(Parameter.audio_optimizer_gain_l), ParserUtils.asIntArray(3), parameterValues.get(Parameter.audio_optimizer_gain_r), ParserUtils.asIntArray(4), parameterValues.get(Parameter.audio_optimizer_gain_c), ParserUtils.asIntArray(5), parameterValues.get(Parameter.audio_optimizer_gain_lfe), ParserUtils.asIntArray(7), parameterValues.get(Parameter.audio_optimizer_gain_ls), ParserUtils.asIntArray(8), parameterValues.get(Parameter.audio_optimizer_gain_rs), ParserUtils.asIntArray(9), parameterValues.get(Parameter.audio_optimizer_gain_lrs), ParserUtils.asIntArray(10), parameterValues.get(Parameter.audio_optimizer_gain_rrs) });
        setParamCommand.add(DapParameter.DAP1_AONB, value.length);
        setParamCommand.add(DapParameter.DAP1_AOBF, value);
        setParamCommand.add(DapParameter.DAP1_AOBG, concat);
        setParamCommand.add(DapParameter.DAP1_AOCC, 8);
        this.dap2Translate(setParamCommand, parameterValues, endpoint);
    }
    
    @Override
    public void dap2Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int[] value = parameterValues.get(Parameter.audio_optimizer_frequency);
        final int[] concat = Ints.concat(new int[][] { parameterValues.get(Parameter.audio_optimizer_gain_l), parameterValues.get(Parameter.audio_optimizer_gain_r), parameterValues.get(Parameter.audio_optimizer_gain_c), parameterValues.get(Parameter.audio_optimizer_gain_lfe), parameterValues.get(Parameter.audio_optimizer_gain_ls), parameterValues.get(Parameter.audio_optimizer_gain_lrs), parameterValues.get(Parameter.audio_optimizer_gain_rs), parameterValues.get(Parameter.audio_optimizer_gain_rrs), parameterValues.get(Parameter.audio_optimizer_gain_ltm), parameterValues.get(Parameter.audio_optimizer_gain_rtm) });
        setParamCommand.add(DapParameter.DAP2_AOBS, new int[][] { { value.length, concat.length / value.length }, value, concat });
    }
    
    @Override
    public Parameter[] srcParams() {
        return AudioOptimizerTranslator.srcParams;
    }
}
