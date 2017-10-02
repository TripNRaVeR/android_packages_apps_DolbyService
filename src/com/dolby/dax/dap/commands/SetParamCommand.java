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

package com.dolby.dax.dap.commands;

import android.util.Log;
import com.dolby.dax.dap.Dap;
import com.dolby.dax.dap.DapParameter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.google.common.io.LittleEndianDataOutputStream;

public class SetParamCommand {

    final LittleEndianDataOutputStream buffer;
    final ByteArrayOutputStream byteArrayOutputStream;
    int deviceId;
    int numParameters;
    
    public SetParamCommand(int i)
    {
        super();
        this.numParameters = 0;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.buffer = new LittleEndianDataOutputStream((OutputStream)this.byteArrayOutputStream);
        try
        {
            this.buffer.writeInt(i);
            this.buffer.writeInt(this.numParameters);
            return;
        }
        catch(java.io.IOException a)
        {
            Log.wtf("SetParamCommand", "Unexpected exception from ByteArrayOutputStream", (Throwable)a);
            return;
        }
    }
    
    public void add(DapParameter a, int i)
    {
        try
        {
            this.buffer.writeInt(this.deviceId);
            this.buffer.writeInt(a.getId());
            this.buffer.writeInt(1);
            this.buffer.writeInt(i);
            this.numParameters = this.numParameters + 1;
            return;
        }
        catch(java.io.IOException a0)
        {
            Log.wtf("SetParamCommand", "Unexpected exception from ByteArrayOutputStream", (Throwable)a0);
            return;
        }
    }
    
    public void add(DapParameter a, int i, int[][] a0)
    {
        try
        {
            this.buffer.writeInt(this.deviceId);
            this.buffer.writeInt(a.getId());
            int i0 = a0.length;
            int i1 = 0;
            int i2 = 1;
            while(i1 < i0)
            {
                i2 = i2 + a0[i1].length;
                i1 = i1 + 1;
            }
            this.buffer.writeInt(i2);
            this.buffer.writeInt(i);
            int i3 = a0.length;
            int i4 = 0;
            while(i4 < i3)
            {
                int[] a1 = a0[i4];
                int i5 = a1.length;
                int i6 = 0;
                while(i6 < i5)
                {
                    boolean b = a1[i6] != 0;
                    this.buffer.writeInt(b ? 1 : 0);
                    i6 = i6 + 1;
                }
                i4 = i4 + 1;
            }
            this.numParameters = this.numParameters + 1;
            return;
        }
        catch(java.io.IOException a2)
        {
            Log.wtf("SetParamCommand", "Unexpected exception from ByteArrayOutputStream", (Throwable)a2);
            return;
        }
    }
    
    public void add(DapParameter a, int[] a0)
    {
        try
        {
            this.buffer.writeInt(this.deviceId);
            this.buffer.writeInt(a.getId());
            this.buffer.writeInt(a0.length);
            int i = a0.length;
            int i0 = 0;
            while(i0 < i)
            {
                boolean b = a0[i0] != 0;
                this.buffer.writeInt(b ? 1 : 0);
                i0 = i0 + 1;
            }
            this.numParameters = this.numParameters + 1;
            return;
        }
        catch(java.io.IOException a1)
        {
            Log.wtf("SetParamCommand", "Unexpected exception from ByteArrayOutputStream", (Throwable)a1);
            return;
        }
    }
    
    public void add(DapParameter a, int[][] a0)
    {
        try
        {
            this.buffer.writeInt(this.deviceId);
            this.buffer.writeInt(a.getId());
            int i = a0.length;
            int i0 = 0;
            int i1 = 0;
            while(i0 < i)
            {
                i1 = i1 + a0[i0].length;
                i0 = i0 + 1;
            }
            this.buffer.writeInt(i1);
            int i2 = a0.length;
            int i3 = 0;
            while(i3 < i2)
            {
                int[] a1 = a0[i3];
                int i4 = a1.length;
                int i5 = 0;
                while(i5 < i4)
                {
                    boolean b = a1[i5] != 0;
                    this.buffer.writeInt(b ? 1 : 0);
                    i5 = i5 + 1;
                }
                i3 = i3 + 1;
            }
            this.numParameters = this.numParameters + 1;
            return;
        }
        catch(java.io.IOException a2)
        {
            Log.wtf("SetParamCommand", "Unexpected exception from ByteArrayOutputStream", (Throwable)a2);
            return;
        }
    }
    
    public int execute(Dap a)
    {
        if (this.numParameters == 0)
        {
            return 0;
        }
        ByteBuffer a0 = ByteBuffer.wrap(this.byteArrayOutputStream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
        a0.putInt(4, this.numParameters);
        return a.send(0, a0.array());
    }
    
    public void setDeviceId(int i)
    {
        this.deviceId = i;
    }
}
