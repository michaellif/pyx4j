/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-07-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.client;

import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendConfig;

public abstract class CleintApplicationBackendConfig implements ApplicationBackendConfig {

    private static boolean productionBackend;

    @Override
    public int getDefaultDataStringLength() {
        return com.pyx4j.config.shared.ApplicationBackend.GAE_ORDINARY_STRING_LENGTH_MAX;
    }

    @Override
    public final boolean isProductionBackend() {
        return productionBackend;
    }

    public static void setProductionBackend(boolean productionBackend) {
        CleintApplicationBackendConfig.productionBackend = productionBackend;
    }

}
