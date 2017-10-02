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

package com.dolby.dax.dap;

import com.dolby.dax.dap.translator.ParamChangeObserver;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ContentParameters extends ParameterValues
{
    public static final Set<Parameter> validParams;
    GeqPreset geqPreset;
    IeqPreset ieqPreset;
    ParamChangeObserver paramChangeObserver;
    Profile profile;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.geq_frequency, Parameter.geq_gain, Parameter.graphic_equalizer_enable, Parameter.ieq_amount, Parameter.ieq_enable, Parameter.ieq_frequency, Parameter.ieq_target, Parameter.mi_dialog_enhancer_steering_enable, Parameter.mi_dv_leveler_steering_enable, Parameter.mi_ieq_steering_enable, Parameter.mi_surround_compressor_steering_enable, Parameter.virtualizer_headphone_reverb_gain);
    }
    
    ContentParameters(final ParamChangeObserver paramChangeObserver) {
        (this.paramChangeObserver = paramChangeObserver).setSource(this, null);
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return ContentParameters.validParams;
    }
    
    public void setGeqPreset(final GeqPreset geqPreset) {
        this.updateParams(this.geqPreset = geqPreset);
    }
    
    public void setIeqPreset(final IeqPreset ieqPreset) {
        this.ieqPreset = ieqPreset;
        if (ieqPreset.getType() != PresetType.off) {
            this.updateParams(ieqPreset);
        }
    }
    
    public void setProfile(final Profile profile) {
        this.updateParams(this.profile = profile);
    }
    
    protected void updateParams(final ParameterValues parameterValues) {
        for (final Map.Entry<Parameter, int[]> entry : parameterValues.entrySet()) {
            final Parameter parameter = entry.getKey();
            if (this.checkAndUpdate(parameter, entry.getValue())) {
                this.paramChangeObserver.onParameterChanged(parameter);
            }
        }
    }
}
