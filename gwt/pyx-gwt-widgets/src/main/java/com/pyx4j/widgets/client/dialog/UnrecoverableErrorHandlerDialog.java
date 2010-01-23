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
import com.pyx4j.widgets.client.dialog.Dialog.Type;

public class UnrecoverableErrorHandlerDialog implements UnrecoverableErrorHandler {

    /**
     * Only one instance of Dialog is shown.
     */
    private static boolean unrecoverableErrorDialogShown = false;

    public static void register() {
        UncaughtHandler.setUnrecoverableErrorHandler(new UnrecoverableErrorHandlerDialog());
    }

    /**
     * Session aware applications can override this function.
     * 
     * @return true if there was a session and it is Closed Now or would we closed ASAP.
     */
    public boolean closeSessionOnUnrecoverableError() {
        return false;
    }

    @Override
    public void onUnrecoverableError(Throwable e, String errorCode) {
        if (unrecoverableErrorDialogShown) {
            return;
        }

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

        boolean sessionClosed = closeSessionOnUnrecoverableError();

        Dialog d = new Dialog("An Unexpected Error Has Occurred",

        "Please report the incident to technical support,\n"

        + "describing the steps taken prior to the error.\n"

        + ((sessionClosed) ? "\nThis session has been terminated to prevent data corruption." : "")

        + ((detailsMessage != null) ? detailsMessage : ""), Type.Error, new OkOption() {
            @Override
            public boolean onClickOk() {
                unrecoverableErrorDialogShown = false;
                return true;
            }
        });

        unrecoverableErrorDialogShown = true;
        d.show();
    }
}
