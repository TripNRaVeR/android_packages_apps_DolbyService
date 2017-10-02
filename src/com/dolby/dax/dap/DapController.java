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

import android.util.Log;
import com.dolby.dax.dap.commands.SetParamCommand;
import com.dolby.dax.dap.translator.ParameterTranslator;
import com.dolby.dax.state.OnDsContextChange;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.Tuning;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.Port;
import com.dolby.dax.state.DsContext;
import java.util.Iterator;
import java.util.EnumMap;
import java.util.Map;
import tripndroid.dolby.audio.api.DsLog;

public class DapController implements OnDsContextChange
{
    DapContext dapContext;
    Dap dapEffect;
    final int dapVersion;
    final Map<AndroidDevice, ParameterTranslator> paramTranslators;
    
    public DapController(final int dapVersion, final Dap dapEffect) {
        this.dapVersion = dapVersion;
        this.dapEffect = dapEffect;
        this.paramTranslators = new EnumMap<AndroidDevice, ParameterTranslator>(AndroidDevice.class);
        final Iterator<AndroidDevice> iterator = DapContext.kDapDevices.iterator();
        while (iterator.hasNext()) {
            this.paramTranslators.put(iterator.next(), new ParameterTranslator(dapVersion));
        }
        this.dapContext = new DapContext(this.paramTranslators);
    }
    
    @Override
    public void onDapOnChanged(final DsContext dsContext, final boolean enabled) {
        this.dapEffect.setEnabled(enabled);
    }
    
    @Override
    public void onLoad(final DsContext dsContext) {
        DsLog.log1("DapController", "onLoad(" + dsContext + ")");
        this.dapContext.setProfile(dsContext.getSelectedProfile());
        this.dapContext.setIeqPreset(dsContext.getSelectedProfileIeq());
        this.dapContext.setGeqPreset(dsContext.getSelectedProfileGeq());
        for (final Port port : DapContext.kDeviceForPort.keySet()) {
            final ProfileEndpoint selectedProfileEndpoint = dsContext.getSelectedProfileEndpoint(port);
            if (selectedProfileEndpoint != null) {
                this.dapContext.setProfileEndpoint(port, selectedProfileEndpoint);
            }
            else {
                Log.e("DapController", "No profile endpoint found for port " + port);
            }
            final ProfilePort selectedProfilePort = dsContext.getSelectedProfilePort(port);
            if (selectedProfilePort != null) {
                this.dapContext.setProfilePort(selectedProfilePort);
            }
            else {
                Log.e("DapController", "No profile port found for port " + port);
            }
            final Tuning selectedTuning = dsContext.getSelectedTuning(port);
            if (selectedTuning != null) {
                this.dapContext.setTuning(port, selectedTuning);
            }
            else {
                Log.e("DapController", "No tuning found for port " + port);
            }
        }
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
        this.dapEffect.setEnabled(dsContext.getDapOn());
    }
    
    @Override
    public void onSelectedProfileChanged(final DsContext dsContext) {
        DsLog.log1("DapController", "Profile Changed to " + dsContext.getSelectedProfile().getName());
        this.dapContext.setProfile(dsContext.getSelectedProfile());
        this.dapContext.setIeqPreset(dsContext.getSelectedProfileIeq());
        this.dapContext.setGeqPreset(dsContext.getSelectedProfileGeq());
        for (final Port port : DapContext.kDeviceForPort.keySet()) {
            final ProfileEndpoint selectedProfileEndpoint = dsContext.getSelectedProfileEndpoint(port);
            if (selectedProfileEndpoint != null) {
                this.dapContext.setProfileEndpoint(port, selectedProfileEndpoint);
            }
            final ProfilePort selectedProfilePort = dsContext.getSelectedProfilePort(port);
            if (selectedProfilePort != null) {
                this.dapContext.setProfilePort(selectedProfilePort);
            }
        }
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
    }
    
    @Override
    public void onSelectedProfileEndpointChanged(final DsContext dsContext, final Port port, final ProfileEndpoint profileEndpoint) {
        DsLog.log1("DapController", "onSelectedProfileEndpointChanged(" + port + ", " + profileEndpoint + ")");
        this.dapContext.setProfileEndpoint(port, profileEndpoint);
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
    }
    
    @Override
    public void onSelectedProfileGeqChanged(final DsContext dsContext, final GeqPreset geqPreset) {
        DsLog.log1("DapController", "onSelectedProfileGeqChanged(" + geqPreset + ")");
        this.dapContext.setGeqPreset(geqPreset);
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
    }
    
    @Override
    public void onSelectedProfileIeqChanged(final DsContext dsContext, final IeqPreset ieqPreset) {
        DsLog.log1("DapController", "onSelectedProfileIeqChanged(" + ieqPreset + ")");
        this.dapContext.setIeqPreset(ieqPreset);
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
    }
    
    @Override
    public void onSelectedProfileNameChanged(final DsContext dsContext, final Profile profile) {
        DsLog.log1("DapController", "onSelectedProfileNameChanged(" + profile + ")");
    }
    
    @Override
    public void onSelectedProfileParameterChanged(final DsContext dsContext, final Profile profile) {
        DsLog.log1("DapController", "onSelectedProfileParameterChanged(" + profile + ")");
        this.dapContext.setProfile(profile);
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
    }
    
    @Override
    public void onSelectedTuningChanged(final DsContext dsContext, final Port port, final Tuning tuning) {
        DsLog.log1("DapController", "onSelectedTuningChanged(" + tuning + ")");
        this.dapContext.setTuning(port, tuning);
        this.sendParameters(AndroidDevice.DEVICE_OUT_DEFAULT);
    }
    
    protected void sendParameters(final AndroidDevice androidDevice) {
        final SetParamCommand setParamCommand = new SetParamCommand(androidDevice.getNativeDeviceId());
        for (final Map.Entry<AndroidDevice, ParameterTranslator> entry : this.paramTranslators.entrySet()) {
            setParamCommand.setDeviceId(entry.getKey().getNativeDeviceId());
            entry.getValue().translatePending(setParamCommand);
        }
        setParamCommand.execute(this.dapEffect);
    }
    
    public void setDapEffect(final Dap dapEffect, final DsContext dsContext) {
        this.dapEffect = dapEffect;
        final SetParamCommand setParamCommand = new SetParamCommand(AndroidDevice.DEVICE_OUT_DEFAULT.getNativeDeviceId());
        for (final Map.Entry<AndroidDevice, ParameterTranslator> entry : this.paramTranslators.entrySet()) {
            setParamCommand.setDeviceId(entry.getKey().getNativeDeviceId());
            entry.getValue().translateAll(setParamCommand);
        }
        setParamCommand.execute(dapEffect);
        dapEffect.setEnabled(dsContext.getDapOn());
    }
}
