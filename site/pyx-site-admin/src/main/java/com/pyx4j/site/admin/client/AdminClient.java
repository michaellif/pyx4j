package com.pyx4j.site.admin.client;

import com.google.gwt.core.client.EntryPoint;

import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.ria.client.ApplicationManager;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AdminClient implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        ClientEntityFactory.ensureIEntityImplementations();
        ClientLogger.addAppender(new RPCAppender(Level.WARN));
        ClientLogger.setDebugOn(true);
        UnrecoverableErrorHandlerDialog.register();

        ClientLogger.setDebugOn(true);
        ApplicationManager.loadApplication(new AdminApplication());

    }

}
