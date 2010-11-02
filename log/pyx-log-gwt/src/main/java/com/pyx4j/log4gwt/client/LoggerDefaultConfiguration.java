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
 * Created on Apr 24, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.google.gwt.core.client.GWT;

class LoggerDefaultConfiguration {

    private static interface Impl {

        void setUp();

    }

    static void setUp() {
        if (GWT.isClient()) {
            if (GWT.isScript()) {
                Impl impl = GWT.create(Impl.class);
                if (impl != null) {
                    impl.setUp();
                }
            } else {
                ClientLogger.addAppender(new AppenderStdOut());
                ClientLogger.addAppender(new AppenderHosted());
            }
        } else {
            ClientLogger.addAppender(new AppenderStdOut());
        }
    }

    @SuppressWarnings("unused")
    private static class ImplUnknown implements Impl {

        @Override
        public void setUp() {
        }

    }

    @SuppressWarnings("unused")
    private static class ImplMozilla implements Impl {

        @Override
        public void setUp() {
            if (AppenderFirebug.isSupported()) {
                ClientLogger.addAppender(new AppenderFirebug());
            }
        }

    }

    @SuppressWarnings("unused")
    private static class ImplIE implements Impl {

        @Override
        public void setUp() {
            if (AppenderConsoleIE8.isSupported()) {
                ClientLogger.addAppender(new AppenderConsoleIE8());
            }
        }

    }

    @SuppressWarnings("unused")
    private static class ImplSafari implements Impl {

        @Override
        public void setUp() {
            if (AppenderConsole.isSupported()) {
                ClientLogger.addAppender(new AppenderConsole());
            }
        }

    }

    @SuppressWarnings("unused")
    private static class ImplOpera implements Impl {

        @Override
        public void setUp() {
            // TODO
            if (AppenderConsole.isSupported()) {
                ClientLogger.addAppender(new AppenderConsole());
            }
        }

    }
}
