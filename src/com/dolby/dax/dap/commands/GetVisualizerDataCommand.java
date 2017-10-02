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
import java.util.Arrays;

public class GetVisualizerDataCommand
{
    private int VISUALIZER_BAND_COUNT;
    private final float[] excitations;
    private final float[] gains;
    private final int[] visualizerData;
    
    public GetVisualizerDataCommand() {
        this.VISUALIZER_BAND_COUNT = 20;
        this.visualizerData = new int[this.VISUALIZER_BAND_COUNT * 2];
        this.gains = new float[this.VISUALIZER_BAND_COUNT];
        this.excitations = new float[this.VISUALIZER_BAND_COUNT];
    }
    
    public int execute(final Dap dap) {
        final int receive = dap.receive(2, this.visualizerData);
        if (receive != 0) {
            Arrays.fill(this.visualizerData, 0);
        }
        for (int i = 0; i < this.VISUALIZER_BAND_COUNT; ++i) {
            this.gains[i] = this.visualizerData[i] / 16.0f;
        }
        for (int j = this.VISUALIZER_BAND_COUNT; j < this.visualizerData.length; ++j) {
            this.excitations[j - this.VISUALIZER_BAND_COUNT] = this.visualizerData[j] / 16.0f;
        }
        return receive;
    }
    
    public float[] getExcitations() {
        return this.excitations;
    }
    
    public float[] getGains() {
        return this.gains;
    }
}
