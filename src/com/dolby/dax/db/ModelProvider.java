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
import com.dolby.dax.db.QueryIterator;
import com.dolby.dax.model.Model;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ModelProvider<ModelImpl extends Model>
{
    static final Function<String, Integer> toIntFunction;
    protected final SQLiteOpenHelper dbHelper;
    
    static {
        toIntFunction = new Function<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                if (s == null) {
                    throw new RuntimeException("Invalid input value null");
                }
                return Integer.valueOf(s);
            }
        };
    }
    
    public ModelProvider(final SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    static String[] defineColumns(final Set set, final String... array) {
        ImmutableList build = ImmutableList.builder().add((Object[]) array).addAll(Iterables.transform(set, Functions.toStringFunction())).build();
        return (String[]) build.toArray(new String[build.size()]);
    }

    static void readParamValues(final ParameterValues parameterValues, final Cursor cursor, int i) {
        final Iterator<Parameter> iterator = parameterValues.getValidParams().iterator();
        while (i < cursor.getColumnCount()) {
            parameterValues.set(iterator.next(), stringToValues(cursor.getString(i)));
            ++i;
        }
    }
    
    static int[] stringToValues(final String s) {
        if (Strings.isNullOrEmpty(s)) {
            return new int[0];
        }
        return Ints.toArray((Collection<? extends Number>)Lists.newArrayList(Iterables.transform(Splitter.on(", ").split(s), (Function<? super String, ?>)ModelProvider.toIntFunction)));
    }
    
    static String valuesToString(final int[] array) {
        final String string = Arrays.toString(array);
        return string.substring(1, string.length() - 1);
    }
    
    static String whereEquals(final String... array) {
        return Joiner.on(" = ? AND ").join(array) + " = ?";
    }
    
    static void writeParamValues(final ParameterValues parameterValues, final ContentValues contentValues) {
        for (final Map.Entry<Parameter, int[]> entry : parameterValues.entrySet()) {
            contentValues.put(entry.getKey().toString(), valuesToString(entry.getValue()));
        }
    }

    public void create(final ModelImpl modelImpl) {
        this.dbHelper.getWritableDatabase().insertWithOnConflict(getTable(), null, write(modelImpl, true), 5);
    }

    protected abstract String[] getColumns();
    
    protected abstract String getTable();

    protected ModelImpl load(final String str, String... strArr) {
        Model model = null;
        Cursor query = query(getTable(), str, strArr);
        if (query != null) {
            try {
                if (query.moveToNext()) {
                    model = read(query);
                    model.setDirty(false);
                }
                query.close();
            } catch (Throwable th) {
                query.close();
            }
        }
        return (ModelImpl)model;
    }
    
    protected Cursor query(final String s, final String s2, final String... array) {
        return this.dbHelper.getReadableDatabase().query(s, this.getColumns(), s2, array, (String)null, (String)null, (String)null);
    }
    
    protected Iterator<ModelImpl> queryIter(final String s, final String... array) {
        return new QueryIterator<ModelImpl>(this, this.query(this.getTable(), s, array));
    }
    
    protected abstract ModelImpl read(final Cursor p0);
    
    public boolean save(final ModelImpl modelImpl) {
        if (!modelImpl.isDirty()) {
            return true;
        }
        modelImpl.setDirty(false);
        return this.dbHelper.getWritableDatabase().update(this.getTable(), this.write(modelImpl, false), whereEquals("_id"), new String[] { Long.toString(modelImpl.getId()) }) == 1;
    }
    
    protected abstract ContentValues write(final ModelImpl p0, final boolean p1);
}
