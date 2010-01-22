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
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

public class UncaughtHandler implements UncaughtExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(UncaughtHandler.class);

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

    public static void onUnrecoverableError(Throwable e, String errorCode) {
        if (handlerReplacement != null) {
            handlerReplacement.onUncaughtException(e);
        } else {
            try {
                log.error("An Unexpected Error Has Occurred" + ((errorCode != null) ? "[" + errorCode + "] " : " ")
                /* + Logger. retriveTraceInfo ( ) */
                + ";\n UserAgent " + userAgent(), e);
                GoogleAnalytics.track("unrecoverableError");
                if (UncaughtHandler.delegate != null) {
                    UncaughtHandler.delegate.onUnrecoverableError(e, errorCode);
                }
            } catch (Throwable ignore) {
            }
        }
    }

}
