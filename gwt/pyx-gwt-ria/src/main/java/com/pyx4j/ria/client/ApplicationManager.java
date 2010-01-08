/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class ApplicationManager {

    private static ApplicationManager instance;

    private IApplication currentApplication;

    private ApplicationManager() {
        StyleManger.installTheme(new WindowsTheme());
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
