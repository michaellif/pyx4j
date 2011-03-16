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
package com.propertyvista.portal.client.ptapp.ui.components;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

public abstract class FileUploadDialog extends VerticalPanel implements OkCancelOption, OkOptionText, FormPanel.SubmitCompleteHandler, FormPanel.SubmitHandler {

    private final static Logger log = LoggerFactory.getLogger(FileUploadDialog.class);

    private final Dialog dialog;

    private final FormPanel form;

    private final FileUpload upload;

    private final Set<String> supportedFormats;

    private final TextBox description;

    public FileUploadDialog(/* Order order */) {
        form = new FormPanel();
        form.setAction("uploadorderphoto");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.addSubmitCompleteHandler(this);
        form.addSubmitHandler(this);

        form.setWidget(this);

        {
            Label descriptionLabel = new Label("Description:", false);
            descriptionLabel.setWidth("150px");
            descriptionLabel.getElement().getStyle().setPaddingRight(15, Unit.PX);
            description = new TextBox();
//            description.setName(ServletParams.ORDER_IMAGE_DESCRIPTION);
            HorizontalPanel line = new HorizontalPanel();
            line.add(descriptionLabel);
            line.setCellHorizontalAlignment(descriptionLabel, HasHorizontalAlignment.ALIGN_RIGHT);
            line.add(description);
            line.getElement().getStyle().setPaddingTop(15, Unit.PX);
            this.add(line);
        }

        {
            Label uploadLabel = new Label("File:", false);
            uploadLabel.setWidth("150px");
            uploadLabel.getElement().getStyle().setPaddingRight(15, Unit.PX);
            upload = new FileUpload();
            upload.setName("upload");
            HorizontalPanel line = new HorizontalPanel();
            line.add(uploadLabel);
            line.setCellHorizontalAlignment(uploadLabel, HasHorizontalAlignment.ALIGN_RIGHT);
            line.add(upload);
            line.getElement().getStyle().setPaddingTop(15, Unit.PX);
            this.add(line);

        }

//        this.add(new Hidden(ServletParams.ORDER_IMAGE_ORDER_ID, String.valueOf(order.getPrimaryKey())));

        Label remarks = new Label("Maximum upload size is 1 megabyte", false);
        DOM.setStyleAttribute(remarks.getElement(), "fontStyle", "italic");
        this.add(remarks);

        this.setCellHorizontalAlignment(remarks, HasHorizontalAlignment.ALIGN_RIGHT);

        supportedFormats = new HashSet<String>();
        supportedFormats.add("PNG");
        supportedFormats.add("JPEG");
        supportedFormats.add("JPG");
        supportedFormats.add("GIF");
        supportedFormats.add("TIFF");
        supportedFormats.add("BMP");

        dialog = new Dialog("Upload file", this);

        form.setSize("400px", "100px");

        dialog.setBody(form);
        dialog.setPixelSize(460, 150);
    }

    public void show() {
        dialog.show();
    }

    public abstract void onComplete();

    @Override
    public String optionTextOk() {
        return "Upload";
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public boolean onClickOk() {
        form.submit();
        return false;
    }

    @Override
    public void onSubmit(SubmitEvent event) {
        String name = upload.getFilename();
        if (!CommonsStringUtils.isStringSet(name)) {
            MessageDialog.error("Upload error", "The file name must not be empty");
            event.cancel();
            return;
        }
        int extIdx = name.lastIndexOf('.');
        String ext = "";
        if ((extIdx > 0) && (extIdx < name.length() - 2)) {
            ext = name.substring(extIdx + 1).toUpperCase();
        }
        if (!supportedFormats.contains(ext)) {
            MessageDialog.error("Upload error", "Only JPEG, PNG, GIF, BMP and TIFF image formats are supported\n<br/>[" + ext + "] not supported");
            event.cancel();
            return;
        }
        GlassPanel.show();
    }

    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
        GlassPanel.hide();
        String message = event.getResults();
        if (message == null) {
            message = "";
        }
        int idx = 0;// = message.indexOf(ServletParams.UPLOAD_RESPONSE_PREFIX);
        if (idx >= 0) {
//            message = message.substring(idx + ServletParams.UPLOAD_RESPONSE_PREFIX.length(), message.length());
            if (message.startsWith("OK")) {
                onComplete();
                dialog.hide();
            } else {
                log.error("Upload server message [{}]", message);
                MessageDialog.error("Upload error", message);
            }
        } else {
            log.error("Upload server message [{}]", message);
            MessageDialog.error("Upload error", "Error uploading image");
        }

    }
}
