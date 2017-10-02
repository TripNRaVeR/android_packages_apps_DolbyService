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

import com.dolby.dax.dap.commands.*;
import com.dolby.dax.model.*;
import com.dolby.dax.dap.*;

public class AudioRegulatorTranslator implements Translator
{
    static final Parameter[] srcParams;
    
    static {
        srcParams = new Parameter[] { Parameter.regulator_frequency, Parameter.regulator_isolated_band, Parameter.regulator_threshold_high, Parameter.regulator_threshold_low };
    }
    
    @Override
    public void dap1Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int[] value = parameterValues.get(Parameter.regulator_frequency);
        final int[] value2 = parameterValues.get(Parameter.regulator_threshold_low);
        final int[] value3 = parameterValues.get(Parameter.regulator_threshold_high);
        final int[] value4 = parameterValues.get(Parameter.regulator_isolated_band);
        setParamCommand.add(DapParameter.DAP1_ARNB, value.length);
        setParamCommand.add(DapParameter.DAP1_ARBF, value);
        setParamCommand.add(DapParameter.DAP1_ARBL, value2);
        setParamCommand.add(DapParameter.DAP1_ARBH, value3);
        setParamCommand.add(DapParameter.DAP1_ARBI, value4);
        this.dap2Translate(setParamCommand, parameterValues, endpoint);
    }
    
    @Override
    public void dap2Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int[] value = parameterValues.get(Parameter.regulator_frequency);
        setParamCommand.add(DapParameter.DAP2_ARBS, value.length, new int[][] { value, parameterValues.get(Parameter.regulator_threshold_low), parameterValues.get(Parameter.regulator_threshold_high), parameterValues.get(Parameter.regulator_isolated_band) });
    }
    
    @Override
    public Parameter[] srcParams() {
        return AudioRegulatorTranslator.srcParams;
    }
}
