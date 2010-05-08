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
 * Created on 2010-05-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.CloseOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class DeferredProcessDialog extends SimplePanel implements CloseOption, CancelOption {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessDialog.class);

    protected String deferredCorrelationID;

    protected long deferredProcessStartTime;

    protected final Dialog dialog;

    private boolean canceled = false;

    VerticalPanel messagePenel = new VerticalPanel();

    protected final HTML message1;

    protected final HTML message2;

    public DeferredProcessDialog(String title, String initialMessage) {
        this.setWidget(messagePenel = new VerticalPanel());
        messagePenel.add(message1 = new HTML(initialMessage));
        messagePenel.add(message2 = new HTML(""));
        dialog = new Dialog(title, this);
        dialog.setBody(this);
        dialog.getCloseButton().setVisible(false);
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    @Override
    public boolean onClickClose() {
        return true;
    }

    @Override
    public boolean onClickCancel() {
        canceled = true;
        cancelProgress();
        return true;
    }

    public void setDeferredCorrelationID(final String deferredCorrelationID) {
        this.deferredCorrelationID = deferredCorrelationID;
        if (canceled) {
            cancelProgress();
        } else {
            deferredProcessStartTime = System.currentTimeMillis();
            executeProcess();
        }
    }

    public void cancelProgress() {
        if (deferredCorrelationID != null) {
            RPCManager.executeBackground(DeferredProcessServices.Cancel.class, deferredCorrelationID, null);
            deferredCorrelationID = null;
        }
    }

    protected void onDeferredSuccess(DeferredProcessProgressResponse result) {
        message1.setHTML("Compleated");
        onDeferredCompleate();
    }

    protected void onDeferredError(DeferredProcessProgressResponse result) {
        onDeferredCompleate();
        hide();
        MessageDialog.error(dialog.getTitle(), result.getMessage());
    }

    protected void onDeferredCompleate() {
        dialog.getCancelButton().setVisible(false);
        dialog.getCloseButton().setVisible(true);
        log.info("Deferred " + dialog.getTitle() + " completed in " + TimeUtils.secSince(deferredProcessStartTime));
    }

    public void executeProcess() {
        AsyncCallback<DeferredProcessProgressResponse> progressHandlingCallback = new AsyncCallback<DeferredProcessProgressResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                hide();
                if (!canceled) {
                    throw new UnrecoverableClientError(caught);
                }
            }

            @Override
            public void onSuccess(DeferredProcessProgressResponse result) {
                if (result.isError()) {
                    onDeferredError(result);
                } else if (result.isCompleted()) {
                    onDeferredSuccess(result);
                } else {
                    message2.setHTML("Progress: " + result.getProgress());
                    executeProcess();
                }
            }

        };
        RPCManager.executeBackground(DeferredProcessServices.ContinueExecution.class, deferredCorrelationID, progressHandlingCallback);
    }
}
