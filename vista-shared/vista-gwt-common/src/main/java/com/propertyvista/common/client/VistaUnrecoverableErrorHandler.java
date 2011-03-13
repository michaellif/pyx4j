/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client;

import java.util.Date;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.inject.Inject;
import com.propertyvista.common.client.events.UserMessageEvent;
import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.DefaultUnrecoverableErrorHandler;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.util.BrowserType;

public class VistaUnrecoverableErrorHandler extends DefaultUnrecoverableErrorHandler {

    private static I18n i18n = I18nFactory.getI18n(VistaUnrecoverableErrorHandler.class);

    private final EventBus eventBus;

    @Inject
    public VistaUnrecoverableErrorHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        UncaughtHandler.setUnrecoverableErrorHandler(this);
    }

    @Override
    protected void selectError(final Throwable caught, final String errorCode) {
        super.selectError(caught, errorCode);
    }

    @Override
    protected void showReloadApplication() {
        String message = i18n.tr("We updated our application.\nIn order to continue using this application you need to refresh the page."
                + "\nPlease refresh the page now!");
        showMessage(message, UserMessageType.FAILURE);
    }

    @Override
    protected void showUnauthorized() {
        ClientContext.terminateSession();
        showMessage(i18n.tr("This session has been terminated ."), UserMessageType.FAILURE);

    }

    @Override
    protected void showWarning(String text) {
        showMessage(text, UserMessageType.WARN);
    }

    @Override
    protected void showThrottle() {
        showMessage(i18n.tr("We're sorry but your requests look similar to automated requests initiated by computer virus or spyware applications. "
                + "To protect our users, we can't process your request at this time."), UserMessageType.FAILURE);
    }

    @Override
    protected void showInternetConnectionError() {
        showMessage(i18n.tr("Please make sure you are connected to internet."), UserMessageType.FAILURE);

    }

    @Override
    protected void showHttpStatusCode(StatusCodeException caught, int statusCode, String errorCode) {
        if ((statusCode == 0) && BrowserType.isFirefox()) {
            showInternetConnectionError();
        } else {
            showDefaultError(caught, errorCode);
        }
    }

    @Override
    protected void showDefaultError(Throwable caught, String errorCode) {

        String detailsMessage = "";
        if (ApplicationMode.isDevelopment() && CommonsStringUtils.isStringSet(caught.getMessage()) && caught.getMessage().length() < 220) {
            detailsMessage += "\n" + caught.getMessage();
        }

        boolean sessionClosed = false;

        String userMessage = i18n.tr("An Unexpected Error Has Occurred.") + " " +

        i18n.tr("Please report the incident to technical support,\n"

        + "describing the steps taken prior to the error.\n")

        + ((sessionClosed) ? "\n" + i18n.tr("This session has been terminated to prevent data corruption.") : "")

        + detailsMessage;

        StringBuilder debugMessage = new StringBuilder();

        if (ApplicationMode.isDevelopment()) {
            debugMessage.append(new Date());
            if (errorCode != null) {
                debugMessage.append("ErrorCode [" + errorCode + "]");
            }
            if (caught != null) {
                debugMessage.append("\n" + caught.getClass());
                if (caught instanceof StatusCodeException) {
                    debugMessage.append(" StatusCode: " + (((StatusCodeException) caught).getStatusCode()));
                }
            }
        }

        showMessage(userMessage, debugMessage.toString(), UserMessageType.ERROR);
    }

    protected void showMessage(String userMessage, String debugMessage, UserMessageType messageType) {
        eventBus.fireEvent(new UserMessageEvent(userMessage, debugMessage, messageType));

    }

    protected void showMessage(String userMessage, UserMessageType messageType) {
        showMessage(userMessage, null, messageType);
    }

}