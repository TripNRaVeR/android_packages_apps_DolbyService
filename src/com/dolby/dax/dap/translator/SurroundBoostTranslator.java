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

public class SurroundBoostTranslator implements Translator
{
    static final Parameter[] srcParams;
    
    static {
        srcParams = new Parameter[] { Parameter.surround_boost };
    }
    
    @Override
    public void dap1Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        if (endpoint == Endpoint.headphone) {
            setParamCommand.add(DapParameter.DAP1_DHSB, parameterValues.get(SurroundBoostTranslator.srcParams[0]));
        }
        else if (endpoint == Endpoint.speaker) {
            setParamCommand.add(DapParameter.DAP1_DSSB, parameterValues.get(SurroundBoostTranslator.srcParams[0]));
        }
        this.dap2Translate(setParamCommand, parameterValues, endpoint);
    }
    
    @Override
    public void dap2Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        setParamCommand.add(DapParameter.DAP2_DSB, parameterValues.get(SurroundBoostTranslator.srcParams[0]));
    }
    
    @Override
    public Parameter[] srcParams() {
        return SurroundBoostTranslator.srcParams;
    }
}
