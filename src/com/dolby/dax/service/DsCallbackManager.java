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

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import com.dolby.dax.service.DsManager;
import com.dolby.api.IDsCallbacks;
import tripndroid.dolby.audio.api.DsCommon;
import tripndroid.dolby.audio.api.DsLog;

public class DsCallbackManager
{
    private static final String TAG = "DsCallbackManager";


    private static final Object lock_ = new Object();

    private RemoteCallbackList<IDsCallbacks> ds1Callbacks_ = null;
    private RemoteCallbackList<IDsCallbacks> ds2Callbacks_ = null;
  
    public DsCallbackManager() {
        synchronized (lock_) {
            ds1Callbacks_ = new RemoteCallbackList<IDsCallbacks>();
            ds2Callbacks_ = new RemoteCallbackList<IDsCallbacks>();
        }
    }

    public void release() {
        synchronized (lock_) {
            if (ds1Callbacks_ != null) {
                ds1Callbacks_.kill();
                ds1Callbacks_ = null;
            }
            if (ds2Callbacks_ != null) {
                ds2Callbacks_.kill();
                ds2Callbacks_ = null;
            }
        }
    }

    public void register(IDsCallbacks cb, int handle, int ver) {
        synchronized (lock_) {
            if (ver == DsCommon.DS_CLIENT_VER_ONE) {
                ds1Callbacks_.register(cb, handle);
            }
            else {
                ds2Callbacks_.register(cb, handle);
            }
            DsLog.log1(TAG, "the register handle is "+ handle + " the version is " + ver);
        }
    }

    /**
     * The method to unregister the visualizer data.
     *
     */
    public void unregister(IDsCallbacks cb, int ver)
    {
        synchronized (lock_)
        {
            if (ver == DsCommon.DS_CLIENT_VER_ONE)
            {
                ds1Callbacks_.unregister(cb);
            }
            else
            {
                ds2Callbacks_.unregister(cb);
            }
            DsLog.log1(TAG, "unregisterCallback, version is " + ver);
        }
    }

    /**
     * Call the callback implemented in the DS2 client.
     *
     */
    public boolean invokeCallback(int what, int handle, int arg1, int arg2, Object obj1, Object obj2)
    {
    	boolean value = true;
	
        synchronized (lock_)
        {
            final int N = ds2Callbacks_.beginBroadcast();
            int vis_handle;
			int access_handle;
            for (int i = 0; i < N; i++)
            {
                try
                {
                    // Parse different callback and parameters in this method
                    switch (what)
                    {
                        case DsCommon.DS_STATUS_CHANGED_MSG:
                            //DS_STATUS_CHANGED_MSG, arg1 is used
                            boolean isOn = (arg1 == 1) ? true : false;
                            ds2Callbacks_.getBroadcastItem(i).onDsOn(isOn);
                            break;
                        case DsCommon.PROFILE_SELECTED_MSG:
                            //PROFILE_SELECTED_MSG, arg1 is used
                            ds2Callbacks_.getBroadcastItem(i).onProfileSelected(arg1);
                            break;
                        case DsCommon.PROFILE_SETTINGS_CHANGED_MSG:
                            //PROFILE_SETTINGS_CHANGED_MSG, arg1 is used
                            ds2Callbacks_.getBroadcastItem(i).onProfileSettingsChanged(arg1);
                            break;
                        case DsCommon.VISUALIZER_UPDATED_MSG:
                            //VISUALIZER_UPDATED_MSG, handle, obj1 and obj2 are used
                            vis_handle = ((Integer)ds2Callbacks_.getBroadcastCookie(i)).intValue();
                            if (vis_handle == handle)
                            {
                                ds2Callbacks_.getBroadcastItem(i).onVisualizerUpdated((float[])obj1, (float[])obj2);
                            }
                            break;
                        case DsCommon.VISUALIZER_SUSPENDED_MSG:
                            //VISUALIZER_SUSPENDED_MSG, handle and arg1 are used
                            vis_handle = ((Integer)ds2Callbacks_.getBroadcastCookie(i)).intValue();
                            if (vis_handle == handle)
                            {
                                boolean isSuspended = (arg1 == 1) ? true : false;
                                ds2Callbacks_.getBroadcastItem(i).onVisualizerSuspended(isSuspended);
                            }
                            break;
                        case DsCommon.DS_STATUS_SUSPENDED_MSG:
                            //DS_STATUS_SUSPENDED_MSG, arg1 is used
                            boolean isSuspended = (arg1 == 1) ? true : false;
                            ds2Callbacks_.getBroadcastItem(i).onDsSuspended(isSuspended);
                            break;
                        case DsCommon.ACCESS_REQUESTED_MSG:
                            access_handle = ((Integer)ds2Callbacks_.getBroadcastCookie(i)).intValue();
                            if(access_handle == handle)
                            {
                                value = ds2Callbacks_.getBroadcastItem(i).onAccessRequested((String)obj1,arg1);
                            }
                            break;
                        case DsCommon.ACCESS_RELEASED_MSG:
                            access_handle = ((Integer)ds2Callbacks_.getBroadcastCookie(i)).intValue();
                            if(access_handle == handle)
                            {
                                ds2Callbacks_.getBroadcastItem(i).onAccessForceReleased((String)obj1, arg1);
                            }
                            break;
                        case DsCommon.ACCESS_AVAILABLE_MSG:
                            access_handle = ((Integer)ds2Callbacks_.getBroadcastCookie(i)).intValue();
                            if(access_handle == handle)
                            {
                                ds2Callbacks_.getBroadcastItem(i).onAccessAvailable();
                            }
                            break;
                        case DsCommon.PROFILE_NAME_CHANGED_MSG:
                            ds2Callbacks_.getBroadcastItem(i).onProfileNameChanged(arg1,(String)obj1);
                            break;
                        default: 
                            break;
                    }
                }
                catch (RemoteException e)
                {
                    // The RemoteCallbackList will take care of removing
                    // the dead object for us.
                }
            }
            ds2Callbacks_.finishBroadcast();
        }
        return value;
    }

