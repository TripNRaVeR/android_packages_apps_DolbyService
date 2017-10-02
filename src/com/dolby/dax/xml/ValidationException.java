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

public class ValidationException extends Exception
{
    private static final long serialVersionUID = 6439401954448677991L;
    private final boolean isXmlException;
    
    public ValidationException(final TagIterator tagIterator, final String s) {
        super(formatDescription(tagIterator, s));
        this.isXmlException = true;
    }
    
    public ValidationException(final TagIterator tagIterator, final String s, final Exception ex) {
        super(formatDescription(tagIterator, s), ex);
        this.isXmlException = true;
    }
    
    public ValidationException(final String s) {
        super(s);
        this.isXmlException = false;
    }
    
    private static String formatDescription(final TagIterator tagIterator, final String s) {
        return s + " : " + tagIterator.getPositionDescription();
    }
    
    public boolean isXmlException() {
        return this.isXmlException;
    }
}
