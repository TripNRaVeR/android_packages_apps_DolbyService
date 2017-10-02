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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import com.dolby.api.DsParams;
import com.dolby.api.IDs;
import com.dolby.api.IDs.Stub;
import com.dolby.api.IDsCallbacks;
import com.dolby.api.IDsDeathHandler;
import com.dolby.dax.model.ProfileType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import tripndroid.dolby.audio.api.DsClientInfo;
import tripndroid.dolby.audio.api.DsLog;
import tripndroid.dolby.audio.api.DsProfileName;

public class DsService extends Service {
    private DsAccessRightManager arManager_ = null;
    private Method audioManagerIsAppInFocus_;
    private final Stub binder_ = new Stub() {
        public void iRegisterVisualizerData(int handle) {
            DsLog.log2("DsService", "Add a visualizer handle " + handle);
            synchronized (DsService.this.lockDolbyContext_) {
                DsService.this.visManager_.register(handle);
            }
        }

        public void iUnregisterVisualizerData(int handle) {
            DsLog.log2("DsService", "remove a visualzier handle " + handle);
            synchronized (DsService.this.lockDolbyContext_) {
                DsService.this.visManager_.unregister(handle);
            }
        }

        public void iRegisterDeathHandler(int handle, IDsDeathHandler dh) {
            DsLog.log2("DsService", "iRegisterDeathHandler");
            if (dh != null) {
                synchronized (DsService.this.lockDolbyContext_) {
                    DsClientDeathHandler clientDeathHandler = new DsClientDeathHandler(dh, handle);
                    try {
                        clientDeathHandler.linkToDeath();
                        DsService.this.dsClientDeathHandlerList_.put(dh.asBinder(), clientDeathHandler);
                    } catch (RemoteException e) {
                        Log.e("DsService", "DsService  iRegisterDeathHandler() could not link to " + dh + " binder death");
                    }
                }
            }
        }

        public void iUnregisterDeathHandler(int handle, IDsDeathHandler dh) {
            DsLog.log2("DsService", "iUnregisterDeathHandler");
            if (dh != null) {
                synchronized (DsService.this.lockDolbyContext_) {
                    DsClientDeathHandler clientDeathHandler = (DsClientDeathHandler) DsService.this.dsClientDeathHandlerList_.remove(dh.asBinder());
                    if (clientDeathHandler != null) {
                        clientDeathHandler.unlinkToDeath();
                    }
                }
            }
        }

        public void iRegisterDsAccess(int handle, DsClientInfo info) {
            DsLog.log2("DsService", "iRegisterDsAccess");
            synchronized (DsService.this.lockDolbyContext_) {
                if (info != null) {
                    DsService.this.dsClientInfoMap_.put(Integer.valueOf(handle), info);
                    DsService.this.arManager_.addDsConnectedApp(handle, info.getPackageName(), info.getConnectionBridge());
                }
            }
        }

        public void iUnregisterDsAccess(int handle) {
            DsLog.log2("DsService", "iUnregisterDsAccess");
            synchronized (DsService.this.lockDolbyContext_) {
                DsService.this.arManager_.unRegisterDsVersion(handle);
                DsService.this.arManager_.removeDsConnectedApp(handle);
                DsService.this.dsClientInfoMap_.remove(Integer.valueOf(handle));
            }
        }

        public void iRegisterCallback(int handle, IDsCallbacks cb, int version) {
            if (cb != null) {
                synchronized (DsService.this.lockDolbyContext_) {
                    DsService.this.cbkManager_.register(cb, handle, version);
                    if (version == 1) {
                        DsService.this.arManager_.registerDsVersion(handle, version);
                    }
                    DsLog.log2("DsService", "iRegisterCallback");
                }
            }
        }

        public void iUnregisterCallback(int handle, IDsCallbacks cb, int version) {
            if (cb != null) {
                synchronized (DsService.this.lockDolbyContext_) {
                    if (version == 1) {
                        DsService.this.arManager_.unRegisterDsVersion(handle);
                    }
                    DsService.this.cbkManager_.unregister(cb, version);
                    if (DsService.this.arManager_.isGlobalSettings(handle)) {
                        DsService.this.dsManager_.saveDsStateAndSettings();
                    }
                    DsLog.log2("DsService", "iUnregisterCallback");
                }
            }
        }

        public int iGetState(int Device, int[] on) {
            int i = 0;
            DsLog.log2("DsService", "DsService.iGetState()");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                if (on != null) {
                    try {
                        if (DsService.this.dsManager_.getDsOn()) {
                            i = 1;
                        }
                        on[0] = i;
                        error = 0;
                    } catch (DeadObjectException e) {
                        Log.e("DsService", "DeadObjectException in iGetState");
                        e.printStackTrace();
                        error = -2;
                    } catch (Exception e2) {
                        Log.e("DsService", "Exception in iGetState");
                        e2.printStackTrace();
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iSetState(int handle, int Device, boolean on) {
            DsLog.log2("DsService", "DsService.iSetState(" + on + ")" + ", handle = " + handle);
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    boolean isCleared = DsService.this.arManager_.doAccessForLegacyClient(handle, true);
                    if (DsService.this.arManager_.isAppAccessPermitted(handle)) {
                        error = DsService.this.doSetDsOn(handle, on);
                    } else {
                        error = -5;
                    }
                    if (isCleared) {
                        DsService.this.arManager_.doAccessForLegacyClient(handle, false);
                    }
                } catch (DeadObjectException e) {
                    Log.e("DsService", "DeadObjectException in iSetState");
                    e.printStackTrace();
                    error = -2;
                } catch (Exception e2) {
                    Log.e("DsService", "Exception in iSetState");
                    e2.printStackTrace();
                }
            }
            return error;
        }

        public int iGetOffType(int[] offType) {
            DsLog.log2("DsService", "iGetOffType");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = 0;
            synchronized (DsService.this.lockDolbyContext_) {
                if (offType != null) {
                    try {
                        offType[0] = DsService.this.dsManager_.getOffType();
                    } catch (DeadObjectException e) {
                        Log.e("DsService", "DeadObjectException in iGetOffType");
                        e.printStackTrace();
                        error = -2;
                    } catch (IllegalStateException e2) {
                        Log.e("DsService", "IllegalStateException in iGetOffType");
                        e2.printStackTrace();
                        error = -6;
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetDsServiceVersion(String[] version) {
            DsLog.log2("DsService", "DsService.getDsVersion");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (version != null) {
                    version[0] = DsService.this.dsManager_.getDsVersion();
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetDapLibraryVersion(String[] version) {
            DsLog.log2("DsService", "DsService.iGetDapLibraryVersion");
            int error = -6;
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            synchronized (DsService.this.lockDolbyContext_) {
                if (version != null) {
                    try {
                        version[0] = DsService.this.dsManager_.getDapVersion();
                        if (version[0] == null) {
                            error = -6;
                        } else {
                            error = 0;
                        }
                    } catch (DeadObjectException e) {
                        Log.e("DsService", "DeadObjectException in iGetDapLibraryVersion");
                        e.printStackTrace();
                        error = -2;
                    } catch (Exception e2) {
                        Log.e("DsService", "Exception in iGetDapLibraryVersion");
                        e2.printStackTrace();
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetUdcLibraryVersion(String[] version) {
            DsLog.log2("DsService", "DsService.iGetUdcLibraryVersion()");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (version != null) {
                    version[0] = "UDCv1.7.2";
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iSetParameter(int handle, int device, int profile, int paramId, int[] values) {
            DsLog.log2("DsService", "DsService.iSetParameter()");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            if (DsService.this.dsManager_.isMonoInternalSpeaker() && paramId == DsParams.DolbyVirtualSpeakerVirtualizerControl.toInt() && values[0] != 0) {
                return -1;
            }
            if (paramId == DsParams.IntelligentEqualizerEnable.toInt() && ((values[0] != 0 && DsService.this.dsManager_.getIeqPreset(profile) == 0) || (values[0] == 0 && DsService.this.dsManager_.getIeqPreset(profile) != 0))) {
                return -1;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    boolean isCleared = DsService.this.arManager_.doAccessForLegacyClient(handle, true);
                    if (!DsService.this.arManager_.isAppAccessPermitted(handle)) {
                        error = -5;
                    } else if (DsService.this.dsManager_.setParameter(profile, paramId, values)) {
                        if (DsService.this.arManager_.isGlobalSettings(handle) && profile == DsService.this.dsManager_.getSelectedProfile()) {
                            DsService.this.cbkManager_.invokeCallback(3, handle, profile, 0, null, null);
                            DsService.this.cbkManager_.invokeDs1Callback(3, handle, profile, 0, null, null);
                        }
                        error = 0;
                    } else {
                        error = -1;
                    }
                    if (isCleared) {
                        DsService.this.arManager_.doAccessForLegacyClient(handle, false);
                    }
                } catch (DeadObjectException e) {
                    Log.e("DsService", "DeadObjectException in iSetParameter");
                    e.printStackTrace();
                    error = -2;
                } catch (Exception e2) {
                    Log.e("DsService", "Exception in iSetParameter");
                    e2.printStackTrace();
                }
            }
            return error;
        }

        public int iGetParameter(int device, int profile, int paramId, int[] values) {
            DsLog.log2("DsService", "DsService.iGetParameter()");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                if (values != null) {
                    try {
                        int[] realParam = DsService.this.dsManager_.getParameter(profile, paramId);
                        if (realParam != null) {
                            System.arraycopy(realParam, 0, values, 0, realParam.length);
                            error = 0;
                        } else {
                            error = 4;
                        }
                    } catch (Exception e) {
                        Log.e("DsService", "Exception in iGetParameter");
                        e.printStackTrace();
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iSetIeqPreset(int handle, int device, int preset) {
            DsLog.log2("DsService", "DsService.iSetIeqPreset");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    if (DsService.this.arManager_.isAppAccessPermitted(handle)) {
                        int profile = DsService.this.dsManager_.getSelectedProfile();
                        if (DsService.this.dsManager_.setIeqPreset(profile, preset)) {
                            if (DsService.this.arManager_.isGlobalSettings(handle)) {
                                DsService.this.cbkManager_.invokeCallback(3, handle, profile, 0, null, null);
                                DsService.this.cbkManager_.invokeDs1Callback(3, handle, profile, 0, null, null);
                            }
                            error = 0;
                        }
                    } else {
                        error = -5;
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("DsService", "IllegalArgumentException in setIeqPreset");
                    e.printStackTrace();
                    error = -1;
                } catch (DeadObjectException e2) {
                    Log.e("DsService", "DeadObjectException in setIeqPreset");
                    e2.printStackTrace();
                    error = -2;
                } catch (Exception e3) {
                    Log.e("DsService", "Exception in setIeqPreset");
                    e3.printStackTrace();
                }
            }
            return error;
        }

        public int iGetIeqPreset(int device, int[] preset) {
            DsLog.log2("DsService", "DsService.iGetIeqPreset");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                if (preset != null) {
                    try {
                        preset[0] = DsService.this.dsManager_.getIeqPreset(DsService.this.dsManager_.getSelectedProfile());
                        error = 0;
                    } catch (IllegalArgumentException e) {
                        Log.e("DsService", "IllegalArgumentException in getIeqPreset");
                        e.printStackTrace();
                        error = -1;
                    } catch (Exception e2) {
                        Log.e("DsService", "Exception in getIeqPreset");
                        e2.printStackTrace();
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetIeqPresetCount(int device, int[] count) {
            DsLog.log2("DsService", "DsService.iGetIeqPresetCount()");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (count != null) {
                    count[0] = 4;
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iSetProfile(int handle, int device, int profile) {
            DsLog.log2("DsService", "DsService.iSetProfile(" + profile + ")");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    boolean isCleared = DsService.this.arManager_.doAccessForLegacyClient(handle, true);
                    if (!DsService.this.arManager_.isAppAccessPermitted(handle)) {
                        error = -5;
                    } else if (DsService.this.doSetSelectedProfile(handle, profile)) {
                        error = 0;
                    }
                    if (isCleared) {
                        DsService.this.arManager_.doAccessForLegacyClient(handle, false);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("DsService", "IllegalArgumentException in setSelectedProfile");
                    e.printStackTrace();
                    error = -1;
                } catch (DeadObjectException e2) {
                    Log.e("DsService", "DeadObjectException in setSelectedProfile");
                    e2.printStackTrace();
                    error = -2;
                } catch (Exception e3) {
                    Log.e("DsService", "Exception in setSelectedProfile");
                    e3.printStackTrace();
                }
            }
            return error;
        }

        public int iGetProfile(int device, int[] profile) {
            DsLog.log2("DsService", "DsService.iGetProfile");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (profile != null) {
                    profile[0] = DsService.this.dsManager_.getSelectedProfile();
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iResetProfile(int handle, int device, int profile) {
            DsLog.log2("DsService", "DsService.iResetProfile");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    boolean isCleared = DsService.this.arManager_.doAccessForLegacyClient(handle, true);
                    if (!DsService.this.arManager_.isAppAccessPermitted(handle)) {
                        error = -5;
                    } else if (DsService.this.dsManager_.resetProfile(profile)) {
                        if (DsService.this.arManager_.isGlobalSettings(handle)) {
                            DsService.this.cbkManager_.invokeCallback(3, handle, profile, 0, null, null);
                            DsService.this.cbkManager_.invokeDs1Callback(3, handle, profile, 0, null, null);
                            if (profile >= 4) {
                                DsProfileName name = DsService.this.dsManager_.getProfileName(profile);
                                DsService.this.cbkManager_.invokeCallback(10, handle, profile, 0, name.getCurrentName(), null);
                                DsService.this.cbkManager_.invokeDs1Callback(10, handle, profile, 0, name.getCurrentName(), null);
                            }
                            DsService.this.dsManager_.saveDsProfileSettings();
                        }
                        error = 0;
                    }
                    if (isCleared) {
                        DsService.this.arManager_.doAccessForLegacyClient(handle, false);
                    }
                } catch (DeadObjectException e) {
                    Log.e("DsService", "DeadObjectException in resetProfile");
                    e.printStackTrace();
                } catch (IllegalArgumentException e2) {
                    Log.e("DsService", "IllegalArgumentException in resetProfile");
                    e2.printStackTrace();
                    error = -1;
                } catch (UnsupportedOperationException e3) {
                    Log.e("DsService", "UnsupportedOperationException in resetProfile");
                    e3.printStackTrace();
                    error = -1;
                } catch (Exception e4) {
                    Log.e("DsService", "Exception in resetProfile");
                    e4.printStackTrace();
                }
            }
            return error;
        }

        public int iGetProfileCount(int device, int[] count) {
            DsLog.log2("DsService", "DsService.getProfileCount()");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (count != null) {
                    count[0] = DsService.this.dsManager_.getProfileCount();
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iRequestAccessRight(int handle, int type) {
            boolean z = true;
            DsLog.log2("DsService", "DsService.iRequestAccessRight");
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    error = DsService.this.arManager_.requestAccessRight(handle, type, (AudioManager) DsService.this.getSystemService("audio"));
                    DsService.this.arManager_.registerDsVersion(handle, 2);
                    if ((type == 2 || type == 1) && error == 0) {
                        Intent broadcast = new Intent("DS_ACCESS_RIGHT_GRANTED");
                        Bundle bundle = new Bundle();
                        String str = "DS_GLOBAL";
                        if (2 != type) {
                            z = false;
                        }
                        bundle.putBoolean(str, z);
                        broadcast.putExtras(bundle);
                        DsService.this.sendBroadcast(broadcast);
                    }
                } catch (Exception e) {
                    Log.e("DsService", "Exception in iSetAccessLock");
                    e.printStackTrace();
                }
            }
            return error;
        }

        public int iAbandonAccessRight(int handle, int type) {
            DsLog.log2("DsService", "DsService.iAbandonAccessRight");
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    AudioManager audioManager = (AudioManager) DsService.this.getSystemService("audio");
                    if (DsService.this.arManager_.isGlobalSettings(handle)) {
                        DsService.this.dsManager_.saveDsStateAndSettings();
                    }
                    error = DsService.this.arManager_.abandonAccessRight(handle, type, audioManager);
                    if (type == 1 && !DsService.this.arManager_.isDsFocusGranted() && error == 0) {
                        Intent broadcast = new Intent("DS_ACCESS_RIGHT_GRANTED");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("DS_GLOBAL", true);
                        broadcast.putExtras(bundle);
                        DsService.this.sendBroadcast(broadcast);
                    }
                    DsService.this.arManager_.unRegisterDsVersion(handle);
                } catch (Exception e) {
                    Log.e("DsService", "Exception in iAbandonAccessRight");
                    e.printStackTrace();
                }
            }
            return error;
        }

        public int iCheckAccessRight(int handle, int type, int[] state) {
            DsLog.log2("DsService", "DsService.iCheckAccessRight");
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                if (state != null) {
                    try {
                        state[0] = DsService.this.arManager_.checkAccessRight(handle, type);
                        error = 0;
                    } catch (Exception e) {
                        Log.e("DsService", "Exception in iGetAccessLock");
                        e.printStackTrace();
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetProfileModified(int device, int profile, boolean[] flag) {
            DsLog.log2("DsService", "DsService.iGetProfileModified");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (flag != null) {
                    flag[0] = DsService.this.dsManager_.isProfileModified(profile);
                    DsLog.log2("DsService", "DsService.iGetProfileModified " + flag[0]);
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetMonoSpeaker(boolean[] mono) {
            DsLog.log2("DsService", "DsService.iGetMonoSpeaker");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (mono != null) {
                    mono[0] = DsService.this.dsManager_.isMonoInternalSpeaker();
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iGetParamLength(int paramId, int[] len) {
            DsLog.log2("DsService", "DsService.iGetParamLength");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error;
            synchronized (DsService.this.lockDolbyContext_) {
                if (len != null) {
                    len[0] = DsService.this.dsManager_.getParamLength(paramId);
                    error = 0;
                } else {
                    error = -1;
                }
            }
            return error;
        }

        public int iSetProfileName(int handle, int profile, DsProfileName name) {
            DsLog.log2("DsService", "DsService.iSetProfileName");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    if (!DsService.this.arManager_.isAppAccessPermitted(handle)) {
                        error = -5;
                    } else if (DsService.this.dsManager_.setProfileName(profile, name)) {
                        if (name.getCurrentName() != null) {
                            DsService.this.cbkManager_.invokeCallback(10, handle, profile, 0, name.getCurrentName(), null);
                            DsService.this.cbkManager_.invokeDs1Callback(10, handle, profile, 0, name.getCurrentName(), null);
                        }
                        DsService.this.dsManager_.saveDsProfileSettings();
                        error = 0;
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("DsService", "IllegalArgumentException in iSetProfileName");
                    e.printStackTrace();
                    error = -1;
                } catch (UnsupportedOperationException e2) {
                    Log.e("DsService", "UnsupportedOperationException in iSetProfileName");
                    e2.printStackTrace();
                    error = -1;
                } catch (Exception e3) {
                    Log.e("DsService", "Exception in setProfileName");
                    e3.printStackTrace();
                }
            }
            return error;
        }

        public int[] iGetProfileParameter(String profileType, String parameter) {
            if (DsService.this.dsManager_ != null) {
                return DsService.this.dsManager_.getProfileParameter(profileType, parameter);
            }
            throw new RuntimeException("DS service not initialized.");
        }

        public int[] iGetProfileEndpointParameter(String profileType, String endpoint, String parameter) {
            if (DsService.this.dsManager_ != null) {
                return DsService.this.dsManager_.getProfileEndpointParameter(profileType, endpoint, parameter);
            }
            throw new RuntimeException("DS service not initialized.");
        }

        public int[] iGetProfilePortParameter(String profileType, String port, String parameter) {
            if (DsService.this.dsManager_ != null) {
                return DsService.this.dsManager_.getProfilePortParameter(profileType, port, parameter);
            }
            throw new RuntimeException("DS service not initialized.");
        }

        public int[] iGetTuningParameter(String port, String parameter) {
            if (DsService.this.dsManager_ != null) {
                return DsService.this.dsManager_.getTuningParameter(port, parameter);
            }
            throw new RuntimeException("DS service not initialized.");
        }

        public int iGetProfileName(int handle, int profile, DsProfileName[] name) {
            DsLog.log2("DsService", "DsService.iGetProfileName");
            if (DsService.this.dsManager_ == null) {
                return -3;
            }
            int error = -6;
            synchronized (DsService.this.lockDolbyContext_) {
                if (name != null) {
                    try {
                        name[0] = DsService.this.dsManager_.getProfileName(profile);
                        error = 0;
                    } catch (IllegalArgumentException e) {
                        Log.e("DsService", "IllegalArgumentException in iGetProfileName");
                        e.printStackTrace();
                        error = -1;
                    } catch (Exception e2) {
                        Log.e("DsService", "Exception in iGetProfileName");
                        e2.printStackTrace();
                    }
                } else {
                    error = -1;
                }
            }
            return error;
        }
    };
    private DsCallbackManager cbkManager_ = null;
    private final HashMap<IBinder, DsClientDeathHandler> dsClientDeathHandlerList_ = new HashMap();
    private HashMap<Integer, DsClientInfo> dsClientInfoMap_ = new HashMap();
    private DsManager dsManager_ = null;
    private BroadcastReceiver intentReceiver_ = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            synchronized (DsService.this.lockDolbyContext_) {
                try {
                    String action = intent.getAction();
                    String cmd = intent.getStringExtra("cmd");
                    DsLog.log1("DsService", "intentReceiver_.onReceive " + action + " / " + cmd + " / " + intent.getStringExtra("widget class"));
                    if ((intent.getAction().equals("android.intent.action.REBOOT") || intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) && DsService.this.dsManager_ != null) {
                        DsLog.log1("DsService", "Save DS state and current settings before shutting down...");
                        DsService.this.dsManager_.saveDsStateAndSettings();
                    } else if (action.equals("media_server_started") || action.equals("audio_server_started")) {
                        DsService.this.dsManager_.validateDsEffect();
                        DsLog.log1("DsService", "DS effect recreate successfully");
                    } else {
                        if (action.equals("DS_EFFECT_SUSPEND_ACTION")) {
                            DsLog.log1("DsService", "DS_EFFECT_SUSPEND_ACTION " + getResultCode());
                            switch (getResultCode()) {
                                case 0:
                                    DsService.this.dsManager_.setDsSuspended(false);
                                    DsLog.log1("DsService", "DS_EFFECT_UNSUSPENDED");
                                    DsService.this.cbkManager_.invokeCallback(6, 0, 0, 0, null, null);
                                    DsService.this.doSetDsOn(0, false);
                                    break;
                                case 1:
                                    DsService.this.dsManager_.setDsSuspended(true);
                                    DsLog.log1("DsService", "DS_EFFECT_SUSPENDED");
                                    DsService.this.cbkManager_.invokeCallback(6, 0, 1, 0, null, null);
                                    if (DsService.this.dsManager_.getDsOn()) {
                                        DsService.this.cbkManager_.invokeCallback(1, 0, 0, 0, null, null);
                                        DsService.this.cbkManager_.invokeDs1Callback(1, 0, 0, 0, null, null);
                                        break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } else if (intent.getAction().equals("DS_AUDIO_FOCUS_CHANGE_ACTION")) {
                            if (intent.hasExtra("packageName")) {
                                String packageName = intent.getStringExtra("packageName");
                                if (intent.hasExtra("focusChange")) {
                                    String focusChange = intent.getStringExtra("focusChange");
                                    if (focusChange.equals("loss") || focusChange.equals("abandon")) {
                                        AudioManager audioManager = (AudioManager) DsService.this.getSystemService("audio");
                                        if (!((Boolean) DsService.this.audioManagerIsAppInFocus_.invoke(audioManager, new Object[]{packageName})).booleanValue()) {
                                            if (focusChange.equals("abandon")) {
                                                DsLog.log1("DsService", "DsService,The application named " + packageName + " has abandoned its audio focus");
                                                DsService.this.arManager_.doAccessForAudioFocusChange(packageName, 2);
                                            } else {
                                                DsLog.log1("DsService", "DsService,The application named " + packageName + " has lost its audio focus");
                                                DsService.this.arManager_.doAccessForAudioFocusChange(packageName, 1);
                                            }
                                        }
                                    } else if (focusChange.equals("gain")) {
                                        DsLog.log1("DsService", "DsService,The application named " + packageName + " has gained its audio focus");
                                        DsService.this.arManager_.doAccessForAudioFocusChange(packageName, 0);
                                    }
                                }
                            }
                        } else if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                            if (intent.getIntExtra("state", 0) == 1) {
                                if (DsService.this.dsManager_.getDsOn()) {
                                    DsService.this.dsManager_.setDsOn(false);
                                    DsService.this.cbkManager_.invokeDs1Callback(1, 0, 0, 0, null, null);
                                    AudioSystem.setParameters("dolby_state=false");
                                } else {
                                    DsLog.log1("DsService", "DS have already turn off,DsOn does not need set false");
                                }
                            } else if (DsService.this.dsManager_.getDsOn()) {
                                DsLog.log1("DsService", "DS have already turn on,DsOn does not need set true");
                            } else {
                                DsService.this.dsManager_.setDsOn(true);
                                DsService.this.cbkManager_.invokeDs1Callback(1, 0, 1, 0, null, null);
                            }
                        } else if (action.equals("dolby.intent.action.MOVIE_PROFILE")) {
                            if (DsService.this.dsManager_.getDsOn()) {
                                DsService.this.dsManager_.setSelectedProfile(ProfileType.movie.ordinal());
                                DsLog.log1("DsService", "DS replace profile with MOVIE (" + ProfileType.movie.ordinal() + ")");
                            }
                        } else if (action.equals("dolby.intent.action.MUSIC_PROFILE")) {
                            if (DsService.this.dsManager_.getDsOn()) {
                                DsService.this.dsManager_.setSelectedProfile(ProfileType.music.ordinal());
                                DsLog.log1("DsService", "DS replace profile with MUSIC (" + ProfileType.movie.ordinal() + ")");
                            }
                        } else if (action.equals("dolby.intent.action.VOICE_PROFILE") && DsService.this.dsManager_.getDsOn()) {
                            DsService.this.dsManager_.setSelectedProfile(ProfileType.voice.ordinal());
                            DsLog.log1("DsService", "DS replace profile with VOICE (" + ProfileType.movie.ordinal() + ")");
                        }
                    }
                } catch (Exception ex) {
                    Log.e("DsService", "Exception found in DsService::onReceive()");
                    ex.printStackTrace();
                }
            }
        }
    };
    private final Object lockDolbyContext_ = new Object();
    private DsVisualizerManager visManager_ = null;

    private class DsClientDeathHandler implements DeathRecipient {
        int mHandle;
        private final IDsDeathHandler mIDsDeathHandler;

        DsClientDeathHandler(IDsDeathHandler dh, int handle) {
            this.mIDsDeathHandler = dh;
            this.mHandle = handle;
        }

        public void linkToDeath() throws RemoteException {
            this.mIDsDeathHandler.asBinder().linkToDeath(this, 0);
        }

        public void unlinkToDeath() {
            this.mIDsDeathHandler.asBinder().unlinkToDeath(this, 0);
        }

        public void binderDied() {
            synchronized (DsService.this.lockDolbyContext_) {
                DsService.this.visManager_.unregister(this.mHandle);
                DsService.this.arManager_.removeDsConnectedApp(this.mHandle);
                DsService.this.arManager_.unRegisterDsVersion(this.mHandle);
                DsService.this.dsClientInfoMap_.remove(Integer.valueOf(this.mHandle));
                DsService.this.dsClientDeathHandlerList_.remove(this.mIDsDeathHandler.asBinder());
            }
        }
    }

    public void onCreate() {
        DsLog.log1("DsService", "DsService.onCreate(), DAX Version = [DAX2.3.1.30_r1]");
        try {
            createDs(null);
            IntentFilter commandFilter = new IntentFilter();
            commandFilter.addAction("com.dolby.ds.srvcmd.init");
            commandFilter.addAction("android.intent.action.REBOOT");
            commandFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            commandFilter.addAction("media_server_started");
            commandFilter.addAction("audio_server_started");
            commandFilter.addAction("DS_EFFECT_SUSPEND_ACTION");
            commandFilter.addAction("DS_AUDIO_FOCUS_CHANGE_ACTION");
            commandFilter.addAction("android.intent.action.HEADSET_PLUG");
            commandFilter.addAction("dolby.intent.action.MOVIE_PROFILE");
            commandFilter.addAction("dolby.intent.action.MUSIC_PROFILE");
            commandFilter.addAction("dolby.intent.action.VOICE_PROFILE");
            registerReceiver(this.intentReceiver_, commandFilter);
            this.cbkManager_ = new DsCallbackManager();
            this.arManager_ = new DsAccessRightManager(this.dsManager_, this.cbkManager_);
            this.visManager_ = new DsVisualizerManager(this.dsManager_, this.cbkManager_);
            this.audioManagerIsAppInFocus_ = AudioManager.class.getMethod("isAppInFocus", new Class[]{String.class});
        } catch (Exception ex) {
            Log.e("DsService", "Exception found in DsService.onCreate()");
            ex.printStackTrace();
        }
    }

    public void onDestroy() {
        DsLog.log1("DsService", "DsService.onDestroy()");
        synchronized (this.lockDolbyContext_) {
            try {
                this.dsManager_.setDsOn(false);
            } catch (Exception ex) {
                Log.e("DsService", "Exception found in DsService.onDestory()");
                ex.printStackTrace();
            }
            this.cbkManager_.release();
            this.arManager_.release();
            this.visManager_.release();
            this.dsManager_ = null;
            unregisterReceiver(this.intentReceiver_);
        }
    }

    public int onStartCommand(Intent callerIntent, int flags, int startId) {
        DsLog.log1("DsService", "DsService.onStartCommand()");
        if (callerIntent != null) {
            try {
                String action = callerIntent.getAction();
                DsLog.log1("DsService", "Intent action is " + action);
                if ("com.dolby.ds.srvcmd.toggleonoff".equals(action)) {
                    synchronized (this.lockDolbyContext_) {
                        doToggleDsOn(0);
                    }
                } else if ("com.dolby.ds.srvcmd.select".equals(action)) {
                    synchronized (this.lockDolbyContext_) {
                        doSetSelectedProfile(0, callerIntent.getIntExtra("cmd", 0));
                    }
                } else if ("com.dolby.ds.srvcmd.launchapp".equals(action)) {
                    Intent intent = getDsConsumerAppIntent();
                    if (intent != null) {
                        intent.addFlags(268435456);
                        startActivity(intent);
                    }
                }
            } catch (Exception ex) {
                Log.e("DsService", "DsService.onStartCommand() exception found");
                ex.printStackTrace();
            }
        } else {
            DsLog.log1("DsService", "onStartCommand: callerIntent==null, ignoring...");
        }
        return 1;
    }

    public IBinder onBind(Intent intent) {
        DsLog.log1("DsService", "DsService.onBind()");
        if (IDs.class.getName().equals(intent.getAction())) {
            return this.binder_;
        }
        Log.e("DsService", "/DsService.onBind() - return null");
        return null;
    }

    public void onTrimMemory(int level) {
        DsLog.log1("DsService", "DsService.onTrimMemory() level " + level);
    }

    public void onLowMemory() {
        DsLog.log1("DsService", "DsService.onLowMemory()");
    }

    private void createDs(Intent callerIntent) {
        DsLog.log1("DsService", "createDs()");
        synchronized (this.lockDolbyContext_) {
            try {
                this.dsManager_ = new DsManager(this);
                String defPackage = "com.dolby.daxappUI";
                String defName = "com.dolby.daxappUI.MainActivity";
                //Editor ed = getSharedPreferences("musicfx", 0).edit();
                //ed.putString("defaultpanelpackage", defPackage);
                //ed.putString("defaultpanelname", defName);
                //ed.commit();
                DsLog.log1("DsService", "wrote " + defPackage + "/" + defName + " as default");
            } catch (Exception ex) {
                Log.e("DsService", "Ds() FAILED!", ex);
            }
        }
    }

    private Intent getDsConsumerAppIntent() {
        Intent intent = new Intent("com.dolby.LAUNCH_DS_APP");
        List<ResolveInfo> ris = getPackageManager().queryIntentActivities(intent, 512);
        return (ris == null || ris.isEmpty()) ? null : intent;
    }

    private int doToggleDsOn(int handle) throws DeadObjectException {
        int doSetDsOn;
        synchronized (this.lockDolbyContext_) {
            doSetDsOn = doSetDsOn(handle, !this.dsManager_.getDsOn());
        }
        return doSetDsOn;
    }

    private int doSetDsOn(int handle, boolean on) throws DeadObjectException {
        synchronized (this.lockDolbyContext_) {
            if (this.dsManager_.getDsSuspended()) {
                DsLog.log2("DsService", "DS_REQUEST_FAILED_EFFECT_SUSPENDED");
                return 1;
            }
            boolean z;
            this.dsManager_.setDsOn(on);
            int newStatus = this.dsManager_.getDsOn() ? 1 : 0;
            if (this.arManager_.isGlobalSettings(handle)) {
                this.cbkManager_.invokeCallback(1, handle, newStatus, 0, null, null);
                this.cbkManager_.invokeDs1Callback(1, handle, newStatus, 0, null, null);
                this.dsManager_.saveDsState();
            }
            DsVisualizerManager dsVisualizerManager = this.visManager_;
            if (newStatus == 1) {
                z = true;
            } else {
                z = false;
            }
            dsVisualizerManager.toggleVisualizer(z);
            return 0;
        }
    }

    private boolean doSetSelectedProfile(int handle, int profile) throws DeadObjectException {
        boolean z = false;
        synchronized (this.lockDolbyContext_) {
            boolean success = this.dsManager_.setSelectedProfile(profile);
            int newProfile = this.dsManager_.getSelectedProfile();
            if (success && profile == newProfile) {
                doUpdateSelectedProfile(handle, profile);
            }
            if (success && profile == newProfile) {
                z = true;
            }
        }
        return z;
    }

    private void doUpdateSelectedProfile(int handle, int profile) {
        if (this.arManager_.isGlobalSettings(handle)) {
            this.cbkManager_.invokeCallback(2, handle, profile, 0, null, null);
            this.cbkManager_.invokeDs1Callback(2, handle, profile, 0, null, null);
            this.dsManager_.saveDsProfileSettings();
        }
    }
}
