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

public enum AndroidDevice
{
    DEVICE_OUT_AUX_DIGITAL("DEVICE_OUT_AUX_DIGITAL", 6, 1024), 
    DEVICE_OUT_BLUETOOTH_A2DP("DEVICE_OUT_BLUETOOTH_A2DP", 5, 128), 
    DEVICE_OUT_DEFAULT("DEVICE_OUT_DEFAULT", 1, 1073741824), 
    DEVICE_OUT_NONE("DEVICE_OUT_NONE", 0, 0), 
    DEVICE_OUT_REMOTE_SUBMIX("DEVICE_OUT_REMOTE_SUBMIX", 8, 32768), 
    DEVICE_OUT_SPEAKER("DEVICE_OUT_SPEAKER", 2, 2), 
    DEVICE_OUT_USB_DEVICE("DEVICE_OUT_USB_DEVICE", 7, 16384), 
    DEVICE_OUT_WIRED_HEADPHONE("DEVICE_OUT_WIRED_HEADPHONE", 4, 8), 
    DEVICE_OUT_WIRED_HEADSET("DEVICE_OUT_WIRED_HEADSET", 3, 4);
    
    protected final int nativeid;
    
    private AndroidDevice(final String s, final int n, final int nativeid) {
        this.nativeid = nativeid;
    }
    
    int getNativeDeviceId() {
        return this.nativeid;
    }
}
