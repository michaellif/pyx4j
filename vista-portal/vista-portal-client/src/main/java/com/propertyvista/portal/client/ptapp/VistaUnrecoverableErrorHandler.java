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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent.UserMessageType;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.gwt.commons.UnrecoverableClientWarning;
import com.pyx4j.gwt.commons.UnrecoverableErrorHandler;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class VistaUnrecoverableErrorHandler implements UnrecoverableErrorHandler {

    private static I18n i18n = I18nFactory.getI18n(VistaUnrecoverableErrorHandler.class);

    private final EventBus eventBus;

    @Inject
    public VistaUnrecoverableErrorHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        UncaughtHandler.setUnrecoverableErrorHandler(this);
    }

    @Override
    public void onUnrecoverableError(final Throwable caught, final String errorCode) {
        // Handle the case when 'stack size exceeded', show dialog later.
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                showMessage(caught, errorCode);
            }
        });

    }

    protected void showMessage(final Throwable caught, final String errorCode) {
        Throwable cause = caught;
        while ((cause instanceof UmbrellaException)
                || ((cause instanceof UnrecoverableClientError) && (cause.getCause() != null) && (cause.getCause() != cause))) {
            if (cause instanceof UmbrellaException) {
                try {
                    cause = ((UmbrellaException) cause).getCauses().iterator().next();
                } catch (Throwable ignore) {
                    break;
                }
            } else {
                cause = cause.getCause();
            }
        }

        if (cause instanceof UserRuntimeException) {
            showWarningMessage(cause.getMessage());
        } else if (cause instanceof UnrecoverableClientWarning) {
            showWarningMessage(cause.getMessage());
        } else if (cause instanceof IncompatibleRemoteServiceException) {
            showReloadApplicationMessage();
        } else if ((cause instanceof StatusCodeException) && (((StatusCodeException) cause).getStatusCode()) == Response.SC_NOT_FOUND) {
            showReloadApplicationMessage();
        } else if ((cause instanceof StatusCodeException) && (((StatusCodeException) cause).getStatusCode()) == Response.SC_PRECONDITION_FAILED) {
            showThrottleMessage();
        } else if ((cause instanceof RuntimeException) && ("HTTP download failed with status 404".equals(cause.getMessage()))) {
            // TODO see if com.google.gwt.core.client.impl.AsyncFragmentLoader.HttpDownloadFailure was made public
            showReloadApplicationMessage();
        } else {
            showDefaultErrorMessage(cause, errorCode);
        }
    }

    protected void showReloadApplicationMessage() {
        String message = i18n.tr("We updated our application.\nIn order to continue using this application you need to refresh the page."
                + "\nPlease refresh the page now!");
        showMessage(message, UserMessageType.FAILURE);
    }

    protected void showWarningMessage(String text) {
        showMessage(text, UserMessageType.WARN);
    }

    protected void showThrottleMessage() {
        showMessage(i18n.tr("We're sorry but your requests look similar to automated requests initiated by computer virus or spyware applications. "
                + "To protect our users, we can't process your request at this time."), UserMessageType.FAILURE);
    }

    protected void showDefaultErrorMessage(Throwable caught, String errorCode) {

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