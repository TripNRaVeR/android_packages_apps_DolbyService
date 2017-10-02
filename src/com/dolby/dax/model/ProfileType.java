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

package com.dolby.dax.model;

public enum ProfileType
{
    auto("auto", 7), 
    game("game", 2), 
    movie("movie", 0), 
    music("music", 1), 
    off("off", 6), 
    user1("user1", 4), 
    user2("user2", 5), 
    voice("voice", 3);
    
    private ProfileType(final String s, final int n) {
    }
}
