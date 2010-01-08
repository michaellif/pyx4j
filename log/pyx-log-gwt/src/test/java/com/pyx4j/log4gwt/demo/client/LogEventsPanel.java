/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 27, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.log4gwt.client.ClientLogger;

public class LogEventsPanel extends VerticalPanel {

    private static final Logger log = LoggerFactory.getLogger(LogEventsPanel.class);

    public LogEventsPanel() {

        final TextBox text = new TextBox();
        text.setValue("A log message with data {} and {} (if any)");
        this.add(text);

        final CheckBox checkDebug = new CheckBox("Debug On");
        checkDebug.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ClientLogger.setDebugOn(checkDebug.getValue());
            }
        });
        checkDebug.setValue(ClientLogger.isDebugOn());
        this.add(checkDebug);

        final HorizontalPanel debugPanel = new HorizontalPanel();
        this.add(debugPanel);

        debugPanel.add(new Button("Logg.debug", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.debug(text.getValue());
            }
        }));

        debugPanel.add(new Button("debug('',int)", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.debug(text.getValue(), 22);
            }
        }));
        debugPanel.add(new Button("debug('',int,int)", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.debug(text.getValue(), 7, 49);
            }
        }));

        debugPanel.add(new Button("debug('',int,null)", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.debug(text.getValue(), 11, null);
            }
        }));

        debugPanel.add(new Button("debug('',String)", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.debug(text.getValue(), "Any String");
            }
        }));

        debugPanel.add(new Button("debug('',int[])", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.debug(text.getValue(), new int[] { 1, 2, 3, 4 });
            }
        }));

        final HorizontalPanel otherPanel = new HorizontalPanel();
        this.add(otherPanel);

        otherPanel.add(new Button("Logg.info", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.info(text.getValue());
            }
        }));

        otherPanel.add(new Button("Logg.warn", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.warn(text.getValue());
            }
        }));

        otherPanel.add(new Button("Logg.error", new ClickHandler() {
            public void onClick(ClickEvent event) {
                log.error(text.getValue(), new Error("Try this one"));
            }
        }));
    }
}
