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

public class DeviceTuning implements Model
{
    protected final String device;
    protected boolean dirty;
    protected final long id;
    protected final String port;
    protected final String tuning;
    
    public DeviceTuning(final long id, final String device, final String port, final String tuning) {
        this.dirty = false;
        this.id = id;
        this.device = device;
        this.port = port;
        this.tuning = tuning;
    }
    
    public DeviceTuning(final String device, final String port, final String tuning) {
        this.dirty = false;
        this.id = -1L;
        this.device = device;
        this.port = port;
        this.tuning = tuning;
        this.dirty = true;
    }
    
    public String getDevice() {
        return this.device;
    }
    
    @Override
    public long getId() {
        return this.id;
    }
    
    public String getPort() {
        return this.port;
    }
    
    public String getTuning() {
        return this.tuning;
    }
    
    @Override
    public boolean isDirty() {
        return this.dirty;
    }
    
    @Override
    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }
    
    @Override
    public String toString() {
        return "DeviceTuning{id=" + this.id + ", device='" + this.device + '\'' + ", port='" + this.port + '\'' + ", tuning='" + this.tuning + '\'' + '}';
    }
}
