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

import com.dolby.dax.model.GeqPreset;
import com.dolby.dax.model.IeqPreset;
import com.dolby.dax.model.Model;
import com.dolby.dax.model.Parameter;
import com.dolby.dax.model.ParameterValues;
import com.dolby.dax.model.PresetType;
import com.dolby.dax.model.ProfileType;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PresetParser
{
    private static final Pattern GEQ_NAME_PATTERN;
    private static final Pattern IEQ_NAME_PATTERN;
    private static final PresetInfo geqInfo;
    private static final PresetInfo ieqInfo;
    private final List<GeqPreset> geqPresets;
    private final List<IeqPreset> ieqPresets;
    private final TagIterator ti;
    
    static {
        IEQ_NAME_PATTERN = Pattern.compile("ieq_(open|rich|focused)");
        GEQ_NAME_PATTERN = Pattern.compile("geq_(" + Joiner.on('|').join(ProfileType.values()) + ")_(" + Joiner.on('|').join(PresetType.values()) + ")");
        ieqInfo = new PresetInfo("ieq-bands", "band_ieq", "target", Parameter.ieq_frequency, Parameter.ieq_target);
        geqInfo = new PresetInfo("graphic-equalizer-bands", "band_geq", "gain", Parameter.geq_frequency, Parameter.geq_gain);
    }
    
    public PresetParser(final TagIterator ti, final List<IeqPreset> ieqPresets, final List<GeqPreset> geqPresets) {
        this.ti = ti;
        this.ieqPresets = ieqPresets;
        this.geqPresets = geqPresets;
    }
    
    private GeqPreset addGeqPreset(final String s) throws ValidationException {
        final PresetType geqPresetType = getGeqPresetType(this.ti, s);
        final ProfileType geqProfileType = getGeqProfileType(this.ti, s);
        for (final GeqPreset geqPreset : this.geqPresets) {
            if (geqPreset.getType() == geqPresetType && geqPreset.getProfile() == geqProfileType) {
                throw new ValidationException(this.ti, "Geq preset " + s + " is not unique");
            }
        }
        final GeqPreset geqPreset2 = new GeqPreset(geqPresetType, geqProfileType);
        this.geqPresets.add(geqPreset2);
        return geqPreset2;
    }
    
    private IeqPreset addIeqPreset(final String s) throws ValidationException {
        final PresetType ieqPresetType = getIeqPresetType(this.ti, s);
        final Iterator<IeqPreset> iterator = this.ieqPresets.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getType() == ieqPresetType) {
                throw new ValidationException(this.ti, "Ieq preset " + s + " is not unique");
            }
        }
        final IeqPreset ieqPreset = new IeqPreset(ieqPresetType);
        this.ieqPresets.add(ieqPreset);
        return ieqPreset;
    }
    
    public static PresetType getGeqPresetType(final TagIterator tagIterator, final String s) throws ValidationException {
        final Matcher matcher = PresetParser.GEQ_NAME_PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new ValidationException(tagIterator, "GEQ preset name is not valid: " + s);
        }
        return PresetType.valueOf(matcher.group(2));
    }
    
    public static ProfileType getGeqProfileType(final TagIterator tagIterator, final String s) throws ValidationException {
        final Matcher matcher = PresetParser.GEQ_NAME_PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new ValidationException(tagIterator, "GEQ preset name is not valid: " + s);
        }
        return ProfileType.valueOf(matcher.group(1));
    }
    
    public static PresetType getIeqPresetType(final TagIterator tagIterator, final String s) throws ValidationException {
        final Matcher matcher = PresetParser.IEQ_NAME_PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new ValidationException(tagIterator, "IEQ preset name is not valid: " + s);
        }
        return PresetType.valueOf(matcher.group(1));
    }
    
    public void parse() throws ValidationException {
        while (this.ti.atStartTag("preset")) {
            final String stringAttribute = this.ti.getStringAttribute("id");
            final String stringAttribute2 = this.ti.getStringAttribute("type");
            this.ti.next();
            PresetInfo presetInfo;
            Model model;
            if (stringAttribute2.equals("ieq")) {
                presetInfo = PresetParser.ieqInfo;
                model = this.addIeqPreset(stringAttribute);
            }
            else {
                if (!stringAttribute2.equals("geq")) {
                    throw new ValidationException(this.ti, "Invalid preset type " + stringAttribute2 + " for " + stringAttribute);
                }
                presetInfo = PresetParser.geqInfo;
                model = this.addGeqPreset(stringAttribute);
            }
            this.parsePresetParams(presetInfo, (ParameterValues)model);
            this.ti.consumeEndTag("preset");
        }
    }
    
    void parsePresetParams(final PresetInfo presetInfo, final ParameterValues parameterValues) throws ValidationException {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        final ArrayList<Integer> list2 = new ArrayList<Integer>();
        this.ti.consumeStartTag("data");
        this.ti.consumeStartTag(presetInfo.dataTag);
        while (this.ti.atStartTag(presetInfo.rowTag)) {
            list.add(this.ti.getIntAttribute("frequency"));
            list2.add(this.ti.getIntAttribute(presetInfo.valueTag));
            this.ti.next();
            this.ti.consumeEndTag(presetInfo.rowTag);
        }
        this.ti.consumeEndTag(presetInfo.dataTag);
        this.ti.consumeEndTag("data");
        parameterValues.set(presetInfo.frequency, ParserUtils.asIntArray(list));
        parameterValues.set(presetInfo.value, ParserUtils.asIntArray(list2));
    }
    
    public static class PresetInfo
    {
        final String dataTag;
        final Parameter frequency;
        final String rowTag;
        final Parameter value;
        final String valueTag;
        
        PresetInfo(final String dataTag, final String rowTag, final String valueTag, final Parameter frequency, final Parameter value) {
            this.dataTag = dataTag;
            this.rowTag = rowTag;
            this.valueTag = valueTag;
            this.frequency = frequency;
            this.value = value;
        }
    }
}
