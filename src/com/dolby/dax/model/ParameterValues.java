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

import com.google.common.collect.Iterables;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public abstract class ParameterValues
{
    protected boolean dirty;
    protected final Map<Parameter, int[]> values;
    
    public ParameterValues() {
        this.values = new EnumMap<Parameter, int[]>(Parameter.class);
        this.dirty = false;
    }
    
    protected boolean checkAndUpdate(final Parameter parameter, final int[] array) {
        if (this.getValidParams().contains(parameter) && !Arrays.equals(array, this.get(parameter))) {
            this.set(parameter, array);
            return true;
        }
        return false;
    }
    
    public Set<Map.Entry<Parameter, int[]>> entrySet() {
        return this.values.entrySet();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterValues)) {
            return false;
        }
        final ParameterValues parameterValues = (ParameterValues)o;
        if (this.values.size() != parameterValues.values.size()) {
            return false;
        }
        final Iterator<Map.Entry<Parameter, int[]>> iterator = this.values.entrySet().iterator();
        final Iterator<Map.Entry<Parameter, int[]>> iterator2 = parameterValues.values.entrySet().iterator();
        while (iterator.hasNext() && iterator2.hasNext()) {
            final Map.Entry<Parameter, int[]> entry = iterator.next();
            final Map.Entry<Parameter, int[]> entry2 = iterator2.next();
            if (entry.getKey() != entry2.getKey() || !Arrays.equals(entry.getValue(), entry2.getValue())) {
                return false;
            }
        }
        return iterator.hasNext() == iterator2.hasNext();
    }
    
    public int[] get(final Parameter parameter) {
        return this.values.get(parameter);
    }
    
    public abstract Set<Parameter> getValidParams();
    
    @Override
    public int hashCode() {
        return this.values.hashCode();
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void set(final Parameter parameter, final int[] array) {
        this.values.put(parameter, array);
        this.dirty = true;
    }
    
    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }
    
    @Override
    public String toString() {
        return "ParameterValues{" + this.valuesToString() + "}";
    }
    
    protected String valuesToString() {
        return Joiner.on(", ").join(Iterables.transform((Iterable<Map.Entry<Parameter, int[]>>)this.entrySet(), (Function<? super Map.Entry<Parameter, int[]>, ?>)new Function<Map.Entry<Parameter, int[]>, String>() {
            @Override
            public String apply(final Map.Entry<Parameter, int[]> entry) {
                return entry.getKey().toString() + '=' + Arrays.toString(entry.getValue());
            }
        }));
    }
}
