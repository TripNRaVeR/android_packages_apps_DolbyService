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

import com.dolby.dax.model.Endpoint;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.Port;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.Profile;
import com.dolby.dax.model.ProfileEndpoint;
import com.dolby.dax.model.ProfilePort;
import com.dolby.dax.model.ProfileType;
import java.util.Iterator;
import java.util.List;

public class ProfileParser
{
    private final List<ProfileEndpoint> profileEndpoints;
    private final List<ProfilePort> profilePorts;
    private final List<Profile> profiles;
    private final TagIterator ti;
    
    public ProfileParser(final TagIterator ti, final List<Profile> profiles, final List<ProfileEndpoint> profileEndpoints, final List<ProfilePort> profilePorts) {
        this.ti = ti;
        this.profiles = profiles;
        this.profileEndpoints = profileEndpoints;
        this.profilePorts = profilePorts;
    }
    
    private Profile parseProfileData(final ProfileType profileType) throws ValidationException {
        final String stringAttribute = this.ti.getStringAttribute("name");
        this.ti.next();
        final Profile profile = new Profile(profileType, stringAttribute, null, null);
        this.ti.consumeStartTag("data");
        profile.set(Parameter.graphic_equalizer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("graphic-equalizer-enable")));
        profile.set(Parameter.ieq_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("ieq-enable")));
        profile.set(Parameter.ieq_amount, ParserUtils.asIntArray(this.ti.consumeIntValueTag("ieq-amount")));
        profile.set(Parameter.mi_dialog_enhancer_steering_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("mi-dialog-enhancer-steering-enable")));
        profile.set(Parameter.mi_dv_leveler_steering_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("mi-dv-leveler-steering-enable")));
        profile.set(Parameter.mi_ieq_steering_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("mi-ieq-steering-enable")));
        profile.set(Parameter.mi_surround_compressor_steering_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("mi-surround-compressor-steering-enable")));
        profile.set(Parameter.virtualizer_headphone_reverb_gain, ParserUtils.asIntArray(this.ti.consumeIntValueTag("virtualizer-headphone-reverb-gain")));
        profile.set(Parameter.audio_optimizer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_audio-optimizer-enable")));
        profile.set(Parameter.bass_enhancer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_bass-enhancer-enable")));
        profile.set(Parameter.bass_extraction_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_bass-extraction-enable")));
        profile.set(Parameter.process_optimizer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_process-optimizer-enable")));
        profile.set(Parameter.regulator_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_regulator-enable")));
        profile.set(Parameter.regulator_speaker_dist_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_regulator-speaker-dist-enable")));
        profile.set(Parameter.surround_decoder_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_surround-decoder-enable")));
        profile.set(Parameter.volume_modeler_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_volume-modeler-enable")));
        profile.set(Parameter.virtual_bass_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_partial_virtual_bass_enable")));
        this.parseProfileEndpoints(profileType);
        this.parseProfileEndpointPorts(profileType);
        this.ti.consumeEndTag("data");
        this.parseProfilePresets(profile);
        return profile;
    }
    
    private void parseProfileEndpointPorts(final ProfileType profileType) throws ValidationException {
        while (this.ti.atStartTag("endpoint_port")) {
            final Port value = Port.valueOf(this.ti.getStringAttribute("id"));
            this.ti.next();
            for (final ProfilePort profilePort : this.profilePorts) {
                if (profilePort.getProfileType() == profileType && profilePort.getPort() == value) {
                    throw new ValidationException(this.ti, "Profile " + profileType + " contains duplicate endpoint port " + value);
                }
            }
            final ProfilePort profilePort2 = new ProfilePort(profileType, value);
            this.parseProfileEndpointPort(profilePort2);
            this.profilePorts.add(profilePort2);
            this.ti.consumeEndTag("endpoint_port");
        }
    }
    
    private void parseProfilePresets(final Profile profile) throws ValidationException {
        while (this.ti.atStartTag("include")) {
            final String stringAttribute = this.ti.getStringAttribute("preset");
            this.ti.next();
            if (stringAttribute.startsWith("ieq")) {
                final PresetType ieqPresetType = PresetParser.getIeqPresetType(this.ti, stringAttribute);
                if (profile.getSelectedIeqPreset() != null) {
                    throw new ValidationException(this.ti, "IEQ preset " + ieqPresetType + " can not by used for profile " + profile.getName() + " because " + profile.getSelectedIeqPreset() + " is already present");
                }
                profile.setSelectedIeqPreset(ieqPresetType);
            }
            else if (stringAttribute.startsWith("geq")) {
                final PresetType geqPresetType = PresetParser.getGeqPresetType(this.ti, stringAttribute);
                if (profile.getSelectedGeqPreset() != null) {
                    throw new ValidationException(this.ti, "GEQ preset " + geqPresetType + " can not by used for profile " + profile.getName() + " because " + profile.getSelectedGeqPreset() + " is already present");
                }
                profile.setSelectedGeqPreset(geqPresetType);
            }
            this.ti.consumeEndTag("include");
        }
    }
    
    public void parse() throws ValidationException {
        while (this.ti.atStartTag("profile")) {
            final ProfileType value = ProfileType.valueOf(this.ti.getStringAttribute("id"));
            final Iterator<Profile> iterator = this.profiles.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getType() == value) {
                    throw new ValidationException(this.ti, "Profile " + value + " is not unique");
                }
            }
            this.profiles.add(this.parseProfileData(value));
            this.ti.consumeEndTag("profile");
        }
    }
    
    void parseProfileEndpointPort(final ProfilePort profilePort) throws ValidationException {
        profilePort.set(Parameter.volume_leveler_in_target, ParserUtils.asIntArray(this.ti.consumeIntValueTag("volume-leveler-in-target")));
        profilePort.set(Parameter.volume_leveler_out_target, ParserUtils.asIntArray(this.ti.consumeIntValueTag("volume-leveler-out-target")));
    }
    
    void parseProfileEndpointType(final ParameterValues parameterValues) throws ValidationException {
        parameterValues.set(Parameter.calibration_boost, ParserUtils.asIntArray(this.ti.consumeIntValueTag("calibration-boost")));
        parameterValues.set(Parameter.dialog_enhancer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("dialog-enhancer-enable")));
        parameterValues.set(Parameter.dialog_enhancer_amount, ParserUtils.asIntArray(this.ti.consumeIntValueTag("dialog-enhancer-amount")));
        parameterValues.set(Parameter.dialog_enhancer_ducking, ParserUtils.asIntArray(this.ti.consumeIntValueTag("dialog-enhancer-ducking")));
        parameterValues.set(Parameter.surround_boost, ParserUtils.asIntArray(this.ti.consumeIntValueTag("surround-boost")));
        parameterValues.set(Parameter.volmax_boost, ParserUtils.asIntArray(this.ti.consumeIntValueTag("volmax-boost")));
        parameterValues.set(Parameter.volume_leveler_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("volume-leveler-enable")));
        parameterValues.set(Parameter.volume_leveler_amount, ParserUtils.asIntArray(this.ti.consumeIntValueTag("volume-leveler-amount")));
        parameterValues.set(Parameter.virtualizer_enable, ParserUtils.asIntArray(this.ti.consumeBoolValueTag("intermediate_profile_partial_virtualizer_enable")));
    }
    
    void parseProfileEndpoints(final ProfileType profileType) throws ValidationException {
        while (this.ti.atStartTag("endpoint_type")) {
            final Endpoint value = Endpoint.valueOf(this.ti.getStringAttribute("id"));
            this.ti.next();
            for (final ProfileEndpoint profileEndpoint : this.profileEndpoints) {
                if (profileEndpoint.getEndpoint() == value && profileEndpoint.getProfileType() == profileType) {
                    throw new ValidationException(this.ti, "Profile " + profileType + " contains duplicate endpoint type " + value);
                }
            }
            final ProfileEndpoint profileEndpoint2 = new ProfileEndpoint(profileType, value);
            this.parseProfileEndpointType(profileEndpoint2);
            this.profileEndpoints.add(profileEndpoint2);
            this.ti.consumeEndTag("endpoint_type");
        }
    }
}
