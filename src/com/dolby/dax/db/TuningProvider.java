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
import com.dolby.dax.model.Tuning;

public class TuningProvider extends ModelProvider<Tuning>
{
    private static final String[] columns;
    
    static {
        columns = ModelProvider.defineColumns(Tuning.validParams, "_id", "name", "endpoint", "is_mono", "is_readonly");
    }
    
    public TuningProvider(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }
    
    @Override
    protected String[] getColumns() {
        return TuningProvider.columns;
    }
    
    @Override
    protected String getTable() {
        return "tunings";
    }
    
    public Tuning load(final String s) {
        return this.load(ModelProvider.whereEquals("name"), s);
    }
    
    @Override
    protected Tuning read(final Cursor cursor) {
        boolean b = true;
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final String string = cursor.getString(n);
        final int n2 = n + 1;
        final Endpoint value = Endpoint.valueOf(cursor.getString(n2));
        final int n3 = n2 + 1;
        final boolean b2 = cursor.getInt(n3) != 0;
        final int n4 = n3 + 1;
        if (cursor.getInt(n4) == 0) {
            b = false;
        }
        final Tuning tuning = new Tuning(long1, string, value, b2, b);
        ModelProvider.readParamValues(tuning, cursor, n4 + 1);
        return tuning;
    }
    
    @Override
    protected ContentValues write(final Tuning tuning, final boolean b) {
        final ContentValues contentValues = new ContentValues();
        if (b) {
            contentValues.put("name", tuning.getName());
            contentValues.put("is_readonly", tuning.isReadonly());
        }
        contentValues.put("endpoint", tuning.getEndpoint().toString());
        contentValues.put("is_mono", tuning.isMono());
        ModelProvider.writeParamValues(tuning, contentValues);
        return contentValues;
    }
}
