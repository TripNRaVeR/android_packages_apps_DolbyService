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
import com.dolby.dax.model.Model;
import java.util.Iterator;

public class QueryIterator<ModelImpl extends Model> implements Iterator<ModelImpl>
{
    final Cursor cursor;
    final ModelProvider<ModelImpl> provider;
    
    QueryIterator(final ModelProvider<ModelImpl> provider, final Cursor cursor) {
        this.cursor = cursor;
        this.provider = provider;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (!this.cursor.isClosed()) {
                this.cursor.close();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    @Override
    public boolean hasNext() {
        return !this.cursor.isLast();
    }
    
    @Override
    public ModelImpl next() {
        this.cursor.moveToNext();
        return this.provider.read(this.cursor);
    }
    
    @Override
    public void remove() {
        throw new RuntimeException();
    }
}
