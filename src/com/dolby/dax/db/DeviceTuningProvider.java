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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteOpenHelper;
import com.dolby.dax.model.DeviceTuning;

public class DeviceTuningProvider
{
    final SQLiteOpenHelper dbHelper;
    
    public DeviceTuningProvider(final SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    public boolean create(final DeviceTuning deviceTuning) {
        final SQLiteStatement compileStatement = this.dbHelper.getWritableDatabase().compileStatement("INSERT INTO device_tunings(device,port,tuning_id) VALUES (?, ?, (SELECT _id FROM tunings WHERE name = ?));");
        compileStatement.bindString(1, deviceTuning.getDevice());
        compileStatement.bindString(2, deviceTuning.getPort());
        compileStatement.bindString(3, deviceTuning.getTuning());
        return compileStatement.executeInsert() != -1L;
    }
    
    public DeviceTuning load(final String s) {
        final SQLiteDatabase readableDatabase = this.dbHelper.getReadableDatabase();
        final DeviceTuning deviceTuning = null;
        final DeviceTuning deviceTuning2 = null;
        final Cursor rawQuery = readableDatabase.rawQuery("SELECT device_tunings._id,device_tunings.device,device_tunings.port,tunings.name FROM device_tunings JOIN tunings ON (tunings._id = device_tunings.tuning_id) WHERE device_tunings.device = ?;", new String[] { s });
        DeviceTuning read = deviceTuning;
        if (rawQuery == null) {
            return read;
        }
        read = deviceTuning2;
        try {
            if (rawQuery.moveToNext()) {
                read = this.read(rawQuery);
            }
            return read;
        }
        finally {
            rawQuery.close();
        }
    }
    
    protected DeviceTuning read(final Cursor cursor) {
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final String string = cursor.getString(n);
        final int n2 = n + 1;
        return new DeviceTuning(long1, string, cursor.getString(n2), cursor.getString(n2 + 1));
    }
}
