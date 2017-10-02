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

package com.dolby.dax.model;

public enum Port
{
    bluetooth("bluetooth", 4), 
    hdmi("hdmi", 1), 
    headphone_port("headphone_port", 3), 
    internal_speaker("internal_speaker", 0), 
    miracast("miracast", 2), 
    other("other", 6), 
    usb("usb", 5);
    
    private Port(final String s, final int n) {
    }
}
