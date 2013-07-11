/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.SimpleMessageFormat;

import com.propertyvista.common.client.ui.components.login.LoginView.DevLoginCredentials;
import com.propertyvista.domain.DemoData;

abstract class DevLoginPanel extends Composite {

    private final FlowPanel devLoginAnchorsPanel;

    private HTML applicationModeLabel;

    private int prevDevKey;

    private int devCount;

    private List<? extends DevLoginCredentials> credentialsSet;

    public DevLoginPanel() {
        FlowPanel devMessagePanel = new FlowPanel();
        devMessagePanel.getElement().getStyle().setMargin(20, Unit.PX);

        devMessagePanel.addAttachHandler(new AttachEvent.Handler() {
            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey()) {
                                if (devCredentialsSelected(event.getNativeEvent().getKeyCode())) {
                                    event.getNativeEvent().preventDefault();
                                }
                            }
                        }
                    });
                } else {
                    handlerRegistration.removeHandler();
                }
            }
        });

        devMessagePanel.add(applicationModeLabel = new HTML());

        devLoginAnchorsPanel = new FlowPanel();
        devMessagePanel.add(devLoginAnchorsPanel);

        initWidget(devMessagePanel);
    }

    public void setApplicationModeName(String applicationModeName) {
        applicationModeLabel.setHTML("This application is running in <B>" + DemoData.applicationModeName() + "</B> mode.");
    }

    public void setDevCredentials(final List<? extends DevLoginCredentials> credentialsSet) {
        this.credentialsSet = credentialsSet;
        devLoginAnchorsPanel.clear();

        for (final DevLoginCredentials credentials : credentialsSet) {
            Anchor touchAnchor = new Anchor(SimpleMessageFormat.format("Press Ctrl+{0} to login as {1}", (char) credentials.getHotKey(), credentials
                    .getUserType().toString()));
            touchAnchor.getElement().getStyle().setProperty("textDecoration", "none");
            touchAnchor.getElement().getStyle().setDisplay(Display.BLOCK);
            touchAnchor.addClickHandler(new ClickHandler() {

                private final int hotKey = credentials.getHotKey();

                @Override
                public void onClick(ClickEvent event) {
                    devCredentialsSelected(hotKey);
                }

            });
            devLoginAnchorsPanel.add(touchAnchor);
        }
    }

    protected abstract void onDevCredentialsSelected(String userId, String password);

    private boolean devCredentialsSelected(int hotKey) {
        if (prevDevKey != hotKey) {
            devCount = 0;
        }
        prevDevKey = hotKey;
        for (DevLoginCredentials credentials : credentialsSet) {
            if (hotKey == credentials.getHotKey()) {
                devCount = (devCount % credentials.getUserType().getDefaultMax()) + 1;
                onDevCredentialsSelected(credentials.getUserType().getEmail(devCount), credentials.getUserType().getEmail(devCount));
                return true;
            }
        }
        return false;
    }
}