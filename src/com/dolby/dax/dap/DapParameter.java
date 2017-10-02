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

package com.dolby.dax.dap;

import android.util.SparseArray;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DapParameter
{
    DAP1_AOBF("DAP1_AOBF", 36), 
    DAP1_AOBG("DAP1_AOBG", 37), 
    DAP1_AOCC("DAP1_AOCC", 57), 
    DAP1_AONB("DAP1_AONB", 35), 
    DAP1_ARBF("DAP1_ARBF", 39), 
    DAP1_ARBH("DAP1_ARBH", 60), 
    DAP1_ARBI("DAP1_ARBI", 58), 
    DAP1_ARBL("DAP1_ARBL", 59), 
    DAP1_ARNB("DAP1_ARNB", 38), 
    DAP1_BNDL("DAP1_BNDL", 26), 
    DAP1_BVER("DAP1_BVER", 25), 
    DAP1_DHSB("DAP1_DHSB", 53), 
    DAP1_DSSA("DAP1_DSSA", 63), 
    DAP1_DSSB("DAP1_DSSB", 54), 
    DAP1_DSSF("DAP1_DSSF", 62), 
    DAP1_ENDP("DAP1_ENDP", 61), 
    DAP1_GEBF("DAP1_GEBF", 34), 
    DAP1_GEBG("DAP1_GEBG", 56), 
    DAP1_GENB("DAP1_GENB", 33), 
    DAP1_IEBF("DAP1_IEBF", 32), 
    DAP1_IEBT("DAP1_IEBT", 55), 
    DAP1_IENB("DAP1_IENB", 31), 
    DAP1_LCMF("DAP1_LCMF", 50), 
    DAP1_LCPT("DAP1_LCPT", 52), 
    DAP1_LCVD("DAP1_LCVD", 51), 
    DAP1_OCF("DAP1_OCF", 27), 
    DAP1_PLMD("DAP1_PLMD", 40), 
    DAP1_SCPE("DAP1_SCPE", 30), 
    DAP1_TEST("DAP1_TEST", 41), 
    DAP1_VCBE("DAP1_VCBE", 48), 
    DAP1_VCBG("DAP1_VCBG", 47), 
    DAP1_VDHE("DAP1_VDHE", 28), 
    DAP1_VEN("DAP1_VEN", 42), 
    DAP1_VMON("DAP1_VMON", 49), 
    DAP1_VNBE("DAP1_VNBE", 46), 
    DAP1_VNBF("DAP1_VNBF", 44), 
    DAP1_VNBG("DAP1_VNBG", 45), 
    DAP1_VNNB("DAP1_VNNB", 43), 
    DAP1_VSPE("DAP1_VSPE", 29), 
    DAP2_AOBS("DAP2_AOBS", 83), 
    DAP2_ARBS("DAP2_ARBS", 68), 
    DAP2_ARDE("DAP2_ARDE", 66), 
    DAP2_ARON("DAP2_ARON", 65), 
    DAP2_ARRA("DAP2_ARRA", 67), 
    DAP2_ARTI("DAP2_ARTI", 69), 
    DAP2_BEB("DAP2_BEB", 74), 
    DAP2_BECF("DAP2_BECF", 75), 
    DAP2_BEON("DAP2_BEON", 73), 
    DAP2_BEW("DAP2_BEW", 76), 
    DAP2_DFSA("DAP2_DFSA", 92), 
    DAP2_DHFM("DAP2_DHFM", 91), 
    DAP2_DHSA("DAP2_DHSA", 94), 
    DAP2_DMC("DAP2_DMC", 90), 
    DAP2_DOM("DAP2_DOM", 70), 
    DAP2_DSA("DAP2_DSA", 93), 
    DAP2_DSB("DAP2_DSB", 71), 
    DAP2_GEBS("DAP2_GEBS", 72), 
    DAP2_IEBS("DAP2_IEBS", 64), 
    DAP2_MDEE("DAP2_MDEE", 87), 
    DAP2_MDLE("DAP2_MDLE", 86), 
    DAP2_MIEE("DAP2_MIEE", 85), 
    DAP2_MSCE("DAP2_MSCE", 84), 
    DAP2_VBHG("DAP2_VBHG", 81), 
    DAP2_VBM("DAP2_VBM", 77), 
    DAP2_VBMF("DAP2_VBMF", 82), 
    DAP2_VBOG("DAP2_VBOG", 79), 
    DAP2_VBSF("DAP2_VBSF", 78), 
    DAP2_VBSG("DAP2_VBSG", 80), 
    DAP2_VCBS("DAP2_VCBS", 88), 
    DAP2_VNBS("DAP2_VNBS", 89), 
    DAP_AOON("DAP_AOON", 21), 
    DAP_AROD("DAP_AROD", 17), 
    DAP_ARTP("DAP_ARTP", 18), 
    DAP_DEA("DAP_DEA", 6), 
    DAP_DED("DAP_DED", 7), 
    DAP_DEON("DAP_DEON", 5), 
    DAP_DHRG("DAP_DHRG", 19), 
    DAP_DVLA("DAP_DVLA", 12), 
    DAP_DVLE("DAP_DVLE", 11), 
    DAP_DVLI("DAP_DVLI", 13), 
    DAP_DVLO("DAP_DVLO", 14), 
    DAP_DVMC("DAP_DVMC", 10), 
    DAP_DVME("DAP_DVME", 9), 
    DAP_GEON("DAP_GEON", 20), 
    DAP_IEA("DAP_IEA", 16), 
    DAP_IEON("DAP_IEON", 15), 
    DAP_NGON("DAP_NGON", 4), 
    DAP_PLB("DAP_PLB", 3), 
    DAP_PREG("DAP_PREG", 2), 
    DAP_PSTG("DAP_PSTG", 1), 
    DAP_VCBF("DAP_VCBF", 23), 
    DAP_VCNB("DAP_VCNB", 22), 
    DAP_VER("DAP_VER", 24), 
    DAP_VMB("DAP_VMB", 8), 
    DAP_VOL("DAP_VOL", 0);
    
    static final SparseArray idMap;
    private final int dapVersion;
    private final int id;
    
    static {
        int i = 0;
        idMap = new SparseArray();
        for (DapParameter[] values = values(); i < values.length; ++i) {
            final DapParameter dapParameter = values[i];
            DapParameter.idMap.put(dapParameter.getId(), (Object)dapParameter.name());
        }
    }
    
    private DapParameter(String group, final int n) {
        final Matcher matcher = Pattern.compile("DAP(\\d*)_([A-Z]{3,4})").matcher(this.name());
        if (matcher.matches()) {
            group = matcher.group(1);
            final String group2 = matcher.group(2);
            if (group.length() == 0) {
                this.dapVersion = 0;
            }
            else {
                this.dapVersion = Integer.valueOf(group);
            }
            this.id = byteArrayToInt32(group2.toLowerCase().getBytes(StandardCharsets.US_ASCII));
            return;
        }
        throw new RuntimeException("Invalid DAP Parameter " + this.name());
    }
    
    static int byteArrayToInt32(final byte[] array) throws InvalidParameterException {
        int n = 0;
        if (array.length > 4) {
            throw new InvalidParameterException("Byte array length should be less than 4");
        }
        for (int i = 0; i < array.length; ++i) {
            n += array[i] << i * 8;
        }
        return n;
    }
    
    public int getId() {
        return this.id;
    }
}
