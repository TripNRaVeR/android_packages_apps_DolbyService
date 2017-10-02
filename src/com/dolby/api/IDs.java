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

package com.dolby.api;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;
import tripndroid.dolby.audio.api.DsClientInfo;
import tripndroid.dolby.audio.api.DsProfileName;

public interface IDs extends IInterface
{
    int iAbandonAccessRight(final int p0, final int p1) throws RemoteException;
    
    int iCheckAccessRight(final int p0, final int p1, final int[] p2) throws RemoteException;
    
    int iGetDapLibraryVersion(final String[] p0) throws RemoteException;
    
    int iGetDsServiceVersion(final String[] p0) throws RemoteException;
    
    int iGetIeqPreset(final int p0, final int[] p1) throws RemoteException;
    
    int iGetIeqPresetCount(final int p0, final int[] p1) throws RemoteException;
    
    int iGetMonoSpeaker(final boolean[] p0) throws RemoteException;
    
    int iGetOffType(final int[] p0) throws RemoteException;
    
    int iGetParamLength(final int p0, final int[] p1) throws RemoteException;
    
    int iGetParameter(final int p0, final int p1, final int p2, final int[] p3) throws RemoteException;
    
    int iGetProfile(final int p0, final int[] p1) throws RemoteException;
    
    int iGetProfileCount(final int p0, final int[] p1) throws RemoteException;
    
    int[] iGetProfileEndpointParameter(final String p0, final String p1, final String p2) throws RemoteException;
    
    int iGetProfileModified(final int p0, final int p1, final boolean[] p2) throws RemoteException;
    
    int iGetProfileName(final int p0, final int p1, final DsProfileName[] p2) throws RemoteException;
    
    int[] iGetProfileParameter(final String p0, final String p1) throws RemoteException;
    
    int[] iGetProfilePortParameter(final String p0, final String p1, final String p2) throws RemoteException;
    
    int iGetState(final int p0, final int[] p1) throws RemoteException;
    
    int[] iGetTuningParameter(final String p0, final String p1) throws RemoteException;
    
    int iGetUdcLibraryVersion(final String[] p0) throws RemoteException;
    
    void iRegisterCallback(final int p0, final IDsCallbacks p1, final int p2) throws RemoteException;
    
    void iRegisterDeathHandler(final int p0, final IDsDeathHandler p1) throws RemoteException;
    
    void iRegisterDsAccess(final int p0, final DsClientInfo p1) throws RemoteException;
    
    void iRegisterVisualizerData(final int p0) throws RemoteException;
    
    int iRequestAccessRight(final int p0, final int p1) throws RemoteException;
    
    int iResetProfile(final int p0, final int p1, final int p2) throws RemoteException;
    
    int iSetIeqPreset(final int p0, final int p1, final int p2) throws RemoteException;
    
    int iSetParameter(final int p0, final int p1, final int p2, final int p3, final int[] p4) throws RemoteException;
    
    int iSetProfile(final int p0, final int p1, final int p2) throws RemoteException;
    
    int iSetProfileName(final int p0, final int p1, final DsProfileName p2) throws RemoteException;
    
    int iSetState(final int p0, final int p1, final boolean p2) throws RemoteException;
    
    void iUnregisterCallback(final int p0, final IDsCallbacks p1, final int p2) throws RemoteException;
    
    void iUnregisterDeathHandler(final int p0, final IDsDeathHandler p1) throws RemoteException;
    
    void iUnregisterDsAccess(final int p0) throws RemoteException;
    
