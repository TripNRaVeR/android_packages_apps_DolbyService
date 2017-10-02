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

import com.dolby.dax.dap.commands.SetParamCommand;
import com.dolby.dax.dap.DapParameter;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;

public class VirtualBassSrcFreqTranslator implements Translator
{
    static final Parameter[] srcParams;
    
    static {
        srcParams = new Parameter[] { Parameter.virtual_bass_src_freq_low, Parameter.virtual_bass_src_freq_high };
    }
    
    @Override
    public void dap1Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
    }
    
    @Override
    public void dap2Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        setParamCommand.add(DapParameter.DAP2_VBSF, new int[][] { parameterValues.get(VirtualBassSrcFreqTranslator.srcParams[0]), parameterValues.get(VirtualBassSrcFreqTranslator.srcParams[1]) });
    }
    
    @Override
    public Parameter[] srcParams() {
        return VirtualBassSrcFreqTranslator.srcParams;
    }
}
