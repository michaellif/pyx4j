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
 * Created on 2010-08-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import com.google.gwt.core.client.GWT;

public class ApplicationMode {

    private static final Mode impl = GWT.create(Mode.class);

    private static interface Mode {
        boolean isDevelopment();
    }

    static class DevelopmentMode implements Mode {

        @Override
        public boolean isDevelopment() {
            return true;
        }
    }

    static class ProductionMode implements Mode {

        @Override
        public boolean isDevelopment() {
            return false;
        }
    }

    /**
     * Used as dev/prod switch. For production build include GWT module
     * com.pyx4j.gwt.ApplicationProductionMode
     */
    public static boolean isDevelopment() {
        return impl.isDevelopment();
    }
}
