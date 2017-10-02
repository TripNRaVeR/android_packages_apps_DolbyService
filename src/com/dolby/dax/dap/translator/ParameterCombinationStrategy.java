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

import com.dolby.dax.dap.DeviceParameters;
import com.dolby.dax.model.Parameter;
import com.google.common.collect.Sets;
import java.util.Set;

public enum ParameterCombinationStrategy {
    Null {
        protected boolean needsCombining(Parameter p) {
            return false;
        }

        protected int[] getOtherValue(DeviceParameters dp, Parameter p) {
            return null;
        }
    },
    Profile {
        protected boolean needsCombining(Parameter p) {
            return ParameterCombinationStrategy.isIntermediateProfileParam(p);
        }

        protected int[] getOtherValue(DeviceParameters dp, Parameter p) {
            if (dp.getTuning() != null) {
                return dp.getTuning().get(p);
            }
            return null;
        }
    },
    ProfileEndpoint {
        protected boolean needsCombining(Parameter p) {
            return ParameterCombinationStrategy.isIntermediateProfileEndpointParam(p);
        }

        protected int[] getOtherValue(DeviceParameters dp, Parameter p) {
            if (dp.getTuning() != null) {
                return dp.getTuning().get(p);
            }
            return null;
        }
    },
    Tuning {
        protected boolean needsCombining(Parameter p) {
            if (ParameterCombinationStrategy.isIntermediateProfileEndpointParam(p)) {
                return true;
            }
            return ParameterCombinationStrategy.isIntermediateProfileParam(p);
        }

        protected int[] getOtherValue(DeviceParameters dp, Parameter p) {
            if (ParameterCombinationStrategy.isIntermediateProfileParam(p)) {
                if (dp.getProfile() != null) {
                    return dp.getProfile().get(p);
                }
            } else if (dp.getProfileEndpoint() != null) {
                return dp.getProfileEndpoint().get(p);
            }
            return null;
        }
    };
    
    public static Set<Parameter> profileAndTuningParams = null;
    public static Set<Parameter> profileEndpointAndTuningParams = null;

    protected abstract int[] getOtherValue(DeviceParameters deviceParameters, Parameter parameter);

    protected abstract boolean needsCombining(Parameter parameter);

    static {
        profileAndTuningParams = Sets.immutableEnumSet(Parameter.audio_optimizer_enable, Parameter.bass_enhancer_enable, Parameter.bass_extraction_enable, Parameter.process_optimizer_enable, Parameter.regulator_enable, Parameter.regulator_speaker_dist_enable, Parameter.surround_decoder_enable, Parameter.virtual_bass_enable, Parameter.volume_modeler_enable);
        profileEndpointAndTuningParams = Sets.immutableEnumSet(Parameter.virtualizer_enable, new Parameter[0]);
    }

    public int[] getValue(DeviceParameters dp, Parameter p, int[] value) {
        if (!needsCombining(p) || getOtherValue(dp, p) == null) {
            return value;
        }
        return new int[]{value[0] & getOtherValue(dp, p)[0]};
    }

    static boolean isIntermediateProfileParam(Parameter p) {
        return profileAndTuningParams.contains(p);
    }

    static boolean isIntermediateProfileEndpointParam(Parameter p) {
        return profileEndpointAndTuningParams.contains(p);
    }
}
