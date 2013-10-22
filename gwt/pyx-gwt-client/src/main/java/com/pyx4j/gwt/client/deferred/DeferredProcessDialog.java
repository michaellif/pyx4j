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
package com.pyx4j.gwt.client.deferred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.CloseOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class DeferredProcessDialog extends SimplePanel implements CloseOption, CancelOption, DeferredProgressListener {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessDialog.class);

    private static final I18n i18n = I18n.get(DeferredProcessDialog.class);

    protected long deferredProcessStartTime;

    protected final Dialog dialog;

    private boolean canceled = false;

    VerticalPanel messagePenel = new VerticalPanel();

    protected final HTML message1;

    protected final HTML message2;

    private final DeferredProgressPanel deferredProgressPanel;

    public DeferredProcessDialog(String title, String initialMessage, boolean executeByUserRequests) {
        this.setWidget(messagePenel = new VerticalPanel());
        messagePenel.add(message1 = new HTML(initialMessage));
        messagePenel.add(message2 = new HTML(""));

        deferredProgressPanel = new DeferredProgressPanel("100px", "40px", executeByUserRequests, this);
        deferredProgressPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        deferredProgressPanel.getElement().getStyle().setProperty("marginRight", "auto");
        deferredProgressPanel.setSize("100%", "100%");
        deferredProgressPanel.setVisible(false);
        messagePenel.add(deferredProgressPanel);

        dialog = new Dialog(title, this, this);
        dialog.getCloseButton().setVisible(false);
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide(false);
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

    public void startProgress(final String deferredCorrelationId) {
        deferredProgressPanel.startProgress(deferredCorrelationId);
        deferredProgressPanel.setVisible(true);
    }

    public void cancelProgress() {
        deferredProgressPanel.cancelProgress();
    }

    @Override
    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
        message1.setHTML(i18n.tr("Completed"));
        if (result.getMessage() != null) {
            message2.setHTML(result.getMessage().replace("\n", "<br/>"));
        } else {
            message2.setHTML("");
        }
        onDeferredCompleate();
    }

    @Override
    public void onDeferredError(DeferredProcessProgressResponse result) {
        onDeferredCompleate();
        hide();
        if (!canceled) {
            MessageDialog.error(dialog.getTitle(), result.getMessage());
        }
    }

    @Override
    public void onDeferredProgress(DeferredProcessProgressResponse result) {
        // Do nothing the progress is shown in panel
    }

    protected void onDeferredCompleate() {
        deferredProgressPanel.reset();
        deferredProgressPanel.setVisible(false);
        dialog.getCancelButton().setVisible(false);
        dialog.getCloseButton().setVisible(true);
        log.info("Deferred " + dialog.getTitle() + " completed in " + TimeUtils.secSince(deferredProcessStartTime));
    }
}
