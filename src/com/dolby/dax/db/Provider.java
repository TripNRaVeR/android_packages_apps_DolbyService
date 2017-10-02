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

package com.dolby.dax.db;

import android.database.sqlite.SQLiteDatabase;
import com.dolby.dax.model.DeviceTuning;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.model.Tuning;
import java.util.Iterator;

public interface Provider
{
    void beginTransaction();
    
    void clear();
    
    void commitTransaction();
    
    void create(final DeviceTuning p0);
    
    void create(final GeqPreset p0);
    
    void create(final IeqPreset p0);
    
    void create(final Profile p0);
    
    void create(final ProfileEndpoint p0);
    
    void create(final ProfilePort p0);
    
    void create(final Tuning p0);
    
    String getDefaultXmlSignature();
    
    SQLiteDatabase getReadableDatabase();
    
    ProfileType getSelectedProfile();
    
    boolean isDapOn();
    
    Iterator<GeqPreset> loadDefaultGeqPresets(final ProfileType p0);
    
    Profile loadDefaultProfile(final ProfileType p0);
    
    Iterator<ProfileEndpoint> loadDefaultProfileEndpoints(final ProfileType p0);
    
    Iterator<ProfilePort> loadDefaultProfilePorts(final ProfileType p0);
    
    Iterator<GeqPreset> loadGeqPresets(final ProfileType p0);
    
    IeqPreset loadIeqPreset(final PresetType p0);
    
    Profile loadProfile(final ProfileType p0);
    
    Iterator<ProfileEndpoint> loadProfileEndpoints(final ProfileType p0);
    
    Iterator<ProfilePort> loadProfilePorts(final ProfileType p0);
    
    Tuning loadTuningForDevice(final String p0);
    
    GeqPreset reset(final GeqPreset p0);
    
    Profile reset(final Profile p0);
    
    ProfileEndpoint reset(final ProfileEndpoint p0);
    
    ProfilePort reset(final ProfilePort p0);
    
    void save(final GeqPreset p0);
    
    void save(final Profile p0);
    
    void save(final ProfileEndpoint p0);
    
    void save(final ProfilePort p0);
    
    void setDapOn(final boolean p0);
    
    void setDefaultXmlSignature(final String p0);
    
    void setSelectedProfile(final ProfileType p0);
}
