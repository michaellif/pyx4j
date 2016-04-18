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
 */
package com.pyx4j.gwt.client.deferred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.commons.ui.ScrollPanel;
import com.pyx4j.gwt.commons.ui.VerticalPanel;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.CloseOption;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class DeferredProcessDialog extends MessageDialog implements CloseOption, CancelOption, DeferredProgressListener {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessDialog.class);

    protected long deferredProcessStartTime;

    VerticalPanel messagePanel = new VerticalPanel();

    private final DeferredProgressPanel deferredProgressPanel;

    public DeferredProcessDialog(String title, String initialMessage, boolean executeByUserRequests) {
        super(title, initialMessage, Type.Info, null);
        setDialogOptions(this);

        setBody(messagePanel = new VerticalPanel());
        messagePanel.setWidth("100%");

        deferredProgressPanel = new DeferredProgressPanel(initialMessage, executeByUserRequests, this);
        deferredProgressPanel.getStyle().setProperty("border", "10px solid transparent"); // set content margin
        deferredProgressPanel.setSize("100%", "100%");
        deferredProgressPanel.setVisible(false);
        messagePanel.add(deferredProgressPanel);

        getCloseButton().setVisible(false);
    }

    public void setSuccessMessage(String message) {
        deferredProgressPanel.setSuccessMessage(message);
    }

    public void setFailureMessage(String message) {
        deferredProgressPanel.setFailureMessage(message);
    }

    public void hide() {
        hide(false);
    }

    @Override
    public boolean onClickClose() {
        return true;
    }

    @Override
    public boolean onClickCancel() {
        cancelProgress();
        return true;
    }

    public void startProgress(final String deferredCorrelationId) {
        deferredProgressPanel.startProgress(deferredCorrelationId);
        deferredProgressPanel.setVisible(true);
    }

    public void cancelProgress() {
        deferredProgressPanel.cancelProgress();
    }

    @Override
    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
        onDeferredCompleate();
        setStatusMessage(result.getMessage(), Type.Info);
    }

    @Override
    public void onDeferredError(DeferredProcessProgressResponse result) {
        onDeferredCompleate();
        setStatusMessage(result.getMessage(), Type.Error);
    }

    @Override
    public void onDeferredProgress(DeferredProcessProgressResponse result) {
        // Do nothing the progress is shown in panel
    }

    protected void onDeferredCompleate() {
        getCancelButton().setVisible(false);
        getCloseButton().setVisible(true);
        log.info("Deferred " + getTitle() + " completed in " + TimeUtils.secSince(deferredProcessStartTime));
    }

    private void setStatusMessage(String message, MessageDialog.Type type) {
        if (CommonsStringUtils.isEmpty(message)) {
            return;
        }
        ScrollPanel scrollPanel = new ScrollPanel(new MessagePanel(message, type));
        scrollPanel.getStyle().setProperty("maxHeight", "400px");
        messagePanel.add(scrollPanel);
        layout();
    }
}
