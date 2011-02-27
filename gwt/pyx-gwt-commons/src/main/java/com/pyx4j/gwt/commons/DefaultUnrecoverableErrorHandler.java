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
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.StatusCodeException;

import com.pyx4j.rpc.shared.IsWarningException;

public abstract class DefaultUnrecoverableErrorHandler implements UnrecoverableErrorHandler {

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

    protected void selectError(final Throwable caught, final String errorCode) {
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
        if (cause instanceof IsWarningException) {
            showWarning(cause.getMessage());
        } else if (cause instanceof IncompatibleRemoteServiceException) {
            showReloadApplication();
        } else if (cause instanceof StatusCodeException) {
            int statusCode = ((StatusCodeException) cause).getStatusCode();
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
                showInternetConnectionError();
                break;
            default:
                showHttpStatusCode((StatusCodeException) cause, statusCode, errorCode);
            }
        } else if ((cause instanceof RuntimeException) && ("HTTP download failed with status 404".equals(cause.getMessage()))) {
            // TODO see if com.google.gwt.core.client.impl.AsyncFragmentLoader.HttpDownloadFailure was made public
            showReloadApplication();
        } else {
            showDefaultError(cause, errorCode);
        }
    }

    protected abstract void showReloadApplication();

    protected abstract void showUnauthorized();

    protected abstract void showWarning(String text);

    protected abstract void showThrottle();

    protected abstract void showHttpStatusCode(StatusCodeException caught, int statusCode, String errorCode);

    protected abstract void showInternetConnectionError();

    protected abstract void showDefaultError(Throwable caught, String errorCode);
}
