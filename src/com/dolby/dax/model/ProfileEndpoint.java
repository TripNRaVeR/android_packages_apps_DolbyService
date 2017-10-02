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

package com.dolby.dax.model;

import com.google.common.collect.Sets;
import java.util.Set;

public class ProfileEndpoint extends ParameterValues implements Model
{
    public static final Set<Parameter> validParams;
    protected final Endpoint endpoint;
    protected final long id;
    protected final ProfileType profileType;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.calibration_boost, Parameter.dialog_enhancer_amount, Parameter.dialog_enhancer_ducking, Parameter.dialog_enhancer_enable, Parameter.surround_boost, Parameter.virtualizer_enable, Parameter.volmax_boost, Parameter.volume_leveler_amount, Parameter.volume_leveler_enable);
    }
    
    public ProfileEndpoint(final long id, final ProfileType profileType, final Endpoint endpoint) {
        this.id = id;
        this.profileType = profileType;
        this.endpoint = endpoint;
    }
    
    public ProfileEndpoint(final ProfileType profileType, final Endpoint endpoint) {
        this.id = -1L;
        this.profileType = profileType;
        this.endpoint = endpoint;
        this.dirty = true;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileEndpoint)) {
            return false;
        }
        final ProfileEndpoint profileEndpoint = (ProfileEndpoint)o;
        boolean equals = b;
        if (this.endpoint == profileEndpoint.endpoint) {
            equals = b;
            if (this.profileType == profileEndpoint.profileType) {
                equals = super.equals(o);
            }
        }
        return equals;
    }
    
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public ProfileType getProfileType() {
        return this.profileType;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return ProfileEndpoint.validParams;
    }
    
    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.profileType.hashCode()) * 31 + this.endpoint.hashCode();
    }
    
    @Override
    public String toString() {
        return "ProfileEndpoint{id=" + this.id + ", profileType=" + this.profileType + ", endpoint=" + this.endpoint + ", " + this.valuesToString() + '}';
    }
}
