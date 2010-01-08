/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on May 17, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.pyx4j.ria.client.ImageFactory;

public class StatusBarProvingView extends AbstractProvingView {

    public StatusBarProvingView() {
        super("StatusBar Range", ImageFactory.getImages().debugOn());

        ActionGroup progress = createActionGroup("ProgressBar");

        progress.addAction("start", new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });

        progress.addAction("stop", new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });

        ActionGroup message = createActionGroup("StatusBar Message");

        message.addAction("info(s)", new Runnable() {
            @Override
            public void run() {

                //TODO  Logger.info("Some info");
            }
        });

        message.addAction("warn(s)", new Runnable() {
            @Override
            public void run() {

                //TODO Logger.warn("Some warn");
            }
        });

        message.addAction("error(s)", new Runnable() {
            @Override
            public void run() {

                // TODO Logger.error("Some error");
            }
        });

        ActionGroup icons = createActionGroup("StatusBar icons");

        icons.addAction("activate", new Runnable() {
            @Override
            public void run() {

                // TODO Logger.error("TODO migrate");
            }
        });
    }

}
