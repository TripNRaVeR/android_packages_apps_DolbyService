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

package com.dolby.dax.service;

import android.util.Log;
import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.state.DsContext;
import com.dolby.dax.state.OnDsContextChange;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class DsProperty implements OnDsContextChange {

    class SystemProperties {

        private final Class clsSystemProperties;
        private final Method methodGetProperty;
        private final Method methodSetProperty;

        SystemProperties() {
            super();
            ReflectiveOperationException e;
            String message;
            try {
                this.clsSystemProperties = Class.forName("android.os.SystemProperties");
                this.methodSetProperty = this.clsSystemProperties.getMethod("set", new Class[]{String.class, String.class});
                this.methodGetProperty = this.clsSystemProperties.getMethod("get", new Class[]{String.class, String.class});
            } catch (ClassNotFoundException e2) {
                e = e2;
                message = "Can not reflect android.os.SystemProperties class";
                Log.e("DsProperty", message, e);
                throw new RuntimeException(message, e);
            } catch (NoSuchMethodException e3) {
                e = e3;
                message = "Can not reflect android.os.SystemProperties class";
                Log.e("DsProperty", message, e);
                throw new RuntimeException(message, e);
            }
        }

        void set(String property, String value) {
            ReflectiveOperationException e;
            String message;
            try {
                this.methodSetProperty.invoke(this.clsSystemProperties, new Object[]{property, value});
            } catch (IllegalAccessException e2) {
                e = e2;
                message = "Can not invoke android.os.SystemProperties.set method";
                Log.e("DsProperty", message, e);
                throw new RuntimeException(message, e);
            } catch (InvocationTargetException e3) {
                e = e3;
                message = "Can not invoke android.os.SystemProperties.set method";
                Log.e("DsProperty", message, e);
                throw new RuntimeException(message, e);
            }
        }

        void set(String property, boolean value) {
            set(property, value ? "on" : "off");
        }

        void setBoolean(String property, boolean value) {
            set(property, value ? "true" : "false");
        }
    }

    private final SystemProperties systemProperties;
    
    public DsProperty() {
        this.systemProperties = new SystemProperties();
    }
    
    private void setDialogEnhancerProperty(final ProfileEndpoint profileEndpoint) {
        final SystemProperties systemProperties = this.systemProperties;
        final int n = profileEndpoint.get(Parameter.dialog_enhancer_enable)[0];
        boolean b = false;
        if (n != 0) {
            b = true;
        }
        systemProperties.set("dolby.ds.dialogenhancer.state", b);
    }
    
    private void setGeqStateProperty(final Profile profile) {
        final SystemProperties systemProperties = this.systemProperties;
        final int n = profile.get(Parameter.graphic_equalizer_enable)[0];
        boolean b = false;
        if (n != 0) {
            b = true;
        }
        systemProperties.set("dolby.ds.graphiceq.state", b);
    }
    
    private void setIeqPresetProperty(final PresetType presetType) {
        this.systemProperties.set("dolby.ds.intelligenteq.preset", "ieq_" + presetType);
    }
    
    private void setIeqStateProperty(final PresetType presetType) {
        this.systemProperties.set("dolby.ds.intelligenteq.state", presetType != PresetType.off);
    }
    
    private void setMonoSpeakerProperty(final boolean b) {
        this.systemProperties.setBoolean("dolby.monospeaker", b);
    }
    
    private void setProfileNameProperty(final String s) {
        this.systemProperties.set("dolby.ds.profile.name", s);
    }
    
    private void setProfileProperties(final Profile geqStateProperty) {
        this.setProfileNameProperty(geqStateProperty.getName());
        this.setGeqStateProperty(geqStateProperty);
        this.setIeqStateProperty(geqStateProperty.getSelectedIeqPreset());
        this.setIeqPresetProperty(geqStateProperty.getSelectedIeqPreset());
    }
    
    private void setVirtualizerProperty(final Port port, final Tuning tuning, final ProfileEndpoint profileEndpoint) {
        final int n = tuning.get(Parameter.virtualizer_enable)[0];
        boolean b = false;
        if (n != 0) {
            final int n2 = profileEndpoint.get(Parameter.virtualizer_enable)[0];
            b = false;
            if (n2 != 0) {
                b = true;
            }
        }
        String s = "off";
        if (b) {
            switch (tuning.getEndpoint()) {
                case speaker: {
                    s = "speaker";
                    break;
                }
                case headphone: {
                    s = "headphone";
                    break;
                }
            }
        }
        this.systemProperties.set("dolby.ds.virt." + port, s);
    }
    
    private void setVolumeLevellerProperty(final ProfileEndpoint profileEndpoint) {
        final SystemProperties systemProperties = this.systemProperties;
        final int n = profileEndpoint.get(Parameter.volume_leveler_enable)[0];
        boolean b = false;
        if (n != 0) {
            b = true;
        }
        systemProperties.set("dolby.ds.volumeleveler.state", b);
    }
    
    @Override
    public void onDapOnChanged(final DsContext dsContext, final boolean stateProperty) {
        this.setStateProperty(stateProperty);
    }
    
    @Override
    public void onLoad(final DsContext dsContext) {
        this.setMonoSpeakerProperty(dsContext.getSelectedTuning(Port.internal_speaker).isMono());
        this.onDapOnChanged(dsContext, dsContext.getDapOn());
        this.onSelectedProfileChanged(dsContext);
    }
    
    @Override
    public void onSelectedProfileChanged(final DsContext dsContext) {
        this.setProfileProperties(dsContext.getSelectedProfile());
        int n = 1;
        for (final Port port : DsContext.SupportedPorts) {
            final ProfileEndpoint selectedProfileEndpoint = dsContext.getSelectedProfileEndpoint(port);
            this.setVirtualizerProperty(port, dsContext.getSelectedTuning(port), selectedProfileEndpoint);
            if (n != 0) {
                this.setDialogEnhancerProperty(selectedProfileEndpoint);
                this.setVolumeLevellerProperty(selectedProfileEndpoint);
                n = 0;
            }
        }
    }
    
    @Override
    public void onSelectedProfileEndpointChanged(final DsContext dsContext, final Port port, final ProfileEndpoint profileEndpoint) {
        this.setDialogEnhancerProperty(profileEndpoint);
        this.setVolumeLevellerProperty(profileEndpoint);
        this.setVirtualizerProperty(port, dsContext.getSelectedTuning(port), profileEndpoint);
    }
    
    @Override
    public void onSelectedProfileGeqChanged(final DsContext dsContext, final GeqPreset geqPreset) {
    }
    
    @Override
    public void onSelectedProfileIeqChanged(final DsContext dsContext, final IeqPreset ieqPreset) {
        this.setIeqPresetProperty(ieqPreset.getType());
    }
    
    @Override
    public void onSelectedProfileNameChanged(final DsContext dsContext, final Profile profile) {
        this.setProfileNameProperty(profile.getName());
    }
    
    @Override
    public void onSelectedProfileParameterChanged(final DsContext dsContext, final Profile profileProperties) {
        this.setProfileProperties(profileProperties);
    }
    
    @Override
    public void onSelectedTuningChanged(final DsContext dsContext, final Port port, final Tuning tuning) {
        this.setVirtualizerProperty(port, tuning, dsContext.getSelectedProfileEndpoint(port));
    }
    
    public void setStateProperty(final boolean b) {
        this.systemProperties.set("dolby.ds.state", b);
    }
}
