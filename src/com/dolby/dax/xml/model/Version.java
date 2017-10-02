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

import java.util.Objects;

public class Version
{
    public int maintenance;
    public int major;
    public int minor;
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Version version = (Version)o;
        return this.major == version.major && this.minor == version.minor && this.maintenance == version.maintenance;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.maintenance);
    }
    
    @Override
    public String toString() {
        return "Version{" + this.major + "." + this.minor + "." + this.maintenance + '}';
    }
}
