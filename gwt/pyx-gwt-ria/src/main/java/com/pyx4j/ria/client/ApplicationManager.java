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
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.pyx4j.ria.client.theme.Windows7Theme;
import com.pyx4j.widgets.client.style.StyleManger;

public class ApplicationManager {

    private static ApplicationManager instance;

    private IApplication currentApplication;

    private ApplicationManager() {
        StyleManger.installTheme(new Windows7Theme());
    }

    public static ApplicationManager instance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    public static void loadApplication(IApplication application) {
        //If another application exist, fire ApplicationClos
        if (instance().currentApplication != null) {
            discardApplication();
        }
        instance().currentApplication = application;
        //instance().styleInjector.injectStyle(lookAndFeel.getStyles());
        application.onLoad();
    }

    public static void discardApplication() {
        if (instance().currentApplication != null) {
            instance().currentApplication.onDiscard();
            instance().currentApplication = null;
        }
    }

    public static IApplication getCurrentApplication() {
        return instance().currentApplication;
    }

}
