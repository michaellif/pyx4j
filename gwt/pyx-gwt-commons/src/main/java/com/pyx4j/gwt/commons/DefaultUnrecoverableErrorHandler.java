/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-02-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.StatusCodeException;

import com.pyx4j.commons.IsWarningException;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ClientVersionMismatchError;

public abstract class DefaultUnrecoverableErrorHandler implements UnrecoverableErrorHandler {

    private ClosingEvent lastClosingEvent;

    private static boolean applicationInitialized = false;

    protected DefaultUnrecoverableErrorHandler() {
        Window.addWindowClosingHandler(new ClosingHandler() {

            @Override
            public void onWindowClosing(ClosingEvent event) {
                lastClosingEvent = event;
            }
        });
    }

    public boolean isWindowClosing() {
        return (lastClosingEvent != null) && (lastClosingEvent.getMessage() == null);
    }

    /**
     * Session aware applications can override this function.
     * 
     * @return true if there was a session and it is Closed Now or would we closed ASAP.
     */
    protected boolean closeSessionOnUnrecoverableError() {
        return false;
    }

    @Override
    public void onUnrecoverableError(final Throwable caught, final String errorCode) {
        // Handle the case when 'stack size exceeded', show dialog later.
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                try {
                    selectError(caught, errorCode);
                } catch (Throwable e) {
                }
            }
        });

    }

    protected Throwable unwrapCause(Throwable caught) {
        Throwable cause = caught;
        while ((cause instanceof com.google.web.bindery.event.shared.UmbrellaException)
                || ((cause instanceof UnrecoverableClientError) && (cause.getCause() != null) && (cause.getCause() != cause))) {
            if (cause instanceof com.google.web.bindery.event.shared.UmbrellaException) {
                try {
                    cause = ((com.google.web.bindery.event.shared.UmbrellaException) cause).getCauses().iterator().next();
                } catch (Throwable ignore) {
                    break;
                }
            } else {
                cause = cause.getCause();
            }
        }
        return cause;
    }

    protected boolean isVersionMismatch(Throwable cause) {
        if ((cause instanceof IncompatibleRemoteServiceException) || (cause instanceof ClientVersionMismatchError)) {
            return true;
        } else if (cause instanceof RuntimeException) {
            // TODO see if com.google.gwt.core.client.impl.AsyncFragmentLoader.HttpDownloadFailure was made public
            String message = cause.getMessage();
            if ((message != null) && message.contains("Download of") && message.contains("failed with status") && message.contains("404")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Since we stuck in development with old Firefox 26 lets ignore its errors.
     */
    public static void setApplicationInitialized() {
        applicationInitialized = true;
    }

    protected void selectError(final Throwable caught, final String errorCode) {
        // Ignore Old firefox 26 initialization errors
        if (!applicationInitialized && BrowserType.isFirefox() && caught.getMessage() != null && caught.getMessage().contains("gwt$exception: <skipped>")
                && caught.getMessage().contains("Permission denied to access property")) {
            return;
        }
        Throwable cause = unwrapCause(caught);
        if (cause instanceof IsWarningException) {
            showWarning(cause.getMessage());
        } else if (cause instanceof UserRuntimeException) {
            showUserError(cause.getMessage(), (UserRuntimeException) cause);
        } else if (isVersionMismatch(cause)) {
            showReloadApplication();
        } else if (cause instanceof StatusCodeException) {
            int statusCode = ((StatusCodeException) cause).getStatusCode();
            if (isWindowClosing() && statusCode == 0) {
                // Ignore RPC errors when page is Closing e.g. navigating to another page in browser
                return;
            }
            switch (statusCode) {
            case Response.SC_NOT_FOUND:
                showReloadApplication();
                break;
            case Response.SC_PRECONDITION_FAILED:
                showThrottle();
                break;
            case Response.SC_UNAUTHORIZED:
                showUnauthorized();
                break;
            case 12007:
            case 12029:
            case 12152:
                showInternetConnectionError();
                break;
            default:
                if ((statusCode == 0) && (BrowserType.isFirefox() || BrowserType.isSafari())) {
                    showInternetConnectionError();
                } else {
                    showHttpStatusCode((StatusCodeException) cause, statusCode, errorCode);
                }
            }
        } else {
            showDefaultError(cause, errorCode);
        }
    }

    protected abstract void showReloadApplication();

    protected abstract void showUnauthorized();

    protected abstract void showWarning(String text);

    protected abstract void showUserError(String text, UserRuntimeException cause);

    protected abstract void showThrottle();

    protected abstract void showHttpStatusCode(StatusCodeException caught, int statusCode, String errorCode);

    protected abstract void showInternetConnectionError();

    protected abstract void showDefaultError(Throwable caught, String errorCode);
}
