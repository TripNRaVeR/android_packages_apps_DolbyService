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

package com.dolby.dax.state;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.dolby.dax.db.DatabaseProvider;
import com.dolby.dax.db.Provider;
import com.dolby.dax.model.DeviceTuning;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.xml.DeviceDataParser;
import com.dolby.dax.xml.DeviceDataValidator;
import com.dolby.dax.xml.TagIterator;
import com.dolby.dax.xml.ValidationException;
import com.dolby.dax.xml.model.DeviceData;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import tripndroid.dolby.audio.api.DsLog;
import org.xmlpull.v1.XmlPullParserException;

public class DsContextFactory
{
    private static DsContext dsContext;
    private static final Object lock;
    private static final File vendorDefaultXml;
    
    static {
        vendorDefaultXml = new File("/system/etc/dolby/dax-default.xml");
        lock = new Object();
    }
    
    static String getChangedSignature(final File file, final Provider provider) {
        final String signature = getSignature(file);
        final SQLiteDatabase readableDatabase = provider.getReadableDatabase();
        if (!readableDatabase.isDatabaseIntegrityOk()) {
            Log.e("DsContextFactory", "Database integrity check failed. Reloading tuning XML.");
            readableDatabase.close();
            return signature;
        }
        readableDatabase.close();
        if (!signature.equals(provider.getDefaultXmlSignature())) {
            Log.e("DsContextFactory", "Tuning XML file changed. Reloading tuning XML.");
            return signature;
        }
        return null;
    }
    
    public static DsContext getInstance(Context context) {
        String message;
        DsContext dsContext = DsContextFactory.dsContext;
        synchronized (lock) {
            if (dsContext == null) {
                try {
                    Provider provider = new DatabaseProvider(context);
                    initDatabase(provider, vendorDefaultXml);
                    dsContext = new DsContextImpl(provider, new DsContextChangeObservable());
                    dsContext.load();
                } catch (ValidationException e) {
                    if (e.isXmlException()) {
                        message = "Can not parse tuning xml file: " + vendorDefaultXml;
                    } else {
                        message = "Tuning data is not valid in xml file: " + vendorDefaultXml;
                    }
                    Log.e("DsContextFactory", message, e);
                    throw new RuntimeException(message, e);
                }
            }
            dsContext = dsContext;
        }
        return dsContext;
    }
    
    private static String getSignature(final File file) {
        try {
            return Files.hash(file, Hashing.md5()).toString();
        } catch (FileNotFoundException e) {
            Log.e("DsContextFactory", "File " + file + " is not present");
            return "";
        } catch (IOException e2) {
            Log.e("DsContextFactory", "File " + file + " can not be read!", e2);
            return "";
        }
    }
    
    static void initDatabase(final Provider provider, final File file) throws ValidationException {
        if (!file.exists()) {
            final String string = "Please use tuning tool to generate default tuning file: " + file;
            Log.wtf("DsContextFactory", string);
            throw new RuntimeException(string);
        }
        final DeviceData loadXmlIfChanged = loadXmlIfChanged(file, provider);
        if (loadXmlIfChanged != null) {
            new DeviceDataValidator(loadXmlIfChanged).validate();
            populateDatabase(loadXmlIfChanged, provider);
        }
    }
    
    static DeviceData loadXmlIfChanged(final File file, final Provider provider) throws ValidationException {
        final String changedSignature = getChangedSignature(file, provider);
        if (changedSignature != null) {
            try {
                final DeviceData parse = new DeviceDataParser(new TagIterator(new FileInputStream(file))).parse();
                parse.setSignature(changedSignature);
                DsLog.log1("DsContextFactory", "Successfully parsed tuning XML file: " + file);
                return parse;
            }
            catch (IOException ex) {
                Log.wtf("DsContextFactory", "File " + file + " can not be read.", (Throwable)ex);
                return null;
            }
            catch (XmlPullParserException ex2) {
                Log.wtf("DsContextFactory", "File " + file + " is not a valid XML file.", (Throwable)ex2);
                return null;
            }
        }
        DsLog.log1("DsContextFactory", "Tuning XML file " + file + " has not changed since last time.");
        return null;
    }
    
    static void populateDatabase(final DeviceData deviceData, final Provider provider) {
        provider.clear();
        provider.beginTransaction();
        final Iterator<IeqPreset> iterator = deviceData.ieqPresets.iterator();
        while (iterator.hasNext()) {
            provider.create(iterator.next());
        }
        final Iterator<GeqPreset> iterator2 = deviceData.geqPresets.iterator();
        while (iterator2.hasNext()) {
            provider.create(iterator2.next());
        }
        final Iterator<Profile> iterator3 = deviceData.profiles.iterator();
        while (iterator3.hasNext()) {
            provider.create(iterator3.next());
        }
        final Iterator<ProfileEndpoint> iterator4 = deviceData.profileEndpoints.iterator();
        while (iterator4.hasNext()) {
            provider.create(iterator4.next());
        }
        final Iterator<ProfilePort> iterator5 = deviceData.profilePorts.iterator();
        while (iterator5.hasNext()) {
            provider.create(iterator5.next());
        }
        final Iterator<Tuning> iterator6 = deviceData.tunings.iterator();
        while (iterator6.hasNext()) {
            provider.create(iterator6.next());
        }
        final Iterator<DeviceTuning> iterator7 = deviceData.deviceTunings.iterator();
        while (iterator7.hasNext()) {
            provider.create(iterator7.next());
        }
        provider.setDefaultXmlSignature(deviceData.getSignature());
        provider.commitTransaction();
        DsLog.log1("DsContextFactory", "Data from tuning XML file successfully loaded into database.");
    }
}
