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

import com.dolby.dax.model.ProfileType;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.HashMap;
import java.util.Map;

public class ConfigProvider
{
    private final Map<String, String> cache;
    private final SQLiteOpenHelper dbHelper;
    
    public ConfigProvider(final SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.cache = new HashMap<String, String>();
    }
    
    private String dbGet(String s, String string) {
        final Cursor query = this.dbHelper.getReadableDatabase().query("configuration", new String[] { "value" }, "key = ?", new String[] { s }, (String)null, (String)null, (String)null);
        s = string;
        if (query == null) {
            return s;
        }
        try {
            if (query.moveToNext()) {
                string = query.getString(0);
            }
            query.close();
            s = string;
            return s;
        }
        finally {
            query.close();
        }
    }
    
    private void dbSet(final String s, final String s2) {
        final SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put("key", s);
        contentValues.put("value", s2);
        writableDatabase.insertWithOnConflict("configuration", (String)null, contentValues, 5);
    }
    
    private String get(final String s, final String s2) {
        String dbGet;
        if ((dbGet = this.cache.get(s)) == null) {
            dbGet = this.dbGet(s, s2);
        }
        return dbGet;
    }
    
    private void set(final String s, final String s2) {
        if (!s2.equals(this.cache.get(s))) {
            this.dbSet(s, s2);
            this.cache.put(s, s2);
        }
    }
    
    public String getDefaultXmlSignature() {
        return this.get("xml_signature", "");
    }
    
    public ProfileType getSelectedProfile() {
        return ProfileType.valueOf(this.get("selected_profile", ProfileType.movie.toString()));
    }
    
    public boolean isDapOn() {
        return Boolean.valueOf(this.get("is_dap_on", "true"));
    }
    
    public void setDapOn(final boolean b) {
        this.set("is_dap_on", Boolean.toString(b));
    }
    
    public void setDefaultXmlSignature(final String s) {
        this.set("xml_signature", s);
    }
    
    public void setSelectedProfile(final ProfileType profileType) {
        this.set("selected_profile", profileType.toString());
    }
}
