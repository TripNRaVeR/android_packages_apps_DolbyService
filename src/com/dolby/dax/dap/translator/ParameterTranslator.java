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
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ParameterTranslator implements ParamChangeObserver
{
    final int dapVersion;
    Endpoint endpoint;
    ParameterValues parameterValues;
    final Set<Translator> pendingTranslations;
    
    public ParameterTranslator(final int dapVersion) {
        this.dapVersion = dapVersion;
        this.pendingTranslations = new HashSet<Translator>();
    }
    
    @Override
    public void onParameterChanged(final Parameter parameter) {
        final Translator value = Translators.get(parameter);
        if (value != null) {
            this.pendingTranslations.add(value);
        }
    }
    
    @Override
    public void setSource(final ParameterValues parameterValues, final Endpoint endpoint) {
        this.parameterValues = parameterValues;
        this.endpoint = endpoint;
    }
    
    public void translateAll(final SetParamCommand setParamCommand) {
        final Iterator<Parameter> iterator = this.parameterValues.getValidParams().iterator();
        while (iterator.hasNext()) {
            this.onParameterChanged(iterator.next());
        }
        this.translatePending(setParamCommand);
    }
    
    public void translatePending(final SetParamCommand setParamCommand) {
        for (final Translator translator : this.pendingTranslations) {
            switch (this.dapVersion) {
                default: {
                    continue;
                }
                case 1: {
                    translator.dap1Translate(setParamCommand, this.parameterValues, this.endpoint);
                    continue;
                }
                case 2: {
                    translator.dap2Translate(setParamCommand, this.parameterValues, this.endpoint);
                    continue;
                }
            }
        }
        this.pendingTranslations.clear();
    }
}
