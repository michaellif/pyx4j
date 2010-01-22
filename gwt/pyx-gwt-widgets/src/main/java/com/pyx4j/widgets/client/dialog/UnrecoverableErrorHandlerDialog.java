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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.gwt.commons.UnrecoverableErrorHandler;

public class UnrecoverableErrorHandlerDialog implements UnrecoverableErrorHandler {

    public static void register() {
        UncaughtHandler.setUnrecoverableErrorHandler(new UnrecoverableErrorHandlerDialog());
    }

    @Override
    public void onUnrecoverableError(Throwable e, String errorCode) {

        String detailsMessage = null;
        if (CommonsStringUtils.isStringSet(e.getMessage()) && e.getMessage().length() < 30) {
            detailsMessage = "\n\nErrorCode ";
            if (errorCode != null) {
                detailsMessage += "[" + errorCode + "] ";
            }
            detailsMessage += e.getMessage();
        } else if (errorCode != null) {
            detailsMessage = "\n\nErrorCode [" + errorCode + "]";
        }

        boolean todoSessionClosed = false;

        MessageDialog.error("An Unexpected Error Has Occurred",

        "Please report the incident to technical support,\n"

        + "describing the steps taken prior to the error.\n"

        + ((todoSessionClosed) ? "\nThis session has been terminated to prevent data corruption." : "")

        + ((detailsMessage != null) ? detailsMessage : ""));

    }
}
