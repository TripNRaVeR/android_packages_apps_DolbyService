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
import android.media.audiofx.AudioEffect;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import tripndroid.dolby.audio.api.DsLog;

public class DapEffect implements Dap
{
    public static final UUID EFFECT_DS;
    public static final UUID EFFECT_TYPE_DS;
    public static final UUID EFFECT_TYPE_NULL;
    private Method AudioEffect_GetParameter_ByteArray;
    private Method AudioEffect_SetParameter_ByteArray;
    private AudioEffect audioEffect;
    
    static {
        EFFECT_TYPE_NULL = UUID.fromString("ec7178ec-e5e1-4432-a3f4-4657e6795210");
        EFFECT_TYPE_DS = UUID.fromString("46d279d9-9be7-453d-9d7c-ef937f675587");
        EFFECT_DS = UUID.fromString("9d4921da-8225-4f29-aefa-6e6f69726861");
    }

    public DapEffect(final int n) throws UnsupportedOperationException {
        super();
        this.audioEffect = null;
        this.AudioEffect_SetParameter_ByteArray = null;
        this.AudioEffect_GetParameter_ByteArray = null;
        try {
            this.audioEffect = AudioEffect.class.getConstructor(UUID.class, UUID.class, Integer.TYPE, Integer.TYPE).newInstance(DapEffect.EFFECT_TYPE_NULL, DapEffect.EFFECT_DS, 0, n);
            this.AudioEffect_SetParameter_ByteArray = AudioEffect.class.getMethod("setParameter", Integer.TYPE, byte[].class);
            this.AudioEffect_GetParameter_ByteArray = AudioEffect.class.getMethod("getParameter", Integer.TYPE, byte[].class);
            DsLog.log1("DapEffect", "Created DAP AudioEffect successfully");
        }
        catch (Exception ex) {
            Log.e("DapEffect", "Cannot instantiate DAP effect", (Throwable)ex);
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public boolean hasControl() {
        return this.audioEffect.hasControl();
    }
    
    @Override
    public int receive(int intValue, final byte[] array) {
        try {
            intValue = (int)this.AudioEffect_GetParameter_ByteArray.invoke(this.audioEffect, intValue, array);
            if (intValue == array.length) {
                return 0;
            }
            return -5;
        } catch (IllegalStateException ex) {
            Log.e("DapEffect", "AudioEffect instance is not controlling the effect", ex);
            return -5;
        } catch (ReflectiveOperationException e) {
            Log.wtf("DapEffect", "Can not invoke getParameter method on AudioEffect", e);
            return -5;
        }
    }
    
    @Override
    public int receive(int receive, final int[] array) {
        final ByteBuffer order = ByteBuffer.allocate(array.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        receive = this.receive(receive, order.array());
        if (receive == 0) {
            order.asIntBuffer().get(array);
        }
        return receive;
    }
    
    @Override
    public void release() {
        this.audioEffect.release();
    }
    
    @Override
    public int send(int intValue, final byte[] array) {
        try {
            intValue = (int)this.AudioEffect_SetParameter_ByteArray.invoke(this.audioEffect, intValue, array);
            return intValue;
        } catch (IllegalStateException ex) {
            Log.e("DapEffect", "AudioEffect instance is not controlling the effect", ex);
            return -5;
        } catch (ReflectiveOperationException e) {
            Log.wtf("DapEffect", "Can not invoke setParameter method on AudioEffect", e);
            return -5;
        }
    }
    
    @Override
    public int send(final int n, final int[] array) {
        final ByteBuffer allocate = ByteBuffer.allocate(array.length * 4);
        allocate.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(array);
        return this.send(n, allocate.array());
    }
    
    @Override
    public int setEnabled(final boolean enabled) {
        return this.audioEffect.setEnabled(enabled);
    }
}
