/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.pyx4j.log4gwt.client.Appender;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.ria.client.ImageFactory;

public class LogRangeView extends AbstractProvingView {

    private static final Logger log = LoggerFactory.getLogger(LogRangeView.class);

    private int n = 1;

    public LogRangeView() {
        super("Log Range", ImageFactory.getImages().debugOn());

        super.setDescription("Manualy test Logger and error handling");

        final Label infoText = new Label();

        final TextBox text = new TextBox();
        text.setValue("a message");
        mainPanel.add(text);

        ActionGroup debug = createActionGroup("Logger.debug(...)");

        debug.addAction("(s)", new Runnable() {
            @Override
            public void run() {
                log.debug(text.getValue());
            }
        });

        debug.addAction("(s, int)", new Runnable() {
            @Override
            public void run() {
                log.debug(text.getValue(), n++);
            }
        });

        debug.addAction("(s, int, int)", new Runnable() {
            @Override
            public void run() {
                log.debug(text.getValue(), 7, 49);
            }
        });

        debug.addAction("(s, int, null)", new Runnable() {
            @Override
            public void run() {
                log.debug(text.getValue(), 11, null);
            }
        });

        debug.addAction("(s, s2)", new Runnable() {
            @Override
            public void run() {
                log.debug(text.getValue(), "Any second String");
            }
        });

        ActionGroup other = createActionGroup("Logger. other");

        other.addAction("info(s)", new Runnable() {
            @Override
            public void run() {
                log.info(text.getValue());
            }
        });

        other.addAction("warn(s)", new Runnable() {
            @Override
            public void run() {
                log.warn(text.getValue());
            }
        });

        other.addAction("error(s, throwable)", new Runnable() {
            @Override
            public void run() {
                log.error(text.getValue(), new Error("Try this one"));
            }
        });

        ActionGroup errors = createActionGroup("Error handling");
        errors.addAction("catch", new Runnable() {
            @Override
            public void run() {
                try {
                    throw new Exception("a problem");
                } catch (Exception e) {
                    log.error("Got Exception", e);
                }
            }
        });

        errors.addAction("NPE", new Runnable() {
            @Override
            @SuppressWarnings("null")
            public void run() {
                try {
                    ClickHandler obj = null;
                    obj.onClick(null);
                } catch (Exception e) {
                    log.error("Got NPE", e);
                }
            }
        });

        errors.addAction("Unhandled", new Runnable() {
            @Override
            public void run() {
                throw new Error("Unhandled problem");
            }
        });

        mainPanel.add(new Button("list appenders", new ClickHandler() {
            public void onClick(ClickEvent event) {
                StringBuilder info = new StringBuilder();
                info.append("Current Logger appenders:");
                if (ClientLogger.getAllAppenders() == null) {
                    info.append("None");
                } else {
                    for (Appender a : ClientLogger.getAllAppenders()) {
                        info.append(a.getAppenderName()).append(' ');
                    }
                }
                infoText.setText(info.toString());
            }
        }));

        mainPanel.add(infoText);
    }

}
