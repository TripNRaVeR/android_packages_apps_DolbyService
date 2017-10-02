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

package com.dolby.dax.api;

import android.database.MatrixCursor;
import android.database.Cursor;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;

import com.dolby.dax.model.Port;
import com.dolby.dax.state.DsContextFactory;
import com.dolby.dax.state.DsContext;

import java.util.Iterator;

public class TuningsProvider extends ContentProvider
{
    private static final UriMatcher API_URIS;
    
    static {
        (API_URIS = new UriMatcher(-1)).addURI("com.dolby.dax.api.Tunings", "AvailableTunings", 1);
        TuningsProvider.API_URIS.addURI("com.dolby.dax.api.Tunings", "AvailableTunings/#", 2);
        TuningsProvider.API_URIS.addURI("com.dolby.dax.api.Tunings", "AvailableTunings/dev/*", 3);
        TuningsProvider.API_URIS.addURI("com.dolby.dax.api.Tunings", "AvailableTunings/port/*", 4);
        TuningsProvider.API_URIS.addURI("com.dolby.dax.api.Tunings", "SelectedTunings", 5);
        TuningsProvider.API_URIS.addURI("com.dolby.dax.api.Tunings", "SelectedTunings/port/*", 6);
    }
    
    private DsContext getDsContext() {
        return DsContextFactory.getInstance(this.getContext());
    }
    
    public int delete(final Uri uri, final String s, final String[] array) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public String getType(final Uri uri) {
        switch (TuningsProvider.API_URIS.match(uri)) {
            default: {
                throw new IllegalArgumentException("Unknown URI");
            }
            case 1: {
                return "vnd.android.cursor.dir/vnd.com.dolby.dax.api.Tunings.AvailableTunings";
            }
            case 2: {
                return "vnd.android.cursor.item/vnd.com.dolby.dax.api.Tunings.AvailableTunings";
            }
            case 3: {
                return "vnd.android.cursor.item/vnd.com.dolby.dax.api.Tunings.AvailableTunings";
            }
            case 4: {
                return "vnd.android.cursor.item/vnd.com.dolby.dax.api.Tunings.AvailableTunings";
            }
            case 5: {
                return "vnd.android.cursor.dir/vnd.com.dolby.dax.api.Tunings.SelectedTunings";
            }
            case 6: {
                return "vnd.android.cursor.item/vnd.com.dolby.dax.api.Tunings.SelectedTunings";
            }
        }
    }
    
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public boolean onCreate() {
        return true;
    }
    
    public Cursor query(final Uri uri, final String[] array, final String s, final String[] array2, final String s2) {
        switch (TuningsProvider.API_URIS.match(uri)) {
            default: {
                throw new IllegalArgumentException("Unknown URI");
            }
            case 1: {
                return this.queryAvailableTunings(array, s2, s, array2);
            }
            case 2: {
                return this.queryAvailableTunings(array, s2, "_id = ?", uri.getLastPathSegment());
            }
            case 3: {
                return this.queryAvailableTunings(array, s2, "device = ?", uri.getLastPathSegment());
            }
            case 4: {
                return this.queryAvailableTunings(array, s2, "port = ?", uri.getLastPathSegment());
            }
            case 5: {
                return this.querySelectedTunings(array, null);
            }
            case 6: {
                return this.querySelectedTunings(array, Port.valueOf(uri.getLastPathSegment()));
            }
        }
    }
    
    protected Cursor queryAvailableTunings(final String[] array, final String s, final String s2, final String... array2) {
        return this.getDsContext().getProvider().getReadableDatabase().query("available_tunings", array, s2, array2, (String)null, (String)null, s);
    }
    
    protected Cursor querySelectedTunings(final String[] array, final Port port) {
        final DsContext dsContext = this.getDsContext();
        final MatrixCursor matrixCursor = new MatrixCursor(new String[] { "port", "device", "tuning" });
        for (final Port port2 : DsContext.SupportedPorts) {
            if (port == null || port2 == port) {
                final MatrixCursor.RowBuilder row = matrixCursor.newRow();
                row.add((Object)port2.name());
                String selectedTuningDevice;
                if ((selectedTuningDevice = dsContext.getSelectedTuningDevice(port2)).equals(dsContext.getDefaultTuningDevice(port2))) {
                    selectedTuningDevice = "";
                }
                row.add((Object)selectedTuningDevice);
                row.add((Object)dsContext.getSelectedTuning(port2).getName());
            }
        }
        return (Cursor)matrixCursor;
    }
    
    protected int setSelectedTuning(final Port port, final String s) {
        final DsContext dsContext = this.getDsContext();
        if (s == null || s.isEmpty()) {
            dsContext.selectDefaultTuning(port);
            return 1;
        }
        if (dsContext.setSelectedTuning(port, s)) {
            return 1;
        }
        return 0;
    }
    
    public int update(final Uri uri, final ContentValues contentValues, final String s, final String[] array) {
        switch (TuningsProvider.API_URIS.match(uri)) {
            default: {
                throw new IllegalArgumentException("Unknown URI");
            }
            case 1: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
            case 2: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
            case 3: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
            case 4: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
            case 5: {
                return this.setSelectedTuning(Port.valueOf(contentValues.getAsString("port")), contentValues.getAsString("device"));
            }
            case 6: {
                return this.setSelectedTuning(Port.valueOf(uri.getLastPathSegment()), contentValues.getAsString("device"));
            }
        }
    }
}
