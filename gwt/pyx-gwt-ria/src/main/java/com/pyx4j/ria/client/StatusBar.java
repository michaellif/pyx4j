/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 23, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ProgressBar;

import com.pyx4j.widgets.client.style.Theme.CSSClass;

/**
 * @author michaellif
 * 
 */
public class StatusBar extends HorizontalPanel {

    private final MessagePanel messagePanel;

    private final ProgressBar progressBar;

    private final Label loginMessage;

    private final Label versionMessage;

    public StatusBar() {

        loginMessage = new Label("", false);
        setLoginMessage(null);
        addItem(loginMessage);

        addSeparator();

        versionMessage = new Label("Version:", false);
        addItem(versionMessage);

        addSeparator();

        messagePanel = new MessagePanel();
        addItem(messagePanel);

        addSeparator();

        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        add(progressBar);
        progressBar.setWidth("100%");
        progressBar.setHeight("20px");

        setCellWidth(messagePanel, "80%");
        setCellHeight(messagePanel, "100%");
        setCellWidth(progressBar, "20%");
        setCellHeight(progressBar, "100%");

        setStyleName(CSSClass.pyx4j_StatusBar.name());
    }

    public void setLoginMessage(String userId) {
        if (userId == null) {
            loginMessage.setText("Logged out");
        } else {
            loginMessage.setText("Logged in as " + userId);
        }
    }

    public void setVersionMessage(String message) {
        versionMessage.setText("Version: " + message);
    }

    public void addItem(Widget item) {
        add(item);
        this.setCellHeight(item, "100%");
        this.setCellVerticalAlignment(item, ALIGN_MIDDLE);
    }

    public void addSeparator() {
        BarSeparator separator = new BarSeparator();
        add(separator);
        this.setCellVerticalAlignment(separator, ALIGN_MIDDLE);
    }

    /**
     * @param i
     */
    public void setProgress(int i) {
        progressBar.setProgress(i);
    }

    /**
     * @return
     */
    public double getProgress() {
        return progressBar.getProgress();
    }

    /**
     * @param b
     */
    public void setProgressBarVisible(boolean visible) {
        progressBar.setVisible(visible);
    }

}
