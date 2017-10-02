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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.DeviceTuning;
import java.util.Iterator;

public class DatabaseProvider implements Provider
{
    private final ConfigProvider configProvider;
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final DeviceTuningProvider deviceTuningProvider;
    private final GeqPresetProvider geqPresetProvider;
    private final IeqPresetProvider ieqPresetProvider;
    private final ProfileEndpointProvider profileEndpointProvider;
    private final ProfilePortProvider profilePortProvider;
    private final ProfileProvider profileProvider;
    private final TuningProvider tuningProvider;
    
    public DatabaseProvider(final Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.configProvider = new ConfigProvider(this.dbHelper);
        this.ieqPresetProvider = new IeqPresetProvider(this.dbHelper);
        this.geqPresetProvider = new GeqPresetProvider(this.dbHelper);
        this.profileProvider = new ProfileProvider(this.dbHelper);
        this.profileEndpointProvider = new ProfileEndpointProvider(this.dbHelper);
        this.profilePortProvider = new ProfilePortProvider(this.dbHelper);
        this.tuningProvider = new TuningProvider(this.dbHelper);
        this.deviceTuningProvider = new DeviceTuningProvider(this.dbHelper);
    }
    
    @Override
    public void beginTransaction() {
        this.dbHelper.getWritableDatabase().beginTransactionNonExclusive();
    }
    
    @Override
    public void clear() {
        this.dbHelper.close();
        this.context.deleteDatabase(this.dbHelper.getDatabaseName());
    }
    
    @Override
    public void commitTransaction() {
        final SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
    }
    
    @Override
    public void create(final DeviceTuning deviceTuning) {
        this.deviceTuningProvider.create(deviceTuning);
    }
    
    @Override
    public void create(final GeqPreset geqPreset) {
        this.geqPresetProvider.create(geqPreset);
    }
    
    @Override
    public void create(final IeqPreset ieqPreset) {
        this.ieqPresetProvider.create(ieqPreset);
    }
    
    @Override
    public void create(final Profile profile) {
        this.profileProvider.create(profile);
    }
    
    @Override
    public void create(final ProfileEndpoint profileEndpoint) {
        this.profileEndpointProvider.create(profileEndpoint);
    }
    
    @Override
    public void create(final ProfilePort profilePort) {
        this.profilePortProvider.create(profilePort);
    }
    
    @Override
    public void create(final Tuning tuning) {
        this.tuningProvider.create(tuning);
    }
    
    @Override
    public String getDefaultXmlSignature() {
        return this.configProvider.getDefaultXmlSignature();
    }
    
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return this.dbHelper.getReadableDatabase();
    }
    
    @Override
    public ProfileType getSelectedProfile() {
        return this.configProvider.getSelectedProfile();
    }
    
    @Override
    public boolean isDapOn() {
        return this.configProvider.isDapOn();
    }
    
    @Override
    public Iterator<GeqPreset> loadDefaultGeqPresets(final ProfileType profileType) {
        return this.geqPresetProvider.loadDefault(profileType);
    }
    
    @Override
    public Profile loadDefaultProfile(final ProfileType profileType) {
        return this.profileProvider.loadDefault(profileType);
    }
    
    @Override
    public Iterator<ProfileEndpoint> loadDefaultProfileEndpoints(final ProfileType profileType) {
        return this.profileEndpointProvider.loadDefault(profileType);
    }
    
    @Override
    public Iterator<ProfilePort> loadDefaultProfilePorts(final ProfileType profileType) {
        return this.profilePortProvider.loadDefault(profileType);
    }
    
    @Override
    public Iterator<GeqPreset> loadGeqPresets(final ProfileType profileType) {
        return this.geqPresetProvider.load(profileType);
    }
    
    @Override
    public IeqPreset loadIeqPreset(final PresetType presetType) {
        return this.ieqPresetProvider.load(presetType);
    }
    
    @Override
    public Profile loadProfile(final ProfileType profileType) {
        return this.profileProvider.load(profileType);
    }
    
    public ProfileEndpoint loadProfileEndpoint(final ProfileType profileType, final Endpoint endpoint) {
        return this.profileEndpointProvider.load(profileType, endpoint);
    }
    
    @Override
    public Iterator<ProfileEndpoint> loadProfileEndpoints(final ProfileType profileType) {
        return this.profileEndpointProvider.load(profileType);
    }
    
    public ProfilePort loadProfilePort(final ProfileType profileType, final Port port) {
        return this.profilePortProvider.load(profileType, port);
    }
    
    @Override
    public Iterator<ProfilePort> loadProfilePorts(final ProfileType profileType) {
        return this.profilePortProvider.load(profileType);
    }
    
    @Override
    public Tuning loadTuningForDevice(final String s) {
        final DeviceTuning load = this.deviceTuningProvider.load(s);
        if (load == null) {
            return null;
        }
        return this.tuningProvider.load(load.getTuning());
    }
    
    @Override
    public GeqPreset reset(final GeqPreset geqPreset) {
        this.geqPresetProvider.reset(geqPreset.getType(), geqPreset.getProfile());
        return this.geqPresetProvider.load(geqPreset.getType(), geqPreset.getProfile());
    }
    
    @Override
    public Profile reset(final Profile profile) {
        this.profileProvider.reset(profile.getType());
        return this.loadProfile(profile.getType());
    }
    
    @Override
    public ProfileEndpoint reset(final ProfileEndpoint profileEndpoint) {
        this.profileEndpointProvider.reset(profileEndpoint.getProfileType(), profileEndpoint.getEndpoint());
        return this.loadProfileEndpoint(profileEndpoint.getProfileType(), profileEndpoint.getEndpoint());
    }
    
    @Override
    public ProfilePort reset(final ProfilePort profilePort) {
        this.profilePortProvider.reset(profilePort.getProfileType(), profilePort.getPort());
        return this.loadProfilePort(profilePort.getProfileType(), profilePort.getPort());
    }
    
    @Override
    public void save(final GeqPreset geqPreset) {
        this.geqPresetProvider.save(geqPreset);
    }
    
    @Override
    public void save(final Profile profile) {
        this.profileProvider.save(profile);
    }
    
    @Override
    public void save(final ProfileEndpoint profileEndpoint) {
        this.profileEndpointProvider.save(profileEndpoint);
    }
    
    @Override
    public void save(final ProfilePort profilePort) {
        this.profilePortProvider.save(profilePort);
    }
    
    @Override
    public void setDapOn(final boolean dapOn) {
        this.configProvider.setDapOn(dapOn);
    }
    
    @Override
    public void setDefaultXmlSignature(final String defaultXmlSignature) {
        this.configProvider.setDefaultXmlSignature(defaultXmlSignature);
    }
    
    @Override
    public void setSelectedProfile(final ProfileType selectedProfile) {
        this.configProvider.setSelectedProfile(selectedProfile);
    }
}
