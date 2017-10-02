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

public class IeqPreset extends ParameterValues implements Model
{
    public static final Set<Parameter> validParams;
    protected final long id;
    protected final PresetType type;
    
    static {
        validParams = Sets.immutableEnumSet(Parameter.ieq_frequency, Parameter.ieq_target);
    }
    
    public IeqPreset(final long id, final PresetType type) {
        this.id = id;
        this.type = type;
    }
    
    public IeqPreset(final PresetType type) {
        this.id = -1L;
        this.type = type;
        this.dirty = true;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public PresetType getType() {
        return this.type;
    }
    
    @Override
    public Set<Parameter> getValidParams() {
        return IeqPreset.validParams;
    }
    
    @Override
    public String toString() {
        return "IeqPreset{id=" + this.id + ", type=" + this.type + ", " + this.valuesToString() + '}';
    }
}
