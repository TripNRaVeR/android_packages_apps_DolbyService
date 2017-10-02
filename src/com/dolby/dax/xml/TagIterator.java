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

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class TagIterator
{
    XmlPullParser xpp;
    
    public TagIterator(final InputStream inputStream) throws ValidationException {
        try {
            this.init();
            this.xpp.setInput(inputStream, (String)null);
        }
        catch (XmlPullParserException ex) {
            throw new ValidationException(this, "Unable to initialize XML Parser", (Exception)ex);
        }
    }
    
    void assertAttributePresent(final String s, final String s2) throws ValidationException {
        if (s2 == null) {
            throw new ValidationException(this, "Attribute " + s + " is not present.");
        }
    }
    
    public boolean atStartTag(final String s) throws ValidationException {
        try {
            return this.xpp.getEventType() == 2 && this.xpp.getName().equals(s);
        }
        catch (XmlPullParserException ex) {
            throw new ValidationException(this, "Error reading event for start tag: " + s, (Exception)ex);
        }
    }
    
    public boolean consumeBoolValueTag(final String s) throws ValidationException {
        this.requireStartTag(s);
        final boolean boolAttribute = this.getBoolAttribute("value");
        this.next();
        this.consumeEndTag(s);
        return boolAttribute;
    }
    
    public void consumeEndTag(final String s) throws ValidationException {
        this.requireEndTag(s);
        this.next();
    }
    
    public int consumeIntValueTag(final String s) throws ValidationException {
        this.requireStartTag(s);
        final int intAttribute = this.getIntAttribute("value");
        this.next();
        this.consumeEndTag(s);
        return intAttribute;
    }
    
    public void consumeStartTag(final String s) throws ValidationException {
        this.requireStartTag(s);
        this.next();
    }
    
    public void finish(final String s) throws ValidationException {
        this.requireEndTag(s);
        try {
            this.xpp.next();
            this.xpp.require(1, (String)null, (String)null);
        }
        catch (IOException ex) {
            throw new ValidationException(this, "Unable to read end of document", ex);
        }
        catch (XmlPullParserException ex2) {
            throw new ValidationException(this, "Expected end of document", (Exception)ex2);
        }
    }
    
    public boolean getBoolAttribute(final String s) throws ValidationException {
        final String attributeValue = this.xpp.getAttributeValue((String)null, s);
        this.assertAttributePresent(s, attributeValue);
        return this.parseBoolValue(s, attributeValue);
    }
    
    public int getIntAttribute(final String s) throws ValidationException {
        final String attributeValue = this.xpp.getAttributeValue((String)null, s);
        this.assertAttributePresent(s, attributeValue);
        return this.parseIntValue(s, attributeValue);
    }
    
    public String getPositionDescription() {
        return this.xpp.getPositionDescription();
    }
    
    public String getStringAttribute(final String s) throws ValidationException {
        final String attributeValue = this.xpp.getAttributeValue((String)null, s);
        this.assertAttributePresent(s, attributeValue);
        return attributeValue;
    }
    
    void init() throws XmlPullParserException {
        this.xpp = XmlPullParserFactory.newInstance().newPullParser();
    }
    
    public void next() throws ValidationException {
        try {
            this.xpp.nextTag();
        }
        catch (IOException ex) {
            throw new ValidationException(this, "Unable to read next tag", ex);
        }
        catch (XmlPullParserException ex2) {
            throw new ValidationException(this, "Invalid tag found while parsing", (Exception)ex2);
        }
    }
    
    boolean parseBoolValue(final String s, final String s2) throws ValidationException {
        if (s2.equalsIgnoreCase("true")) {
            return true;
        }
        if (s2.equalsIgnoreCase("false")) {
            return false;
        }
        throw new ValidationException(this, "Value for " + s + " is not a boolean - " + s2);
    }
    
    int parseIntValue(final String s, final String s2) throws ValidationException {
        try {
            return Integer.parseInt(s2);
        }
        catch (NumberFormatException ex) {
            throw new ValidationException(this, "Value for " + s + " is not an integer - " + s2);
        }
    }
    
    public void requireEndTag(final String s) throws ValidationException {
        try {
            this.xpp.require(3, (String)null, s);
        }
        catch (IOException ex) {
            throw new ValidationException(this, "Unable to read end tag: " + s, ex);
        }
        catch (XmlPullParserException ex2) {
            throw new ValidationException(this, "Expected end tag: " + s, (Exception)ex2);
        }
    }
    
    public void requireStartTag(final String s) throws ValidationException {
        try {
            this.xpp.require(2, (String)null, s);
        }
        catch (IOException ex) {
            throw new ValidationException(this, "Unable to read start tag: " + s, ex);
        }
        catch (XmlPullParserException ex2) {
            throw new ValidationException(this, "Expected start tag: " + s, (Exception)ex2);
        }
    }
    
    public void start(final String s) throws ValidationException {
        try {
            this.xpp.require(0, (String)null, (String)null);
            this.next();
            this.consumeStartTag(s);
        }
        catch (IOException ex) {
            throw new ValidationException(this, "Unable to read start of document", ex);
        }
        catch (XmlPullParserException ex2) {
            throw new ValidationException(this, "Expected start of document", (Exception)ex2);
        }
    }
}
