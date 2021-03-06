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

public class ProfilePort extends ParameterValues implements Model
{
    public static final Set<Parameter> validParams;
    protected final long id;
    protected final Port port;
    protected final ProfileType profileType;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.volume_leveler_in_target, Parameter.volume_leveler_out_target);
    }
    
    public ProfilePort(final long id, final ProfileType profileType, final Port port) {
        this.id = id;
        this.profileType = profileType;
        this.port = port;
    }
    
    public ProfilePort(final ProfileType profileType, final Port port) {
        this.id = -1L;
        this.profileType = profileType;
        this.port = port;
        this.dirty = true;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfilePort)) {
            return false;
        }
        final ProfilePort profilePort = (ProfilePort)o;
        boolean equals = b;
        if (this.port == profilePort.port) {
            equals = b;
            if (this.profileType == profilePort.profileType) {
                equals = super.equals(o);
            }
        }
        return equals;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public Port getPort() {
        return this.port;
    }
    
    public ProfileType getProfileType() {
        return this.profileType;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return ProfilePort.validParams;
    }
    
    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.profileType.hashCode()) * 31 + this.port.hashCode();
    }
    
    @Override
    public String toString() {
        return "ProfilePort{id=" + this.id + ", profileType=" + this.profileType + ", port=" + this.port + ", " + this.valuesToString() + '}';
    }
}
