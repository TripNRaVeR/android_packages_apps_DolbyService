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
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.Parameter;

public class GeqTranslator implements Translator
{
    static final Parameter[] srcParams;
    
    static {
        srcParams = new Parameter[] { Parameter.geq_frequency, Parameter.geq_gain };
    }
    
    @Override
    public void dap1Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int[] value = parameterValues.get(Parameter.geq_frequency);
        final int[] value2 = parameterValues.get(Parameter.geq_gain);
        setParamCommand.add(DapParameter.DAP1_GENB, value.length);
        setParamCommand.add(DapParameter.DAP1_GEBF, value);
        setParamCommand.add(DapParameter.DAP1_GEBG, value2);
        setParamCommand.add(DapParameter.DAP_VCNB, value.length);
        setParamCommand.add(DapParameter.DAP_VCBF, value);
        this.dap2Translate(setParamCommand, parameterValues, endpoint);
    }
    
    @Override
    public void dap2Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int[] value = parameterValues.get(Parameter.geq_frequency);
        setParamCommand.add(DapParameter.DAP2_GEBS, value.length, new int[][] { value, parameterValues.get(Parameter.geq_gain) });
        setParamCommand.add(DapParameter.DAP_VCBF, value);
    }
    
    @Override
    public Parameter[] srcParams() {
        return GeqTranslator.srcParams;
    }
}
