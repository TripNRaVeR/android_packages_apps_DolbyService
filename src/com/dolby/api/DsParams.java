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

public enum DsParams {
    DolbyHeadphoneVirtualizerControl(101),
    DolbyVirtualSpeakerVirtualizerControl(102),
    DolbyVolumeLevelerEnable(103),
    DolbyVolumeModelerEnable(104),
    NextGenSurroundEnable(105),
    IntelligentEqualizerEnable(106),
    DialogEnhancementEnable(107),
    GraphicEqualizerEnable(108),
    DolbyHeadphoneSurroundBoost(109),
    DolbyHeadphoneReverberationGain(110),
    DolbyVirtualSpeakerSurroundBoost(111),
    DolbyVirtualSpeakerAngle(112),
    DolbyVirtualSpeakerStartFrequency(113),
    DolbyVolumeLevelingAmount(114),
    IntelligentEqualizerBandTargets(115),
    IntelligentEqualizerAmount(116),
    DialogEnhancementAmount(117),
    DialogEnhancementDucking(118),
    GraphicEqualizerBandGains(119),
    AudioOptimizerEnable(120),
    PeakLimiterBoost(121),
    PeakLimitingProtectionMode(122),
    VolumeMaximizerEnable(123),
    VolumeMaximizerBoost(124),
    DolbyVolumeLevelerInputTarget(125),
    DolbyVolumeLevelerOutputTarget(126),
    DolbyVolumeModelerCalibration(127),
    IntelligentEqualizerBandCount(128),
    IntelligentEqualizerBandFrequencies(129),
    GraphicEqualizerBandCount(130),
    GraphicEqualizerBandFrequencies(131),
    AudioOptimizerBandCount(132),
    AudioOptimizerBandFrequencies(133),
    AudioOptimizerBandGains(134),
    AudioRegulatorBandCount(135),
    AudioRegulatorBandFrequencies(136),
    AudioOptimizerChannelCount(137),
    AudioRegulatorBandIsolates(138),
    AudioRegulatorBandLowThresholds(139),
    AudioRegulatorBandHighThresholds(140),
    AudioRegulatorOverdrive(141),
    AudioRegulatorTimbrePreservationAmount(142);
    
    private static String[] DAP1_PARAM_NAMES = null;
    private static DsParams[] params = null;
    private int id_;

    static {
        DAP1_PARAM_NAMES = new String[]{"null", "vdhe", "vspe", "dvle", "dvme", "ngon", "ieon", "deon", "geon", "dhsb", "dhrg", "dssb", "dssa", "dssf", "dvla", "iebt", "iea", "dea", "ded", "gebg", "aoon", "plb", "plmd", "vmon", "vmb", "dvli", "dvlo", "dvmc", "ienb", "iebf", "genb", "gebf", "aonb", "aobf", "aobg", "arnb", "arbf", "aocc", "arbi", "arbl", "arbh", "arod", "artp"};
        params = new DsParams[]{DolbyHeadphoneVirtualizerControl, DolbyVirtualSpeakerVirtualizerControl, DolbyVolumeLevelerEnable, DolbyVolumeModelerEnable, NextGenSurroundEnable, IntelligentEqualizerEnable, DialogEnhancementEnable, GraphicEqualizerEnable, DolbyHeadphoneSurroundBoost, DolbyHeadphoneReverberationGain, DolbyVirtualSpeakerSurroundBoost, DolbyVirtualSpeakerAngle, DolbyVirtualSpeakerStartFrequency, DolbyVolumeLevelingAmount, IntelligentEqualizerBandTargets, IntelligentEqualizerAmount, DialogEnhancementAmount, DialogEnhancementDucking, GraphicEqualizerBandGains, AudioOptimizerEnable, PeakLimiterBoost, PeakLimitingProtectionMode, VolumeMaximizerEnable, VolumeMaximizerBoost, DolbyVolumeLevelerInputTarget, DolbyVolumeLevelerOutputTarget, DolbyVolumeModelerCalibration, IntelligentEqualizerBandCount, IntelligentEqualizerBandFrequencies, GraphicEqualizerBandCount, GraphicEqualizerBandFrequencies, AudioOptimizerBandCount, AudioOptimizerBandFrequencies, AudioOptimizerBandGains, AudioRegulatorBandCount, AudioRegulatorBandFrequencies, AudioOptimizerChannelCount, AudioRegulatorBandIsolates, AudioRegulatorBandLowThresholds, AudioRegulatorBandHighThresholds, AudioRegulatorOverdrive, AudioRegulatorTimbrePreservationAmount};
    }

    private DsParams(int id) {
        this.id_ = id;
    }

    public int toInt() {
        return this.id_;
    }

    public String toString() {
        if (this.id_ <= 100 || this.id_ >= 143) {
            return "error";
        }
        return DAP1_PARAM_NAMES[this.id_ - 100];
    }

    public static DsParams FromInt(int i) {
        if (i <= 100 || i >= 143) {
            return null;
        }
        return params[(i - 100) - 1];
    }
}
