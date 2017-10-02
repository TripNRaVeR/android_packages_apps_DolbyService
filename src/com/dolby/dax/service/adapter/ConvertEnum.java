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

package com.dolby.dax.service.adapter;

import android.util.Log;
import com.dolby.dax.state.DsContext;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ProfileType;
import com.dolby.dax.model.Port;
import java.util.Iterator;

public class ConvertEnum
{
    public static Endpoint endpoint(final String s) {
        final Endpoint value = Endpoint.valueOf(s);
        if (value == null) {
            Log.e("ConvertEnum", "Unknown endpoint");
        }
        return value;
    }
    
    public static Parameter parameter(final String s) {
        final Parameter value = Parameter.valueOf(s);
        if (value == null) {
            Log.e("ConvertEnum", "Unknown DAP parameter");
        }
        return value;
    }
    
    public static Port port(final String s) {
        final Port value = Port.valueOf(s);
        if (value == null) {
            Log.e("ConvertEnum", "Unknown port");
        }
        return value;
    }
    
    public static ProfileType supportedProfile(final String s) {
        for (final ProfileType profileType : DsContext.SupportedProfiles) {
            if (profileType.name().equals(s)) {
                return profileType;
            }
        }
        Log.e("ConvertEnum", "Unsupported profile type");
        return null;
    }
}
