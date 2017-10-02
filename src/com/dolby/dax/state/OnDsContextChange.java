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

import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.Tuning;

public interface OnDsContextChange
{
    void onDapOnChanged(final DsContext p0, final boolean p1);
    
    void onLoad(final DsContext p0);
    
    void onSelectedProfileChanged(final DsContext p0);
    
    void onSelectedProfileEndpointChanged(final DsContext p0, final Port p1, final ProfileEndpoint p2);
    
    void onSelectedProfileGeqChanged(final DsContext p0, final GeqPreset p1);
    
    void onSelectedProfileIeqChanged(final DsContext p0, final IeqPreset p1);
    
    void onSelectedProfileNameChanged(final DsContext p0, final Profile p1);
    
    void onSelectedProfileParameterChanged(final DsContext p0, final Profile p1);
    
    void onSelectedTuningChanged(final DsContext p0, final Port p1, final Tuning p2);
}
