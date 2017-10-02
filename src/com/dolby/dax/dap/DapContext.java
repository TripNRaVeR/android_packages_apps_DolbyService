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

package com.dolby.dax.dap;

import com.dolby.dax.dap.ContentParameters;
import com.dolby.dax.dap.translator.ParamChangeObserver;
import com.dolby.dax.dap.translator.ParameterTranslator;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.Tuning;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DapContext
{
    public static final Set<AndroidDevice> kDapDevices;
    public static final Map<Port, AndroidDevice> kDeviceForPort;
    private final ContentParameters contentParameters;
    private final Map<AndroidDevice, DeviceParameters> deviceParameters;
    
    static {
        kDapDevices = (Set)ImmutableSet.of((Object)AndroidDevice.DEVICE_OUT_DEFAULT, (Object)AndroidDevice.DEVICE_OUT_SPEAKER, (Object)AndroidDevice.DEVICE_OUT_WIRED_HEADPHONE, (Object)AndroidDevice.DEVICE_OUT_BLUETOOTH_A2DP, (Object)AndroidDevice.DEVICE_OUT_AUX_DIGITAL, (Object)AndroidDevice.DEVICE_OUT_USB_DEVICE, (Object[])new AndroidDevice[] { AndroidDevice.DEVICE_OUT_REMOTE_SUBMIX });
        kDeviceForPort = (Map)ImmutableMap.builder().put((Object)Port.internal_speaker, (Object)AndroidDevice.DEVICE_OUT_SPEAKER).put((Object)Port.headphone_port, (Object)AndroidDevice.DEVICE_OUT_WIRED_HEADPHONE).put((Object)Port.bluetooth, (Object)AndroidDevice.DEVICE_OUT_BLUETOOTH_A2DP).put((Object)Port.hdmi, (Object)AndroidDevice.DEVICE_OUT_AUX_DIGITAL).put((Object)Port.usb, (Object)AndroidDevice.DEVICE_OUT_USB_DEVICE).put((Object)Port.miracast, (Object)AndroidDevice.DEVICE_OUT_REMOTE_SUBMIX).build();
    }
    
    DapContext(final Map<AndroidDevice, ParameterTranslator> map) {
        this.contentParameters = new ContentParameters((ParamChangeObserver)map.get(AndroidDevice.DEVICE_OUT_DEFAULT));
        this.deviceParameters = new EnumMap<AndroidDevice, DeviceParameters>(AndroidDevice.class);
        for (final Map.Entry<Port, AndroidDevice> entry : DapContext.kDeviceForPort.entrySet()) {
            this.deviceParameters.put(entry.getValue(), new DeviceParameters((ParamChangeObserver)map.get(entry.getValue())));
        }
    }
    
    protected DeviceParameters getDeviceParameters(final Port port) {
        return this.deviceParameters.get(DapContext.kDeviceForPort.get(port));
    }
    
    public void setGeqPreset(final GeqPreset geqPreset) {
        this.contentParameters.setGeqPreset(geqPreset);
    }
    
    public void setIeqPreset(final IeqPreset ieqPreset) {
        this.contentParameters.setIeqPreset(ieqPreset);
    }
    
    public void setProfile(final Profile profile) {
        this.contentParameters.setProfile(profile);
        final Iterator<DeviceParameters> iterator = this.deviceParameters.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().setProfile(profile);
        }
    }
    
    public void setProfileEndpoint(final Port port, final ProfileEndpoint profileEndpoint) {
        final DeviceParameters deviceParameters = this.getDeviceParameters(port);
        if (deviceParameters != null) {
            deviceParameters.setProfileEndpoint(profileEndpoint);
        }
    }
    
    public void setProfilePort(final ProfilePort profilePort) {
        final DeviceParameters deviceParameters = this.getDeviceParameters(profilePort.getPort());
        if (deviceParameters != null) {
            deviceParameters.setProfilePort(profilePort);
        }
    }
    
    public void setTuning(final Port port, final Tuning tuning) {
        final DeviceParameters deviceParameters = this.getDeviceParameters(port);
        if (deviceParameters != null) {
            deviceParameters.setTuning(tuning);
        }
    }
}
