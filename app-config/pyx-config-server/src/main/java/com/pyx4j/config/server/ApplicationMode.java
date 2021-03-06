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
 * Created on 2010-09-22
 * @author vlads
 */
package com.pyx4j.config.server;

class ApplicationMode {

    public static final class ModeImpl implements com.pyx4j.config.shared.ApplicationMode.Mode {

        @Override
        public boolean isDevelopment() {
            return ServerSideConfiguration.instance().isDevelopmentBehavior();
        }

        @Override
        public boolean isQa() {
            return ServerSideConfiguration.instance().isQaBehavior();
        }

        @Override
        public boolean isDemo() {
            return ServerSideConfiguration.instance().isDemoBehavior();
        }

        @Override
        public String toString() {
            return com.pyx4j.config.shared.ApplicationMode.getModeInfo();
        }

    }
}
