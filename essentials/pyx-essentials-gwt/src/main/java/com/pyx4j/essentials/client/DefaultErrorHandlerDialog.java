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
 */
package com.pyx4j.essentials.client;

import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;

public class DefaultErrorHandlerDialog extends UnrecoverableErrorHandlerDialog {

    private final boolean includeErrorCodeInUserMessage;

    public static void register() {
        register(true);
    }

    public static void register(boolean includeErrorCodeInUserMessage) {
        UncaughtHandler.setUnrecoverableErrorHandler(new DefaultErrorHandlerDialog(includeErrorCodeInUserMessage));
    }

    protected DefaultErrorHandlerDialog(boolean includeErrorCodeInUserMessage) {
        super();
        this.includeErrorCodeInUserMessage = includeErrorCodeInUserMessage;
    }

    @Override
    protected boolean includeErrorCodeInUserMessage() {
        return includeErrorCodeInUserMessage && super.includeErrorCodeInUserMessage();
    }

}