    void iUnregisterVisualizerData(final int p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IDs
    {
        public Stub() {
            this.attachInterface((IInterface)this, "com.dolby.api.IDs");
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int n, final Parcel parcel, final Parcel parcel2, int n2) throws RemoteException {
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.dolby.api.IDs");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iRegisterVisualizerData(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iUnregisterVisualizerData(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iRegisterDeathHandler(parcel.readInt(), IDsDeathHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iUnregisterDeathHandler(parcel.readInt(), IDsDeathHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    DsClientInfo dsClientInfo;
                    if (parcel.readInt() != 0) {
                        dsClientInfo = (DsClientInfo)DsClientInfo.CREATOR.createFromParcel(parcel);
                    }
                    else {
                        dsClientInfo = null;
                    }
                    this.iRegisterDsAccess(n, dsClientInfo);
                    parcel2.writeNoException();
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iUnregisterDsAccess(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iRegisterCallback(parcel.readInt(), IDsCallbacks.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    this.iUnregisterCallback(parcel.readInt(), IDsCallbacks.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    n = this.iSetState(n, n2, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    int[] array;
                    if (n2 < 0) {
                        array = null;
                    }
                    else {
                        array = new int[n2];
                    }
                    n = this.iGetState(n, array);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array);
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    int[] array2;
                    if (n < 0) {
                        array2 = null;
                    }
                    else {
                        array2 = new int[n];
                    }
                    n = this.iGetOffType(array2);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array2);
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    String[] array3;
                    if (n < 0) {
                        array3 = null;
                    }
                    else {
                        array3 = new String[n];
                    }
                    n = this.iGetDsServiceVersion(array3);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeStringArray(array3);
                    return true;
                }
                case 13: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    String[] array4;
                    if (n < 0) {
                        array4 = null;
                    }
                    else {
                        array4 = new String[n];
                    }
                    n = this.iGetDapLibraryVersion(array4);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeStringArray(array4);
                    return true;
                }
                case 14: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    String[] array5;
                    if (n < 0) {
                        array5 = null;
                    }
                    else {
                        array5 = new String[n];
                    }
                    n = this.iGetUdcLibraryVersion(array5);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeStringArray(array5);
                    return true;
                }
                case 15: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = this.iSetParameter(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.createIntArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 16: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    final int int1 = parcel.readInt();
                    final int int2 = parcel.readInt();
                    int[] array6;
                    if (int2 < 0) {
                        array6 = null;
                    }
                    else {
                        array6 = new int[int2];
                    }
                    n = this.iGetParameter(n, n2, int1, array6);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array6);
                    return true;
                }
                case 17: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = this.iSetIeqPreset(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 18: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    int[] array7;
                    if (n2 < 0) {
                        array7 = null;
                    }
                    else {
                        array7 = new int[n2];
                    }
                    n = this.iGetIeqPreset(n, array7);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array7);
                    return true;
                }
                case 19: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    int[] array8;
                    if (n2 < 0) {
                        array8 = null;
                    }
                    else {
                        array8 = new int[n2];
                    }
                    n = this.iGetIeqPresetCount(n, array8);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array8);
                    return true;
                }
                case 20: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = this.iSetProfile(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 21: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    int[] array9;
                    if (n2 < 0) {
                        array9 = null;
                    }
                    else {
                        array9 = new int[n2];
                    }
                    n = this.iGetProfile(n, array9);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array9);
                    return true;
                }
                case 22: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = this.iResetProfile(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 23: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    final int int3 = parcel.readInt();
                    boolean[] array10;
                    if (int3 < 0) {
                        array10 = null;
                    }
                    else {
                        array10 = new boolean[int3];
                    }
                    n = this.iGetProfileModified(n, n2, array10);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeBooleanArray(array10);
                    return true;
                }
                case 24: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    int[] array11;
                    if (n2 < 0) {
                        array11 = null;
                    }
                    else {
                        array11 = new int[n2];
                    }
                    n = this.iGetProfileCount(n, array11);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array11);
                    return true;
                }
                case 25: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = this.iRequestAccessRight(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 26: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = this.iAbandonAccessRight(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 27: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    final int int4 = parcel.readInt();
                    int[] array12;
                    if (int4 < 0) {
                        array12 = null;
                    }
                    else {
                        array12 = new int[int4];
                    }
                    n = this.iCheckAccessRight(n, n2, array12);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array12);
                    return true;
                }
                case 28: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    int[] array13;
                    if (n2 < 0) {
                        array13 = null;
                    }
                    else {
                        array13 = new int[n2];
                    }
                    n = this.iGetParamLength(n, array13);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeIntArray(array13);
                    return true;
                }
                case 29: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    boolean[] array14;
                    if (n < 0) {
                        array14 = null;
                    }
                    else {
                        array14 = new boolean[n];
                    }
                    n = this.iGetMonoSpeaker(array14);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeBooleanArray(array14);
                    return true;
                }
                case 30: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    DsProfileName dsProfileName;
                    if (parcel.readInt() != 0) {
                        dsProfileName = (DsProfileName)DsProfileName.CREATOR.createFromParcel(parcel);
                    }
                    else {
                        dsProfileName = null;
                    }
                    n = this.iSetProfileName(n, n2, dsProfileName);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 31: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    n = parcel.readInt();
                    n2 = parcel.readInt();
                    final int int5 = parcel.readInt();
                    Object[] array15;
                    if (int5 < 0) {
                        array15 = null;
                    }
                    else {
                        array15 = new DsProfileName[int5];
                    }
                    n = this.iGetProfileName(n, n2, (DsProfileName[])array15);
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    parcel2.writeTypedArray((Parcelable[])array15, 1);
                    return true;
                }
                case 32: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    final int[] iGetProfileParameter = this.iGetProfileParameter(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(iGetProfileParameter);
                    return true;
                }
                case 33: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    final int[] iGetProfileEndpointParameter = this.iGetProfileEndpointParameter(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(iGetProfileEndpointParameter);
                    return true;
                }
                case 34: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    final int[] iGetProfilePortParameter = this.iGetProfilePortParameter(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(iGetProfilePortParameter);
                    return true;
                }
                case 35: {
                    parcel.enforceInterface("com.dolby.api.IDs");
                    final int[] iGetTuningParameter = this.iGetTuningParameter(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(iGetTuningParameter);
                    return true;
                }
            }
        }
    }
}
