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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.dolby.dax.model.Model;
import com.google.common.base.Joiner;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class ModelProviderWithDefault<ModelImpl extends Model> extends ModelProvider<ModelImpl>
{
    static final Joiner cacheKeyJoiner;
    protected final Map<String, ModelImpl> cache;
    
    static {
        cacheKeyJoiner = Joiner.on(',');
    }
    
    public ModelProviderWithDefault(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
        this.cache = new HashMap<String, ModelImpl>();
    }
    
    @Override
    public void create(final ModelImpl modelImpl) {
        final ContentValues write = this.write(modelImpl, true);
        final SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
        writableDatabase.insertWithOnConflict(this.getDefaultTable(), (String)null, write, 5);
        writableDatabase.insertWithOnConflict(this.getTable(), (String)null, write, 5);
    }
    
    protected abstract String getDefaultTable();
    
    protected ModelImpl loadDefault(final String str, String... strArr) {
        String str2 = str + ',' + cacheKeyJoiner.join((Object[]) strArr);
        Model model = (Model) this.cache.get(str2);
        if (model == null) {
            Cursor query = query(getDefaultTable(), str, strArr);
            if (query != null) {
                try {
                    if (query.moveToNext()) {
                        model = read(query);
                        this.cache.put(str2, (ModelImpl)model);
                    }
                    query.close();
                } catch (Throwable th) {
                    query.close();
                }
            }
        }
        return (ModelImpl)model;
    }
    
    protected Iterator<ModelImpl> queryIterDefault(final String s, final String... array) {
        return new QueryIterator<ModelImpl>(this, this.query(this.getDefaultTable(), s, array));
    }
    
    protected void reset(String string, final String... array) {
        final String join = Joiner.on(',').join(this.getColumns());
        string = "INSERT OR REPLACE INTO " + this.getTable() + "(" + join + ")" + " SELECT " + join + " FROM " + this.getDefaultTable() + " WHERE " + string + ";";
        final SQLiteStatement compileStatement = this.dbHelper.getWritableDatabase().compileStatement(string);
        compileStatement.bindAllArgsAsStrings(array);
        compileStatement.executeInsert();
    }
}
