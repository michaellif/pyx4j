/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 23, 2014
 * @author vlads
 */
package com.pyx4j.config.client;

import com.pyx4j.config.shared.ApplicationMode;

/**
 * To access Application mode use ApplicationMode.isDevelopment()
 */
public class ClientApplicationMode implements com.pyx4j.config.shared.ApplicationMode.Mode {

    private static boolean development = false;

    private static boolean qa = false;

    private static boolean demo = false;

    public static void setDevelopment(boolean development) {
        ClientApplicationMode.development = development;
    }

    public static void setQa(boolean qa) {
        ClientApplicationMode.qa = qa;
    }

    public static void setDemo(boolean demo) {
        ClientApplicationMode.demo = demo;
    }

    public static void setProduction() {
        setDevelopment(false);
        setDemo(false);
    }

    @Override
    public boolean isDevelopment() {
        return development;
    }

    @Override
    public boolean isQa() {
        return qa;
    }

    @Override
    public boolean isDemo() {
        return demo;
    }

    @Override
    public String toString() {
        return ApplicationMode.getModeInfo();
    }

}
