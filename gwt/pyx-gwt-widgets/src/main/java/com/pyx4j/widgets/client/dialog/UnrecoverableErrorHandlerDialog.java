/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 22, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.DefaultUnrecoverableErrorHandler;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.util.BrowserType;

public class UnrecoverableErrorHandlerDialog extends DefaultUnrecoverableErrorHandler {

    private static I18n i18n = I18n.get(UnrecoverableErrorHandlerDialog.class);

    /**
     * Only one instance of Dialog is shown.
     */
    protected static boolean unrecoverableErrorDialogShown = false;

    public static void register() {
        UncaughtHandler.setUnrecoverableErrorHandler(new UnrecoverableErrorHandlerDialog());
    }

    protected static class ShowOnceDialogOptions implements OkOption, CloseHandler<PopupPanel> {

        public ShowOnceDialogOptions() {
            unrecoverableErrorDialogShown = true;
        }

        @Override
        public boolean onClickOk() {
            return true;
        }

        @Override
        public void onClose(CloseEvent<PopupPanel> event) {
            unrecoverableErrorDialogShown = false;
        }

    };

    /**
     * Session aware applications can override this function.
     * 
     * @return true if there was a session and it is Closed Now or would we closed ASAP.
     */
    @Override
    protected boolean closeSessionOnUnrecoverableError() {
        return false;
    }

    @Override
    public void onUnrecoverableError(final Throwable caught, final String errorCode) {
        if (unrecoverableErrorDialogShown) {
            return;
        }
        unrecoverableErrorDialogShown = true;
        // Handle the case when 'stack size exceeded', show dialog later.
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                try {
                    selectError(caught, errorCode);
                } catch (Throwable e) {
                    unrecoverableErrorDialogShown = false;
                }
            }
        });

    }

    @Override
    protected void showReloadApplication() {
        final YesNoOption optYesNo = new YesNoOption() {

            @Override
            public boolean onClickYes() {
                Window.Location.reload();
                return false;
            }

            @Override
            public boolean onClickNo() {
                return true;
            }
        };
        String message = i18n.tr("We updated our application.\nIn order to continue using this application you need to refresh the page."
                + "\n Do you want to refresh the page now?");
        MessageDialog.show(i18n.tr("System error"), message, Type.Error, optYesNo);
    }

    @Override
    protected void showWarning(String text) {
        MessageDialog.show(i18n.tr("Warning"), text, Type.Warning, new ShowOnceDialogOptions());
    }

    @Override
    protected void showThrottle() {
        MessageDialog.show(i18n.tr("We're sorry"), i18n
                .tr("We're sorry but your requests look similar to automated requests initiated by computer virus or spyware applications. "
                        + "To protect our users, we can't process your request at this time."), Type.Error, new ShowOnceDialogOptions());
    }

    @Override
    protected void showUnauthorized() {
        MessageDialog.show(i18n.tr("We're sorry"), i18n.tr("This session has been terminated."), Type.Error, new ShowOnceDialogOptions());
    }

    @Override
    protected void showInternetConnectionError() {
        showWarning(i18n.tr("Please make sure you are connected to Internet."));

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
        if (CommonsStringUtils.isStringSet(caught.getMessage()) && caught.getMessage().length() < 220) {
            detailsMessage += "\n" + caught.getMessage();
        }
        if (ApplicationMode.isDevelopment() && (errorCode != null)) {
            detailsMessage += "\n\nErrorCode [" + errorCode + "]";
        }

        if (ApplicationMode.isDevelopment() && (caught != null)) {
            detailsMessage += "\n" + caught.getClass();
            if (caught instanceof StatusCodeException) {
                detailsMessage += " StatusCode: " + (((StatusCodeException) caught).getStatusCode());
            }
        }

        boolean sessionClosed = closeSessionOnUnrecoverableError();

        MessageDialog.show(i18n.tr("An Unexpected Error Has Occurred"),

        i18n.tr("Please report the incident to technical support,\n"

        + "describing the steps taken prior to the error.\n")

        + ((sessionClosed) ? "\n" + i18n.tr("This session has been terminated to prevent data corruption.") : "")

        + detailsMessage, Type.Error, new ShowOnceDialogOptions());
    }

}
