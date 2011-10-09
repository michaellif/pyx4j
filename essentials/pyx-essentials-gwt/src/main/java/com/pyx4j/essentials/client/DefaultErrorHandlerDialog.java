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
 * Created on 2010-09-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;


import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;

public class DefaultErrorHandlerDialog extends UnrecoverableErrorHandlerDialog {

    private static I18n i18n = I18n.get(DefaultErrorHandlerDialog.class);

    public static void register() {
        UncaughtHandler.setUnrecoverableErrorHandler(new DefaultErrorHandlerDialog());
    }

    @Override
    protected void showDefaultError(Throwable caught, String errorCode) {
        if (caught instanceof UserRuntimeException) {
            MessageDialog.show(i18n.tr("Error"), caught.getMessage(), Type.Error, new ShowOnceDialogOptions());
        } else {
            super.showDefaultError(caught, errorCode);
        }
    }
}
