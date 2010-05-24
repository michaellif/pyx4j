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
 * Created on May 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.order;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

public abstract class EditCaptionDialog extends VerticalPanel implements OkCancelOption {

    private final Dialog dialog;

    private final TextBox captionTextBox;

    public EditCaptionDialog(String caption) {

        Label labelDescription = new Label("Description:", false);
        captionTextBox = new TextBox();
        captionTextBox.setText(caption);
        HorizontalPanel line = new HorizontalPanel();
        line.add(labelDescription);
        line.add(captionTextBox);
        this.add(line);

        dialog = new Dialog("Edit Caption", this);

        dialog.setBody(this);
        dialog.setPixelSize(460, 200);
    }

    public void show() {
        dialog.show();
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    public String getCaption() {
        return captionTextBox.getText();
    }

}
