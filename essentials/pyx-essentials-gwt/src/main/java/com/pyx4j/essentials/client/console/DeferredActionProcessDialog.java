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
 * Created on 2010-05-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.console;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.essentials.client.DeferredProcessDialog;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;

public class DeferredActionProcessDialog extends DeferredProcessDialog {

    public static DeferredActionProcessDialog start(String title, Class<? extends DeferredProcessServices.AbstractStartDeferredProcessService> serviceInterface) {
        final DeferredActionProcessDialog rd = new DeferredActionProcessDialog(title, "Executing request...");
        rd.show();

        AsyncCallback<String> callback = new BlockingAsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                rd.hide();
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(String deferredCorrelationID) {
                rd.setDeferredCorrelationID(deferredCorrelationID);
            }

        };

        RPCManager.execute(serviceInterface, null, callback);
        return rd;
    }

    public DeferredActionProcessDialog(String title, String initialMessage) {
        super(title, initialMessage);
    }

}
