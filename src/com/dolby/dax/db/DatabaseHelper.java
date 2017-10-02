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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.Parameter;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "dax.sqlite3", null, 1);
    }

    private String boolColumn(String name) {
        return String.format("%1$s INTEGER CHECK (%1$s = 0 OR %1$s = 1)", new Object[]{name});
    }

    private String stringColumn(String name) {
        return name + " STRING NOT NULL";
    }

    private String foreignKey(String name, String foreignTable) {
        return name + " NOT NULL REFERENCES " + foreignTable + "(" + "_id" + ")";
    }

    private static String unique(String... columns) {
        return "UNIQUE(" + Joiner.on(", ").join((Object[]) columns) + ")";
    }

    private static List<String> columns(String... columnDefs) {
        return Arrays.asList(columnDefs);
    }

    private static List<String> constraints(String... constraintDefs) {
        return Arrays.asList(constraintDefs);
    }

    private void table(SQLiteDatabase db, String table, Set<Parameter> parameters, List<String> columns, List<String> constraints) {
        Joiner columnJoiner = Joiner.on(",\n");
        StringBuilder stmt = new StringBuilder();
        stmt.append("CREATE TABLE ").append(table).append("(\n");
        stmt.append("_id").append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n");
        columnJoiner.appendTo(stmt, (Iterable) columns);
        if (parameters != null) {
            stmt.append(",\n");
            Joiner.on(" STRING NOT NULL,\n").appendTo(stmt, (Iterable) parameters);
            stmt.append(" STRING NOT NULL");
        }
        if (constraints != null) {
            stmt.append(",\n");
            columnJoiner.appendTo(stmt, (Iterable) constraints);
        }
        stmt.append(");");
        db.execSQL(stmt.toString());
    }

    private void table2(SQLiteDatabase db, String table1, String table2, Set<Parameter> parameters, List<String> columns, List<String> constraints) {
        table(db, table1, parameters, columns, constraints);
        table(db, table2, parameters, columns, constraints);
    }

    public void onOpen(SQLiteDatabase db) {
        setWriteAheadLoggingEnabled(true);
        super.onOpen(db);
    }

    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

    public void onCreate(SQLiteDatabase db) {
        List columns = columns(stringColumn("key"), stringColumn("value"));
        String[] strArr = new String[1];
        strArr[0] = unique("key");
        SQLiteDatabase sQLiteDatabase = db;
        table(sQLiteDatabase, "configuration", null, columns, constraints(strArr));
        Set set = IeqPreset.validParams;
        columns = columns(stringColumn("preset_type"));
        strArr = new String[1];
        strArr[0] = unique("preset_type");
        sQLiteDatabase = db;
        table(sQLiteDatabase, "ieq_presets", set, columns, constraints(strArr));
        Set set2 = Profile.validParams;
        List columns2 = columns(stringColumn("profile_type"), stringColumn("name"), stringColumn("selected_ieq_preset_type"), stringColumn("selected_geq_preset_type"));
        strArr = new String[1];
        strArr[0] = unique("profile_type");
        sQLiteDatabase = db;
        table2(sQLiteDatabase, "default_profiles", "profiles", set2, columns2, constraints(strArr));
        set2 = GeqPreset.validParams;
        columns2 = columns(stringColumn("preset_type"), stringColumn("profile_type"));
        strArr = new String[1];
        strArr[0] = unique("preset_type", "profile_type");
        sQLiteDatabase = db;
        table2(sQLiteDatabase, "default_geq_presets", "geq_presets", set2, columns2, constraints(strArr));
        set2 = ProfileEndpoint.validParams;
        columns2 = columns(stringColumn("profile_type"), stringColumn("endpoint"));
        strArr = new String[1];
        strArr[0] = unique("profile_type", "endpoint");
        sQLiteDatabase = db;
        table2(sQLiteDatabase, "default_profile_endpoints", "profile_endpoints", set2, columns2, constraints(strArr));
        set2 = ProfilePort.validParams;
        columns2 = columns(stringColumn("profile_type"), stringColumn("port"));
        strArr = new String[1];
        strArr[0] = unique("profile_type", "port");
        sQLiteDatabase = db;
        table2(sQLiteDatabase, "default_profile_ports", "profile_ports", set2, columns2, constraints(strArr));
        set = Tuning.validParams;
        columns = columns(stringColumn("name"), stringColumn("endpoint"), boolColumn("is_mono"), boolColumn("is_readonly"));
        strArr = new String[1];
        strArr[0] = unique("name");
        sQLiteDatabase = db;
        table(sQLiteDatabase, "tunings", set, columns, constraints(strArr));
        columns = columns(stringColumn("device"), stringColumn("port"), foreignKey("tuning_id", "tunings"));
        strArr = new String[1];
        strArr[0] = unique("device");
        sQLiteDatabase = db;
        table(sQLiteDatabase, "device_tunings", null, columns, constraints(strArr));
        createAvailableTuningsView(db);
    }

    void createAvailableTuningsView(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW available_tunings AS  SELECT device_tunings._id AS _id,device_tunings.device AS device,device_tunings.port AS port,tunings.name AS tuning FROM device_tunings JOIN tunings ON device_tunings.tuning_id = tunings._id WHERE device_tunings.device NOT IN ('" + Joiner.on("', '").join(Port.values()) + "');");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS configuration");
        db.execSQL("DROP TABLE IF EXISTS default_profile_endpoints");
        db.execSQL("DROP TABLE IF EXISTS profile_endpoints");
        db.execSQL("DROP TABLE IF EXISTS default_profile_ports");
        db.execSQL("DROP TABLE IF EXISTS profile_ports");
        db.execSQL("DROP TABLE IF EXISTS default_profiles");
        db.execSQL("DROP TABLE IF EXISTS profiles");
        db.execSQL("DROP TABLE IF EXISTS ieq_presets");
        db.execSQL("DROP TABLE IF EXISTS default_geq_presets");
        db.execSQL("DROP TABLE IF EXISTS geq_presets");
        db.execSQL("DROP TABLE IF EXISTS device_tunings");
        db.execSQL("DROP TABLE IF EXISTS tunings");
        db.execSQL("DROP VIEW IF EXISTS available_tunings");
        onCreate(db);
    }
}
