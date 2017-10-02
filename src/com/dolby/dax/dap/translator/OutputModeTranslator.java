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

public class OutputModeTranslator implements Translator {

    static final Parameter[] srcParams;

    static {
        srcParams = new Parameter[] { Parameter.virtualizer_enable, Parameter.output_mix_matrix };
    }

    @Override
    public void dap1Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int n = 0;
        final int n2 = 0;
        int n3 = n;
        int n4 = n2;
            switch (endpoint) {
                default:
                    n4 = n2;
                    n3 = n;
                    break;
                case headphone:
                    n3 = parameterValues.get(Parameter.virtualizer_enable)[0];
                    n4 = n2;
                    break;
                case speaker:
                    n4 = parameterValues.get(Parameter.virtualizer_enable)[0];
                    n3 = n;
                case passthrough:
                    setParamCommand.add(DapParameter.DAP1_ENDP, 0);
                    setParamCommand.add(DapParameter.DAP1_VDHE, n3);
                    setParamCommand.add(DapParameter.DAP1_VSPE, n4);
                    this.dap2Translate(setParamCommand, parameterValues, endpoint);
            }
    }

    @Override
    public void dap2Translate(final SetParamCommand setParamCommand, final ParameterValues parameterValues, final Endpoint endpoint) {
        final int n = 0;
        if (parameterValues.get(Parameter.virtualizer_enable)[0] == 0) {
            setParamCommand.add(DapParameter.DAP2_DOM, 0);
            return;
        }
        int n2 = n;
            switch (endpoint) {
                default:
                    n2 = n;
                    break;
                case headphone:
                    n2 = 2;
                    break;
                case speaker:
                    n2 = 1;
                case passthrough:
                    setParamCommand.add(DapParameter.DAP2_DOM, n2, new int[][] { parameterValues.get(Parameter.output_mix_matrix) });
            }
    }

    @Override
    public Parameter[] srcParams() {
        return srcParams;
    }
}
