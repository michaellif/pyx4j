package com.pyx4j.site.admin.client;

import com.google.gwt.core.client.EntryPoint;

import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.ria.client.ApplicationManager;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AdminClient implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        ClientLogger.setDebugOn(true);
        ApplicationManager.loadApplication(new AdminApplication());

    }

}
