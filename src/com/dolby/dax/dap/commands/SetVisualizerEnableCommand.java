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

public class SetVisualizerEnableCommand
{
    boolean enabled;
    
    public SetVisualizerEnableCommand() {
        this.enabled = false;
    }
    
    public int execute(final Dap dap) {
        int b;
        if (this.enabled) {
            b = 1;
        }
        else {
            b = 0;
        }
        return dap.send(1, new int[] { b });
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
