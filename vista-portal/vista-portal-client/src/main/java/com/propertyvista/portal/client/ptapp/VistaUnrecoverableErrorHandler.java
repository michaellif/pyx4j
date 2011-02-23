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
package com.propertyvista.portal.client.ptapp;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent.UserMessageType;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.DefaultUnrecoverableErrorHandler;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.security.client.ClientContext;

public class VistaUnrecoverableErrorHandler extends DefaultUnrecoverableErrorHandler {

    private static I18n i18n = I18nFactory.getI18n(VistaUnrecoverableErrorHandler.class);

    private final EventBus eventBus;

    @Inject
    public VistaUnrecoverableErrorHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        UncaughtHandler.setUnrecoverableErrorHandler(this);
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
        showMessage("This session has been terminated .", UserMessageType.FAILURE);

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
    protected void showDefaultError(Throwable caught, String errorCode) {

        String detailsMessage = "";
        if (ApplicationMode.isDevelopment() && CommonsStringUtils.isStringSet(caught.getMessage()) && caught.getMessage().length() < 220) {
            detailsMessage += "\n" + caught.getMessage();
        }

        boolean sessionClosed = false;

        showMessage(i18n.tr("An Unexpected Error Has Occurred.") + " " +

        i18n.tr("Please report the incident to technical support,\n"

        + "describing the steps taken prior to the error.\n")

        + ((sessionClosed) ? "\n" + i18n.tr("This session has been terminated to prevent data corruption.") : "")

        + detailsMessage, UserMessageType.ERROR);

        if (ApplicationMode.isDevelopment()) {
            String debugMessage = "";
            if (errorCode != null) {
                debugMessage += "ErrorCode [" + errorCode + "]";
            }
            if (caught != null) {
                debugMessage += "\n" + caught.getClass();
                if (caught instanceof StatusCodeException) {
                    debugMessage += " StatusCode: " + (((StatusCodeException) caught).getStatusCode());
                }
            }
            if (debugMessage.length() > 0) {
                showMessage(debugMessage, UserMessageType.DEBUG);
            }
        }
    }

    protected void showMessage(String string, UserMessageType messageType) {
        eventBus.fireEvent(new UserMessageEvent(string, messageType));

    }
}