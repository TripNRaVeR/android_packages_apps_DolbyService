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

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IDsCallbacks extends IInterface
{
    void onAccessAvailable() throws RemoteException;
    
    void onAccessForceReleased(final String p0, final int p1) throws RemoteException;
    
    boolean onAccessRequested(final String p0, final int p1) throws RemoteException;
    
    void onDsOn(final boolean p0) throws RemoteException;
    
    void onDsSuspended(final boolean p0) throws RemoteException;
    
    boolean onLegacyClientSetting() throws RemoteException;
    
    void onProfileNameChanged(final int p0, final String p1) throws RemoteException;
    
    void onProfileSelected(final int p0) throws RemoteException;
    
    void onProfileSettingsChanged(final int p0) throws RemoteException;
    
    void onVisualizerSuspended(final boolean p0) throws RemoteException;
    
    void onVisualizerUpdated(final float[] p0, final float[] p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IDsCallbacks
    {
        public Stub() {
            this.attachInterface((IInterface)this, "com.dolby.api.IDsCallbacks");
        }
        
        public static IDsCallbacks asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.dolby.api.IDsCallbacks");
            if (queryLocalInterface != null && queryLocalInterface instanceof IDsCallbacks) {
                return (IDsCallbacks)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            final boolean b = false;
            final boolean b2 = false;
            final int n3 = 0;
            final int n4 = 0;
            boolean b3 = false;
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.dolby.api.IDsCallbacks");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    if (parcel.readInt() != 0) {
                        b3 = true;
                    }
                    this.onDsOn(b3);
                    parcel2.writeNoException();
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    boolean b4 = b;
                    if (parcel.readInt() != 0) {
                        b4 = true;
                    }
                    this.onDsSuspended(b4);
                    parcel2.writeNoException();
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    this.onProfileSelected(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    this.onProfileSettingsChanged(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    this.onVisualizerUpdated(parcel.createFloatArray(), parcel.createFloatArray());
                    parcel2.writeNoException();
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    boolean b5 = b2;
                    if (parcel.readInt() != 0) {
                        b5 = true;
                    }
                    this.onVisualizerSuspended(b5);
                    parcel2.writeNoException();
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    this.onAccessForceReleased(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    this.onAccessAvailable();
                    parcel2.writeNoException();
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    final boolean onAccessRequested = this.onAccessRequested(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    n = n3;
                    if (onAccessRequested) {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    this.onProfileNameChanged(parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("com.dolby.api.IDsCallbacks");
                    final boolean onLegacyClientSetting = this.onLegacyClientSetting();
                    parcel2.writeNoException();
                    n = n4;
                    if (onLegacyClientSetting) {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
            }
        }
        
        private static class Proxy implements IDsCallbacks
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void onAccessAvailable() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onAccessForceReleased(final String s, final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    obtain.writeString(s);
                    obtain.writeInt(n);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean onAccessRequested(final String s, int int1) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    obtain.writeString(s);
                    obtain.writeInt(int1);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    int1 = obtain2.readInt();
                    return int1 != 0;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onDsOn(final boolean b) throws RemoteException {
                int n = 1;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    if (!b) {
                        n = 0;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onDsSuspended(final boolean b) throws RemoteException {
                int n = 0;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    if (b) {
                        n = 1;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean onLegacyClientSetting() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onProfileNameChanged(final int n, final String s) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    obtain.writeInt(n);
                    obtain.writeString(s);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onProfileSelected(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    obtain.writeInt(n);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onProfileSettingsChanged(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    obtain.writeInt(n);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onVisualizerSuspended(final boolean b) throws RemoteException {
                int n = 0;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    if (b) {
                        n = 1;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onVisualizerUpdated(final float[] array, final float[] array2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.dolby.api.IDsCallbacks");
                    obtain.writeFloatArray(array);
                    obtain.writeFloatArray(array2);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
