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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import java.util.List;

public class DsCommon
{
    public static final String[][] GEQ_NAMES_XML;
    public static final String[] IEQ_PRESET_NAMES;
    public static final String[] IEQ_PRESET_NAMES_XML;
    public static final String[] PROFILE_NAMES;
    public static final String[] PROFILE_NAMES_XML;
    
    static {
        PROFILE_NAMES = new String[] { "Movie", "Music", "Game", "Voice", "Custom 1", "Custom 2" };
        IEQ_PRESET_NAMES = new String[] { "Off", "Open", "Rich", "Focused", "Bright", "Balanced", "Warm" };
        PROFILE_NAMES_XML = new String[] { "movie", "music", "game", "voice", "user1", "user2" };
        IEQ_PRESET_NAMES_XML = new String[] { "ieq_off", "ieq_open", "ieq_rich", "ieq_focused", "ieq_bright", "ieq_balanced", "ieq_warm" };
        GEQ_NAMES_XML = new String[][] { { "geq_movie_off", "geq_movie_open", "geq_movie_rich", "geq_movie_focused", "geq_movie_bright", "geq_movie_balanced", "geq_movie_warm" }, { "geq_music_off", "geq_music_open", "geq_music_rich", "geq_music_focused", "geq_music_bright", "geq_music_balanced", "geq_music_warm" }, { "geq_game_off", "geq_game_open", "geq_game_rich", "geq_game_focused", "geq_game_bright", "geq_game_balanced", "geq_game_warm" }, { "geq_voice_off", "geq_voice_open", "geq_voice_rich", "geq_voice_focused", "geq_voice_bright", "geq_voice_balanced", "geq_voice_warm" }, { "geq_user1_off", "geq_user1_open", "geq_user1_rich", "geq_user1_focused", "geq_user1_bright", "geq_user1_balanced", "geq_user1_warm" }, { "geq_user2_off", "geq_user2_open", "geq_user2_rich", "geq_user2_focused", "geq_user2_bright", "geq_user2_balanced", "geq_user2_warm" } };
    }
    
    public static Intent getServiceIntent(final Context context) {
        final Intent intent = new Intent(IDs.class.getName());
        final List<ResolveInfo> queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        if (queryIntentServices == null) {
            Log.e("DsCommon", "getServiceIntent() resolveInfos=null");
            return null;
        }
        if (queryIntentServices.size() != 1) {
            Log.e("DsCommon", "getServiceIntent() resolveInfos.size() = " + queryIntentServices.size());
            return null;
        }
        final ResolveInfo resolveInfo = queryIntentServices.get(0);
        intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
        return intent;
    }
}
