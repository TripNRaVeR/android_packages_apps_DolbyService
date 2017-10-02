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

public class GeqPreset extends ParameterValues implements Model
{
    public static final Set<Parameter> validParams;
    protected final long id;
    protected final ProfileType profile;
    protected final PresetType type;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.geq_frequency, Parameter.geq_gain);
    }
    
    public GeqPreset(final long id, final PresetType type, final ProfileType profile) {
        this.id = id;
        this.type = type;
        this.profile = profile;
    }
    
    public GeqPreset(final PresetType type, final ProfileType profile) {
        this.id = -1L;
        this.type = type;
        this.profile = profile;
        this.dirty = true;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeqPreset)) {
            return false;
        }
        final GeqPreset geqPreset = (GeqPreset)o;
        boolean equals = b;
        if (this.profile == geqPreset.profile) {
            equals = b;
            if (this.type == geqPreset.type) {
                equals = super.equals(o);
            }
        }
        return equals;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public ProfileType getProfile() {
        return this.profile;
    }
    
    public PresetType getType() {
        return this.type;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return GeqPreset.validParams;
    }
    
    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.type.hashCode()) * 31 + this.profile.hashCode();
    }
    
    @Override
    public String toString() {
        return "GeqPreset{id=" + this.id + ", type=" + this.type + ", profile=" + this.profile + ", " + this.valuesToString() + '}';
    }
}
