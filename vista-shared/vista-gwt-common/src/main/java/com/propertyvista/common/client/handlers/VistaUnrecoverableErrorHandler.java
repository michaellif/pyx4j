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
package com.propertyvista.common.client.handlers;

import java.util.Date;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.DefaultUnrecoverableErrorHandler;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.events.NotificationEvent;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.shared.config.VistaDemo;

public class VistaUnrecoverableErrorHandler extends DefaultUnrecoverableErrorHandler {

    private static final I18n i18n = I18n.get(VistaUnrecoverableErrorHandler.class);

    public VistaUnrecoverableErrorHandler() {
        UncaughtHandler.setUnrecoverableErrorHandler(this);
    }

    @Override
    protected void selectError(final Throwable caught, final String errorCode) {
        super.selectError(caught, errorCode);
    }

    @Override
    protected void showReloadApplication() {
        String message = i18n.tr("We Updated Our Application\nIn Order To Continue Using This Application You Need To Refresh The Page"
                + "\nPlease Refresh The Page Now");
        showMessage(message, NotificationType.FAILURE);
    }

    @Override
    protected void showUnauthorized() {
        ClientContext.terminateSession();
        if (ApplicationMode.isDevelopment()) {
            // This is response from our GoolgeApps login, environment restarted. Need to relogin.
            Window.Location.reload();
        } else {
            showMessage(i18n.tr("This Session Has Been Terminated"), NotificationType.FAILURE);
        }

    }

    @Override
    protected void showWarning(String text) {
        showMessage(text, NotificationType.WARNING);
    }

    @Override
    protected void showThrottle() {
        showMessage(i18n.tr("We're Sorry But Your Requests Look Similar To Automated Requests Initiated By Computer Virus Or Spyware Applications. "
                + "To Protect Our Users, We Can't Process Your Request At This Time"), NotificationType.FAILURE);
    }

    @Override
    protected void showInternetConnectionError() {
        showMessage(i18n.tr("Please make sure you are connected to Internet"), NotificationType.FAILURE);

    }

    @Override
    protected void showHttpStatusCode(StatusCodeException caught, int statusCode, String errorCode) {
        showDefaultError(caught, errorCode);
    }

    @Override
    protected void showUserError(String text, UserRuntimeException cause) {
        showMessage(text, NotificationType.ERROR);
    }

    @Override
    protected void showDefaultError(Throwable caught, String errorCode) {

        boolean sessionClosed = false;

        String userMessage = i18n.tr("An Unexpected Error Has Occurred.") + " ";

        userMessage += i18n.tr("Please report the incident to technical support,\n" + "describing the steps taken prior to the error.\n");

        if (sessionClosed) {
            userMessage += "\n" + i18n.tr("This Session Has Been Terminated To Prevent Data Corruption");
        }

        if (ApplicationMode.isDevelopment() && CommonsStringUtils.isStringSet(caught.getMessage()) && caught.getMessage().length() < 220) {
            userMessage += "\n(DEV)\n" + caught.getMessage();
        }

        StringBuilder debugMessage = new StringBuilder();

        if (!VistaDemo.isDemo() && ApplicationMode.isDevelopment()) {
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

        showMessage(userMessage, debugMessage.toString(), NotificationType.ERROR);
    }

    protected void showMessage(String userMessage, String debugMessage, NotificationType messageType) {
        AppSite.getEventBus().fireEvent(new NotificationEvent(userMessage, debugMessage, messageType));

    }

    protected void showMessage(String userMessage, NotificationType messageType) {
        showMessage(userMessage, null, messageType);
    }

}