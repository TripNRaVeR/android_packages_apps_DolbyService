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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import com.dolby.dax.model.Model;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.GeqPreset;

import java.util.Iterator;

public class GeqPresetProvider extends ModelProviderWithDefault<GeqPreset>
{
    private static final String[] columns;
    
    static {
        columns = ModelProvider.defineColumns(GeqPreset.validParams, "_id", "preset_type", "profile_type");
    }
    
    public GeqPresetProvider(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }
    
    @Override
    protected String[] getColumns() {
        return GeqPresetProvider.columns;
    }
    
    @Override
    protected String getDefaultTable() {
        return "default_geq_presets";
    }
    
    @Override
    protected String getTable() {
        return "geq_presets";
    }
    
    public GeqPreset load(final PresetType presetType, final ProfileType profileType) {
        return this.load(ModelProvider.whereEquals("preset_type", "profile_type"), presetType.toString(), profileType.toString());
    }
    
    public Iterator<GeqPreset> load(final ProfileType profileType) {
        return this.queryIter(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    public Iterator<GeqPreset> loadDefault(final ProfileType profileType) {
        return this.queryIterDefault(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    @Override
    protected GeqPreset read(final Cursor cursor) {
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final PresetType value = PresetType.valueOf(cursor.getString(n));
        final int n2 = n + 1;
        final GeqPreset geqPreset = new GeqPreset(long1, value, ProfileType.valueOf(cursor.getString(n2)));
        ModelProvider.readParamValues(geqPreset, cursor, n2 + 1);
        return geqPreset;
    }
    
    public void reset(final PresetType presetType, final ProfileType profileType) {
        this.reset(ModelProvider.whereEquals("preset_type", "profile_type"), presetType.toString(), profileType.toString());
    }
    
    @Override
    protected ContentValues write(final GeqPreset geqPreset, final boolean b) {
        final ContentValues contentValues = new ContentValues();
        if (b) {
            contentValues.put("preset_type", geqPreset.getType().toString());
            contentValues.put("profile_type", geqPreset.getProfile().toString());
        }
        ModelProvider.writeParamValues(geqPreset, contentValues);
        return contentValues;
    }
}
