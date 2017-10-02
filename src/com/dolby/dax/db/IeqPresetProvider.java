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
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Model;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.PresetType;
import java.util.EnumMap;
import java.util.Map;

public class IeqPresetProvider extends ModelProvider<IeqPreset>
{
    private static final String[] columns;
    private final Map<PresetType, IeqPreset> cache;
    
    static {
        columns = ModelProvider.defineColumns(IeqPreset.validParams, "_id", "preset_type");
    }
    
    public IeqPresetProvider(final SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
        this.cache = new EnumMap<PresetType, IeqPreset>(PresetType.class);
    }
    
    @Override
    protected String[] getColumns() {
        return IeqPresetProvider.columns;
    }
    
    @Override
    protected String getTable() {
        return "ieq_presets";
    }
    
    public IeqPreset load(final PresetType presetType) {
        IeqPreset ieqPreset;
        if ((ieqPreset = this.cache.get(presetType)) == null) {
            ieqPreset = this.load(ModelProvider.whereEquals("preset_type"), presetType.toString());
            this.cache.put(presetType, ieqPreset);
        }
        return ieqPreset;
    }
    
    @Override
    protected IeqPreset read(final Cursor cursor) {
        final long long1 = cursor.getLong(0);
        final int n = 0 + 1;
        final IeqPreset ieqPreset = new IeqPreset(long1, PresetType.valueOf(cursor.getString(n)));
        ModelProvider.readParamValues(ieqPreset, cursor, n + 1);
        return ieqPreset;
    }
    
    @Override
    protected ContentValues write(final IeqPreset ieqPreset, final boolean b) {
        this.cache.remove(ieqPreset.getType());
        final ContentValues contentValues = new ContentValues();
        if (b) {
            contentValues.put("preset_type", ieqPreset.getType().toString());
        }
        ModelProvider.writeParamValues(ieqPreset, contentValues);
        return contentValues;
    }
}
