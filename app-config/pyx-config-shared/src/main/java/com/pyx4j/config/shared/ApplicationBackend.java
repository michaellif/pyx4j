/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-09-27
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.shared;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.server.ServerSideFactory;

public class ApplicationBackend {

    public static final int GAE_ORDINARY_STRING_LENGTH_MAX = 500;

    public static enum ApplicationBackendType {
        RDB, GAE, AmazonEC2
    }

    public static interface ApplicationBackendConfig {

        ApplicationBackendType getBackendType();

        /**
         * Ordinary String Max length
         */
        int getDefaultDataStringLength();

    }

    public static final class ApplicationBackendConfigGae implements ApplicationBackendConfig {

        @Override
        public ApplicationBackendType getBackendType() {
            return ApplicationBackendType.GAE;
        }

        @Override
        public int getDefaultDataStringLength() {
            return GAE_ORDINARY_STRING_LENGTH_MAX;
        }

    }

    private final static ApplicationBackendConfig impl;

    static {
        if (ApplicationMode.hasGWT()) {
            if (GWT.isClient()) {
                impl = GWT.create(ApplicationBackendConfig.class);
            } else {
                impl = ServerSideFactory.create(ApplicationBackendConfig.class);
            }
        } else {
            impl = ServerSideFactory.create(ApplicationBackendConfig.class);
        }
    }

    public static final ApplicationBackendType getBackendType() {
        return impl.getBackendType();
    }

    public static final int getDefaultDataStringLength() {
        return impl.getDefaultDataStringLength();
    }
}