    /**
     * Call the callback implemented in the DS1 client.
     *
     */
    public boolean invokeDs1Callback(int what, int handle, int arg1, int arg2, Object obj1, Object obj2)
    {
    	boolean value = true;
        int clientHandle = 0;

        synchronized (lock_)
        {
            final int N = ds1Callbacks_.beginBroadcast();
            for (int i = 0; i < N; i++)
            {
                try
                {
                    // Parse different callback and parameters in this method
                    switch (what)
                    {
                        case DsCommon.DS_STATUS_CHANGED_MSG:
                            //DS_STATUS_CHANGED_MSG, arg1 is used
                            clientHandle = ((Integer)ds1Callbacks_.getBroadcastCookie(i)).intValue();
                            // Don't notify the handle who makes the changes, behavior as DS1.
                            if (handle != clientHandle)
                            {
                                boolean isOn = (arg1 == 1) ? true : false;
                                ds1Callbacks_.getBroadcastItem(i).onDsOn(isOn);
                            }
                            break;
                        case DsCommon.PROFILE_SELECTED_MSG:
                            //PROFILE_SELECTED_MSG, arg1 is used
                            clientHandle = ((Integer)ds1Callbacks_.getBroadcastCookie(i)).intValue();
                            // Don't notify the handle who makes the changes, behavior as DS1.
                            if (handle != clientHandle)
                            {
                                ds1Callbacks_.getBroadcastItem(i).onProfileSelected(arg1);
                            }
                            break;
                        case DsCommon.PROFILE_SETTINGS_CHANGED_MSG:
                        case DsCommon.VISUALIZER_UPDATED_MSG:
                        case DsCommon.VISUALIZER_SUSPENDED_MSG:
                        case DsCommon.DS_STATUS_SUSPENDED_MSG:
                        case DsCommon.ACCESS_REQUESTED_MSG:
                        case DsCommon.ACCESS_RELEASED_MSG:
                        case DsCommon.ACCESS_AVAILABLE_MSG:
                        case DsCommon.PROFILE_NAME_CHANGED_MSG:
                        default: 
                            break;
                    }
                }
                catch (RemoteException e)
                {
                    // The RemoteCallbackList will take care of removing
                    // the dead object for us.
                }
            }
            ds1Callbacks_.finishBroadcast();
        }
        return value;
    }
}
