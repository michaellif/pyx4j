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
 * Created on Jul 15, 2009
 * @author vlads
 */
package com.pyx4j.gwt.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.web.bindery.event.shared.UmbrellaException;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;

public class UncaughtHandler implements UncaughtExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(UncaughtHandler.class);

    public static String UNEXPECTED_ERROR_MESSAGE = "An Unexpected Error Has Occurred";

    private static UncaughtHandler instance;

    private static UncaughtExceptionHandler handlerReplacement = null;

    private static UnrecoverableErrorHandler delegate = null;

    public static void init() {
        if (instance != null) {
            return;
        }
        instance = new UncaughtHandler();
    }

    private UncaughtHandler() {
        GWT.setUncaughtExceptionHandler(this);
    }

    public static void replaceUncaughtExceptionHandler(UncaughtExceptionHandler testHandler) {
        init();
        UncaughtHandler.handlerReplacement = testHandler;
    }

    public static void setUnrecoverableErrorHandler(UnrecoverableErrorHandler delegate) {
        init();
        UncaughtHandler.delegate = delegate;
    }

    @Override
    public void onUncaughtException(Throwable e) {
        onUnrecoverableError(e, null);
    }

    public static native String userAgent() /*-{ return navigator.userAgent; }-*/;

    public static void onUnrecoverableError(Throwable caught, String errorCode) {
        if (handlerReplacement != null) {
            handlerReplacement.onUncaughtException(caught);
        } else {
            try {
                if (!(caught instanceof IncompatibleRemoteServiceException)) {
                    try {
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
                        boolean skipLogStackTrace = false;
                        if (cause instanceof UserRuntimeException) {
                            skipLogStackTrace = ((UserRuntimeException) cause).isSkipLogStackTrace();
                        }

                        if (!skipLogStackTrace) {
                            if (GWT.isScript()) {
                                log.error(UNEXPECTED_ERROR_MESSAGE + ((errorCode != null) ? "[" + errorCode + "] " : " ") + ";\n Href "
                                        + Window.Location.getHref() + ";\n UserAgent " + userAgent(), cause);
                            } else {
                                log.error(UNEXPECTED_ERROR_MESSAGE, cause);
                            }
                            GoogleAnalytics.track("unrecoverableError");
                        }
                    } catch (Throwable ignore) {
                        System.out.println("error in exception handling" + ignore);
                    }
                }
                if (UncaughtHandler.delegate != null) {
                    UncaughtHandler.delegate.onUnrecoverableError(caught, errorCode);
                } else {
                    Window.alert(UNEXPECTED_ERROR_MESSAGE + "." + (ApplicationMode.isDevelopment() ? "\n" + caught : ""));
                }
            } catch (Throwable e) {
                Window.alert(UNEXPECTED_ERROR_MESSAGE + "!" + (ApplicationMode.isDevelopment() ? "\n" + caught : ""));
                log.error("Internal error [UH]", e);
            }
        }
    }

}
