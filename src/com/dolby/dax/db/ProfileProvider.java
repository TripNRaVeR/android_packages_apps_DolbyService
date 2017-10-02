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
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileType;

public class ProfileProvider extends ModelProviderWithDefault<Profile>
{
    private static final String[] columns;
    
    static {
        columns = ModelProvider.defineColumns(Profile.validParams, "_id", "profile_type", "name", "selected_ieq_preset_type", "selected_geq_preset_type");
    }
    
    public ProfileProvider(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }
    
    @Override
    protected String[] getColumns() {
        return ProfileProvider.columns;
    }
    
    @Override
    protected String getDefaultTable() {
        return "default_profiles";
    }
    
    @Override
    protected String getTable() {
        return "profiles";
    }
    
    public Profile load(final ProfileType profileType) {
        return this.load(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    public Profile loadDefault(final ProfileType profileType) {
        return this.loadDefault(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    @Override
    protected Profile read(final Cursor cursor) {
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final ProfileType value = ProfileType.valueOf(cursor.getString(n));
        final int n2 = n + 1;
        final String string = cursor.getString(n2);
        final int n3 = n2 + 1;
        final PresetType value2 = PresetType.valueOf(cursor.getString(n3));
        final int n4 = n3 + 1;
        final Profile profile = new Profile(long1, value, string, value2, PresetType.valueOf(cursor.getString(n4)));
        ModelProvider.readParamValues(profile, cursor, n4 + 1);
        return profile;
    }
    
    public void reset(final ProfileType profileType) {
        this.reset(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    @Override
    protected ContentValues write(final Profile profile, final boolean b) {
        final ContentValues contentValues = new ContentValues();
        if (b) {
            contentValues.put("profile_type", profile.getType().toString());
        }
        contentValues.put("name", profile.getName());
        contentValues.put("selected_ieq_preset_type", profile.getSelectedIeqPreset().toString());
        contentValues.put("selected_geq_preset_type", profile.getSelectedGeqPreset().toString());
        ModelProvider.writeParamValues(profile, contentValues);
        return contentValues;
    }
}
