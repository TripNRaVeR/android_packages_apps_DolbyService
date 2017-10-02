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

import android.database.Observable;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.Tuning;

public class DsContextChangeObservable extends Observable<OnDsContextChange> implements OnDsContextChange {
    public void onLoad(DsContext context) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onLoad(context);
            }
        }
    }

    public void onDapOnChanged(DsContext context, boolean on) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onDapOnChanged(context, on);
            }
        }
    }

    public void onSelectedProfileChanged(DsContext context) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedProfileChanged(context);
            }
        }
    }

    public void onSelectedProfileParameterChanged(DsContext context, Profile profile) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedProfileParameterChanged(context, profile);
            }
        }
    }

    public void onSelectedProfileNameChanged(DsContext context, Profile profile) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedProfileNameChanged(context, profile);
            }
        }
    }

    public void onSelectedProfileEndpointChanged(DsContext context, Port port, ProfileEndpoint endpoint) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedProfileEndpointChanged(context, port, endpoint);
            }
        }
    }

    public void onSelectedProfileIeqChanged(DsContext context, IeqPreset ieqPreset) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedProfileIeqChanged(context, ieqPreset);
            }
        }
    }

    public void onSelectedProfileGeqChanged(DsContext context, GeqPreset geqPreset) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedProfileGeqChanged(context, geqPreset);
            }
        }
    }

    public void onSelectedTuningChanged(DsContext context, Port port, Tuning tuning) {
        synchronized (this.mObservers) {
            for (OnDsContextChange obs : this.mObservers) {
                obs.onSelectedTuningChanged(context, port, tuning);
            }
        }
    }
}
