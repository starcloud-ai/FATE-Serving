/*
 * Copyright 2019 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.ai.fate.register.common;

public class ServiceVersionUtil {

    static final String BIGER = "BIGER";
    static final String BIGER_OR_EQUAL = "BIGTHAN_OR_EQUAL";
    static final String SMALLER = "SMALLER";
    static final String EQUALS = "EQUALS";

    public static boolean march(String versionModel, String serverVersion, String clientVersion) {

        Integer serverVersionInteger = new Integer(serverVersion);
        Integer clientVersionInteger = new Integer(clientVersion);

        switch (versionModel) {

            case BIGER_OR_EQUAL:
                if (clientVersionInteger >= serverVersionInteger) {
                    return true;
                }

            case BIGER:
                if (clientVersionInteger > serverVersionInteger) {
                    return true;
                }
            case SMALLER:
                if (clientVersionInteger < serverVersionInteger) {
                    return true;
                }

            default:
                return false;
        }

    }

}
