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

public interface Dap
{
    boolean hasControl();
    
    int receive(final int p0, final byte[] p1);
    
    int receive(final int p0, final int[] p1);
    
    void release();
    
    int send(final int p0, final byte[] p1);
    
    int send(final int p0, final int[] p1);
    
    int setEnabled(final boolean p0);
}
