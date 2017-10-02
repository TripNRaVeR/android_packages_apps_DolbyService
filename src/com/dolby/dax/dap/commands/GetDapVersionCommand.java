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

import com.dolby.dax.dap.Dap;
import java.nio.charset.StandardCharsets;

public class GetDapVersionCommand
{
    String dapVersion;
    
    public int execute(final Dap dap) {
        final byte[] array = new byte[16];
        final int receive = dap.receive(3, array);
        if (receive != 0) {
            this.dapVersion = null;
            return receive;
        }
        final String dapVersion = new String(array, StandardCharsets.UTF_8);
        final int index = dapVersion.indexOf(0);
        if (index < 0) {
            this.dapVersion = dapVersion;
            return receive;
        }
        this.dapVersion = dapVersion.substring(0, index);
        return receive;
    }
    
    public String getDapVersion() {
        return this.dapVersion;
    }
}
