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

package com.dolby.dax.service.adapter;

import android.util.Log;
import com.dolby.api.DsParams;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.state.DsContext;

public class ParameterAdapter
{
    private final DsContext dsContext;
    final ParameterStrategy[] params;
    
    public ParameterAdapter(final DsContext dsContext) {
        this.params = new ParameterStrategy[] { null, new VirtualizerStrategy(Endpoint.headphone), new VirtualizerStrategy(Endpoint.speaker), new ProfileEndpointParameterStrategy(Parameter.volume_leveler_enable, 1), new ProfileParameterStrategy(Parameter.volume_modeler_enable, 1), new ProfileParameterStrategy(Parameter.surround_decoder_enable, 1), new ProfileParameterStrategy(Parameter.ieq_enable, 1), new ProfileEndpointParameterStrategy(Parameter.dialog_enhancer_enable, 1), new ProfileParameterStrategy(Parameter.graphic_equalizer_enable, 1), null, new ProfileParameterStrategy(Parameter.virtualizer_headphone_reverb_gain, 1), null, null, null, null, null, new ProfileParameterStrategy(Parameter.ieq_amount, 1), null, null, new ProfileGeqParameterStrategy(Parameter.geq_gain, 20), new ProfileParameterStrategy(Parameter.audio_optimizer_enable, 1), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        this.dsContext = dsContext;
    }
    
    public int[] getParameter(final ProfileType profileType, final int n) {
        final int n2 = n - 100;
        if (n2 < this.params.length) {
            final ParameterStrategy parameterStrategy = this.params[n2];
            if (parameterStrategy != null) {
                return parameterStrategy.getParameter(profileType);
            }
        }
        Log.e("ParameterAdapter", "param id " + n + " not supported");
        return null;
    }
    
    public int getParameterLength(final DsParams dsParams) {
        final int n = dsParams.toInt() - 100;
        if (n < this.params.length) {
            final ParameterStrategy parameterStrategy = this.params[n];
            if (parameterStrategy != null) {
                return parameterStrategy.getParamLength();
            }
        }
        Log.e("ParameterAdapter", "param id " + dsParams.toInt() + " not supported");
        return 1;
    }
    
    public void setParameter(final ProfileType profileType, final int n, final int[] array) {
        final int n2 = n - 100;
        if (n2 >= this.params.length) {
            Log.e("ParameterAdapter", "param id " + n + " is greater than supported index");
            return;
        }
        final ParameterStrategy parameterStrategy = this.params[n2];
        if (parameterStrategy != null) {
            parameterStrategy.setParameter(profileType, array);
            return;
        }
        Log.e("ParameterAdapter", "param id " + n + " not supported");
    }
    
    private abstract class ParameterStrategy
    {
        private int paramLength;
        private Parameter parameter;
        
        ParameterStrategy(final Parameter parameter, final int paramLength) {
            this.parameter = parameter;
            this.paramLength = paramLength;
        }
        
        public int getParamLength() {
            return this.paramLength;
        }
        
        public Parameter getParameter() {
            return this.parameter;
        }
        
        public abstract int[] getParameter(final ProfileType p0);
        
        public abstract void setParameter(final ProfileType p0, final int[] p1);
    }
    
    private class ProfileEndpointParameterStrategy extends ParameterStrategy
    {
        public ProfileEndpointParameterStrategy(final Parameter parameter, final int n) {
            super(parameter, n);
        }
        
        @Override
        public int[] getParameter(final ProfileType profileType) {
            return ParameterAdapter.this.dsContext.getProfileParameter(profileType, ParameterAdapter.this.dsContext.getProfileEndpoint(profileType, Endpoint.speaker).getEndpoint(), ((ParameterStrategy)this).getParameter());
        }
        
        @Override
        public void setParameter(final ProfileType profileType, final int[] array) {
            final Endpoint[] values = Endpoint.values();
            for (int i = 0; i < values.length; ++i) {
                ParameterAdapter.this.dsContext.setProfileParameter(profileType, values[i], ((ParameterStrategy)this).getParameter(), array);
            }
        }
    }
    
    private class ProfileGeqParameterStrategy extends ParameterStrategy
    {
        public ProfileGeqParameterStrategy(final Parameter parameter, final int n) {
            super(parameter, n);
        }
        
        @Override
        public int[] getParameter(final ProfileType profileType) {
            return ParameterAdapter.this.dsContext.getProfileGeqParameter(profileType, ((ParameterStrategy)this).getParameter());
        }
        
        @Override
        public void setParameter(final ProfileType profileType, final int[] array) {
            ParameterAdapter.this.dsContext.setProfileGeqParameter(profileType, ((ParameterStrategy)this).getParameter(), array);
        }
    }
    
    private class ProfileParameterStrategy extends ParameterStrategy
    {
        public ProfileParameterStrategy(final Parameter parameter, final int n) {
            super(parameter, n);
        }
        
        @Override
        public int[] getParameter(final ProfileType profileType) {
            return ParameterAdapter.this.dsContext.getProfileParameter(profileType, ((ParameterStrategy)this).getParameter());
        }
        
        @Override
        public void setParameter(final ProfileType profileType, final int[] array) {
            ParameterAdapter.this.dsContext.setProfileParameter(profileType, ((ParameterStrategy)this).getParameter(), array);
        }
    }
    
    private class VirtualizerStrategy extends ParameterStrategy
    {
        private final Endpoint endpoint;
        private final Port port;
        
        VirtualizerStrategy(final Endpoint endpoint) {
            super(Parameter.virtualizer_enable, 1);
            this.endpoint = endpoint;
            if (endpoint.compareTo(Endpoint.headphone) == 0) {
                this.port = Port.headphone_port;
                return;
            }
            this.port = Port.internal_speaker;
        }
        
        @Override
        public int[] getParameter(final ProfileType profileType) {
            return new int[] { ParameterAdapter.this.dsContext.getProfileParameter(profileType, this.endpoint, ((ParameterStrategy)this).getParameter())[0] & ParameterAdapter.this.dsContext.getSelectedTuningParameter(this.port, ((ParameterStrategy)this).getParameter())[0] };
        }
        
        @Override
        public void setParameter(final ProfileType profileType, final int[] array) {
            ParameterAdapter.this.dsContext.setProfileParameter(profileType, this.endpoint, ((ParameterStrategy)this).getParameter(), array);
        }
    }
}
