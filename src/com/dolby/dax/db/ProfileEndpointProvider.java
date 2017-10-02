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
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Model;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfileType;
import java.util.Iterator;

public class ProfileEndpointProvider extends ModelProviderWithDefault<ProfileEndpoint>
{
    private static final String[] columns;
    
    static {
        columns = ModelProvider.defineColumns(ProfileEndpoint.validParams, "_id", "profile_type", "endpoint");
    }
    
    public ProfileEndpointProvider(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }
    
    @Override
    protected String[] getColumns() {
        return ProfileEndpointProvider.columns;
    }
    
    @Override
    protected String getDefaultTable() {
        return "default_profile_endpoints";
    }
    
    @Override
    protected String getTable() {
        return "profile_endpoints";
    }
    
    public ProfileEndpoint load(final ProfileType profileType, final Endpoint endpoint) {
        return this.load(ModelProvider.whereEquals("profile_type", "endpoint"), profileType.toString(), endpoint.toString());
    }
    
    public Iterator<ProfileEndpoint> load(final ProfileType profileType) {
        return this.queryIter(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    public Iterator<ProfileEndpoint> loadDefault(final ProfileType profileType) {
        return this.queryIterDefault(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    @Override
    protected ProfileEndpoint read(final Cursor cursor) {
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final ProfileType value = ProfileType.valueOf(cursor.getString(n));
        final int n2 = n + 1;
        final ProfileEndpoint profileEndpoint = new ProfileEndpoint(long1, value, Endpoint.valueOf(cursor.getString(n2)));
        ModelProvider.readParamValues(profileEndpoint, cursor, n2 + 1);
        return profileEndpoint;
    }
    
    public void reset(final ProfileType profileType, final Endpoint endpoint) {
        this.reset(ModelProvider.whereEquals("profile_type", "endpoint"), profileType.toString(), endpoint.toString());
    }
    
    @Override
    protected ContentValues write(final ProfileEndpoint profileEndpoint, final boolean b) {
        final ContentValues contentValues = new ContentValues();
        if (b) {
            contentValues.put("profile_type", profileEndpoint.getProfileType().toString());
            contentValues.put("endpoint", profileEndpoint.getEndpoint().toString());
        }
        ModelProvider.writeParamValues(profileEndpoint, contentValues);
        return contentValues;
    }
}
