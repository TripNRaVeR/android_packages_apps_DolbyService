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
import com.dolby.dax.model.Port;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileType;
import java.util.Iterator;

public class ProfilePortProvider extends ModelProviderWithDefault<ProfilePort>
{
    private static final String[] columns;
    
    static {
        columns = ModelProvider.defineColumns(ProfilePort.validParams, "_id", "profile_type", "port");
    }
    
    public ProfilePortProvider(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }
    
    @Override
    protected String[] getColumns() {
        return ProfilePortProvider.columns;
    }
    
    @Override
    protected String getDefaultTable() {
        return "default_profile_ports";
    }
    
    @Override
    protected String getTable() {
        return "profile_ports";
    }
    
    public ProfilePort load(final ProfileType profileType, final Port port) {
        return this.load(ModelProvider.whereEquals("profile_type", "port"), profileType.toString(), port.toString());
    }
    
    public Iterator<ProfilePort> load(final ProfileType profileType) {
        return this.queryIter(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    public Iterator<ProfilePort> loadDefault(final ProfileType profileType) {
        return this.queryIterDefault(ModelProvider.whereEquals("profile_type"), profileType.toString());
    }
    
    @Override
    protected ProfilePort read(final Cursor cursor) {
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final ProfileType value = ProfileType.valueOf(cursor.getString(n));
        final int n2 = n + 1;
        final ProfilePort profilePort = new ProfilePort(long1, value, Port.valueOf(cursor.getString(n2)));
        ModelProvider.readParamValues(profilePort, cursor, n2 + 1);
        return profilePort;
    }
    
    public void reset(final ProfileType profileType, final Port port) {
        this.reset(ModelProvider.whereEquals("profile_type", "port"), profileType.toString(), port.toString());
    }
    
    @Override
    protected ContentValues write(final ProfilePort profilePort, final boolean b) {
        final ContentValues contentValues = new ContentValues();
        if (b) {
            contentValues.put("profile_type", profilePort.getProfileType().toString());
            contentValues.put("port", profilePort.getPort().toString());
        }
        ModelProvider.writeParamValues(profilePort, contentValues);
        return contentValues;
    }
}
