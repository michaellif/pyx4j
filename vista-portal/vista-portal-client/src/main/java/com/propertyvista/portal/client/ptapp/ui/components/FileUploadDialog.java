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

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;
import gwtupload.client.SingleUploader;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

public abstract class FileUploadDialog extends VerticalPanel implements OkCancelOption, OkOptionText, FormPanel.SubmitCompleteHandler, FormPanel.SubmitHandler {

    private static I18n i18n = I18nFactory.getI18n(FileUploadDialog.class);

    private final static Logger log = LoggerFactory.getLogger(FileUploadDialog.class);

    private final Dialog dialog;

    private final FormPanel form;

    //private final FileUpload upload;

    private final Set<String> supportedFormats;

    private final TextBox description;

    //private final FlowPanel panelImages = new FlowPanel();

    private final HorizontalPanel uploadLine;

    public FileUploadDialog(/* Order order */) {
        log.debug("new FileUploadDialog()");
        form = new FormPanel();
        form.setAction("uploadorderphoto");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.addSubmitCompleteHandler(this);
        form.addSubmitHandler(this);

        form.setWidget(this);

        {
            Label descriptionLabel = new Label(i18n.tr("Description:"), false);
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
            Label uploadLabel = new Label(i18n.tr("File:"), false);
            uploadLabel.setWidth("150px");
            uploadLabel.getElement().getStyle().setPaddingRight(15, Unit.PX);
            //upload = new FileUpload();
            //upload.setName(i18n.tr("upload"));
            SingleUploader singleUploader = new SingleUploader(FileInputType.BUTTON, new BaseUploadStatus());
            singleUploader.setAutoSubmit(true);
            singleUploader.setValidExtensions(new String[] { "jpg", "jpeg", "gif", "png", "tiff", "bmp", "pdf" });
            singleUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
            singleUploader.getFileInput().setText(i18n.tr("Upload File"));
            singleUploader.getFileInput().getWidget().setStyleName("customButton");
            singleUploader.getFileInput().getWidget().setSize("159px", "27px");
            singleUploader.avoidRepeatFiles(true);

            HorizontalPanel line = new HorizontalPanel();
            line.add(uploadLabel);
            line.setCellHorizontalAlignment(uploadLabel, HasHorizontalAlignment.ALIGN_RIGHT);
            line.add(singleUploader);
            line.getElement().getStyle().setPaddingTop(15, Unit.PX);
            this.add(uploadLine = line);

        }

        //        this.add(new Hidden(ServletParams.ORDER_IMAGE_ORDER_ID, String.valueOf(order.getPrimaryKey())));

        Label remarks = new Label(i18n.tr("Maximum upload size is 1 megabyte"), false);
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
        supportedFormats.add("PDF");

        dialog = new Dialog(i18n.tr("Upload file"), this);

        form.setSize("400px", "100px");
        dialog.setBody(form);

        dialog.setPixelSize(460, 150);
    }

    private final IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {

        @Override
        public void onFinish(IUploader uploader) {
            log.debug("onFinishUploaderHandler.onFinish(): uploader=" + uploader);
            if (uploader.getStatus() == Status.SUCCESS) {
                log.debug("onFinishUploaderHandler.onFinish(): SUCCESS");
                new PreloadedImage(uploader.fileUrl(), showImage);
            }
        }
    };

    OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() {

        @Override
        public void onLoad(PreloadedImage img) {
            log.debug("showImage.onLoad(): img=" + img);
            img.setWidth("75px");
            uploadLine.clear();
            uploadLine.add(img);
        }
    };

    public void show() {
        dialog.show();
    }

    public abstract void onComplete();

    @Override
    public String optionTextOk() {
        return i18n.tr("Upload");
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public boolean onClickOk() {
        //form.submit();
        return false;
    }

    @Override
    public void onSubmit(SubmitEvent event) {
        log.debug("FileUploadDialog.onSubmit(): event=" + event);
        /*
         * String name = upload.getFilename(); if (!CommonsStringUtils.isStringSet(name))
         * { MessageDialog.error(i18n.tr("Upload error"),
         * i18n.tr("The file name must not be empty")); event.cancel(); return; } int
         * extIdx = name.lastIndexOf('.'); String ext = ""; if ((extIdx > 0) && (extIdx <
         * name.length() - 2)) { ext = name.substring(extIdx + 1).toUpperCase(); } if
         * (!supportedFormats.contains(ext)) {
         * MessageDialog.error(i18n.tr("Upload error"),
         * i18n.tr("Only JPEG, PNG, GIF, BMP, TIFF and PDF formats are supported") +
         * "\n<br/>[" + ext + "]" + i18n.tr(" not supported")); event.cancel(); return; }
         */
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
                log.error(i18n.tr("Upload server message") + " [{}]", message);
                MessageDialog.error(i18n.tr("Upload error"), message);
            }
        } else {
            log.error("Upload server message [{}]", message);
            MessageDialog.error(i18n.tr("Upload error"), i18n.tr("Error uploading file"));
        }

    }
}
