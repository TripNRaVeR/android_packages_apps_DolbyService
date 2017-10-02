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

package com.dolby.dax.xml;

import com.dolby.dax.xml.model.AuthorizedTechnologies;
import com.dolby.dax.xml.model.DeviceData;
import com.dolby.dax.xml.model.SkuType;
import com.dolby.dax.xml.model.Version;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceDataParser
{
    private final TagIterator ti;
    
    public DeviceDataParser(final TagIterator ti) {
        this.ti = ti;
    }
    
    private AuthorizedTechnologies parseAuthorizedTechnologies() throws ValidationException {
        if (!this.ti.atStartTag("authorized_technologies")) {
            return null;
        }
        final AuthorizedTechnologies authorizedTechnologies = new AuthorizedTechnologies();
        authorizedTechnologies.skuName = this.ti.getStringAttribute("sku_name");
        authorizedTechnologies.device = this.ti.getStringAttribute("device");
        authorizedTechnologies.bundle = this.ti.getStringAttribute("bundle");
        this.ti.next();
        authorizedTechnologies.audioOptimizer = this.parseSku("audio_optimizer");
        authorizedTechnologies.audioRegulator = this.parseSku("audio_regulator");
        authorizedTechnologies.bassEnhancer = this.parseSku("bass_enhancer");
        authorizedTechnologies.bassExtraction = this.parseSku("bass_extraction");
        authorizedTechnologies.calibrationBoost = this.parseSku("calibration_boost");
        authorizedTechnologies.dialogEnhancer = this.parseSku("dialog_enhancer");
        authorizedTechnologies.graphicEqualizer = this.parseSku("graphic_equalizer");
        authorizedTechnologies.heightFilter = this.parseSku("height_filter");
        authorizedTechnologies.intelligentEqualizer = this.parseSku("intelligent_equalizer");
        authorizedTechnologies.mediaIntelligence = this.parseSku("media_intelligence");
        authorizedTechnologies.processOptimizer = this.parseSku("process_optimizer");
        authorizedTechnologies.surroundDecoder = this.parseSku("surround_decoder");
        authorizedTechnologies.surroundVirtualizer = this.parseSku("surround_virtualizer");
        authorizedTechnologies.virtualizerHeadphone = this.parseSku("virtualizer_headphone");
        authorizedTechnologies.virtualizerSpeaker = this.parseSku("virtualizer_speaker");
        authorizedTechnologies.virtualBass = this.parseSku("virtual_bass");
        authorizedTechnologies.volumeLeveler = this.parseSku("volume_leveler");
        authorizedTechnologies.volumeMaximizer = this.parseSku("volume_maximizer");
        authorizedTechnologies.volumeModeler = this.parseSku("volume_modeler");
        this.ti.consumeEndTag("authorized_technologies");
        return authorizedTechnologies;
    }
    
    private SkuType parseSku(final String s) throws ValidationException {
        SkuType skuType = null;
        if (this.ti.atStartTag(s)) {
            skuType = new SkuType();
            skuType.authorized = this.ti.getBoolAttribute("authorized");
            skuType.changeableInTuningTool = this.ti.getBoolAttribute("changeable_in_tuning_tool");
            skuType.enabled = this.ti.getBoolAttribute("enabled");
            skuType.visibleInTuningTool = this.ti.getBoolAttribute("visible_in_tuning_tool");
            this.ti.next();
            this.ti.consumeEndTag(s);
        }
        return skuType;
    }
    
    private Version parseVersion(final String s) throws ValidationException {
        this.ti.requireStartTag(s);
        final Version version = new Version();
        version.major = this.ti.getIntAttribute("major");
        version.minor = this.ti.getIntAttribute("minor");
        version.maintenance = this.ti.getIntAttribute("maintenance");
        this.ti.next();
        this.ti.consumeEndTag(s);
        return version;
    }
    
    public DeviceData parse() throws XmlPullParserException, IOException, ValidationException {
        this.ti.start("device_data");
        final DeviceData deviceData = new DeviceData();
        deviceData.formatVersion = this.parseVersion("format_version");
        deviceData.toolVersion = this.parseVersion("tool_version");
        new PresetParser(this.ti, deviceData.ieqPresets, deviceData.geqPresets).parse();
        new ProfileParser(this.ti, deviceData.profiles, deviceData.profileEndpoints, deviceData.profilePorts).parse();
        new TuningParser(this.ti, deviceData.tunings, deviceData.deviceTunings).parse();
        deviceData.authorizedTechnologies = this.parseAuthorizedTechnologies();
        this.ti.finish("device_data");
        return deviceData;
    }
}
