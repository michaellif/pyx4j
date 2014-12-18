/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 10, 2014
 * @author stanp
 */
package com.pyx4j.forms.client.ui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AsyncLoadingHandler implements AsyncCallback<List<?>> {

    private static final Logger log = LoggerFactory.getLogger(AsyncLoadingHandler.class);

    public enum Status {
        LoadNotRequested, Loading, Complete, Cancelled, Failed
    }

    private Status status;

    private final AsyncCallback<List<?>> handlingCallback;

    public AsyncLoadingHandler(AsyncCallback<List<?>> callback) {
        handlingCallback = callback;
        status = Status.LoadNotRequested;
    }

    public AsyncCallback<List<?>> getHandlingCallback() {
        return handlingCallback;
    }

    public boolean isStatus(Status status) {
        return this.status == status;
    }

    public void cancel() {
        status = Status.Cancelled;
    }

    @Override
    public void onFailure(Throwable caught) {
        if (isStatus(Status.Cancelled)) {
            return;
        }
        status = Status.Failed;
        log.error("Loading failed: {}", caught);
        handlingCallback.onFailure(caught);
    }

    @Override
    public void onSuccess(List<?> result) {
        if (isStatus(Status.Cancelled)) {
            return;
        }
        status = Status.Complete;
        handlingCallback.onSuccess(result);
    }

}
