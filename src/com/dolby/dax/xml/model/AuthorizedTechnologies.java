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

package com.dolby.dax.xml.model;

public class AuthorizedTechnologies
{
    public SkuType audioOptimizer;
    public SkuType audioRegulator;
    public SkuType bassEnhancer;
    public SkuType bassExtraction;
    public String bundle;
    public SkuType calibrationBoost;
    public String device;
    public SkuType dialogEnhancer;
    public SkuType graphicEqualizer;
    public SkuType heightFilter;
    public SkuType intelligentEqualizer;
    public SkuType mediaIntelligence;
    public SkuType processOptimizer;
    public String skuName;
    public SkuType surroundDecoder;
    public SkuType surroundVirtualizer;
    public SkuType virtualBass;
    public SkuType virtualizerHeadphone;
    public SkuType virtualizerSpeaker;
    public SkuType volumeLeveler;
    public SkuType volumeMaximizer;
    public SkuType volumeModeler;
    
    @Override
    public String toString() {
        return "AuthorizedTechnologies{\n\tbundle='" + this.bundle + '\'' + "\n\tdevice='" + this.device + '\'' + "\n\tskuName='" + this.skuName + '\'' + "\n\taudioOptimizer=" + this.audioOptimizer + "\n\taudioRegulator=" + this.audioRegulator + "\n\tbassEnhancer=" + this.bassEnhancer + "\n\tbassExtraction=" + this.bassExtraction + "\n\tcalibrationBoost=" + this.calibrationBoost + "\n\tdialogEnhancer=" + this.dialogEnhancer + "\n\tgraphicEqualizer=" + this.graphicEqualizer + "\n\theightFilter=" + this.heightFilter + "\n\tintelligentEqualizer=" + this.intelligentEqualizer + "\n\tmediaIntelligence=" + this.mediaIntelligence + "\n\tprocessOptimizer=" + this.processOptimizer + "\n\tsurroundDecoder=" + this.surroundDecoder + "\n\tsurroundVirtualizer=" + this.surroundVirtualizer + "\n\tvirtualizerHeadphone=" + this.virtualizerHeadphone + "\n\tvirtualizerSpeaker=" + this.virtualizerSpeaker + "\n\tvirtualBass=" + this.virtualBass + "\n\tvolumeLeveler=" + this.volumeLeveler + "\n\tvolumeMaximizer=" + this.volumeMaximizer + "\n\tvolumeModeler=" + this.volumeModeler + "\n}";
    }
}
