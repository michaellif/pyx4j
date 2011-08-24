/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.upload;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.rpc.client.IServiceBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class UploadPanel extends FlowPanel implements FormPanel.SubmitCompleteHandler, FormPanel.SubmitHandler {

    private final static Logger log = LoggerFactory.getLogger(UploadPanel.class);

    private static I18n i18n = I18nFactory.getI18n(UploadPanel.class);

    private final UploadService service;

    private final FormPanel uploadForm;

    private final FileUpload upload;

    private final Set<String> supportedExtensions = new HashSet<String>();

    private Hidden postCorrelationId;

    private Hidden postuploadKey;

    public static enum UploadError {

        NoFileSelected,

        UnSupportedExtension;

    }

    public UploadPanel(UploadService service) {
        this.service = service;
        uploadForm = new FormPanel();
        uploadForm.setAction("upload/" + ((IServiceBase) service).getServiceClassId());
        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);
        uploadForm.addSubmitCompleteHandler(this);
        uploadForm.addSubmitHandler(this);

        uploadForm.setWidget(this);

        upload = new FileUpload();
        upload.setName("upload");

        HorizontalPanel line = new HorizontalPanel();
        line.add(new Label("File:"));
        line.add(upload);
        this.add(line);

        Button buttonUpload = new Button("Upload");
        buttonUpload.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploadForm.submit();
            }
        });
        Button buttonCancel = new Button("Cancel");
        buttonCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                UploadPanel.this.setVisible(false);
            }
        });

        HorizontalPanel line2 = new HorizontalPanel();
        line2.add(buttonUpload);
        line2.add(buttonCancel);
        this.add(line2);
    }

    public IsWidget getForm() {
        return uploadForm;
    }

    public void setServletPath(String path) {
        if (path != null) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            uploadForm.setAction(path + ((IServiceBase) service).getServiceClassId());
        }
    }

    public void setSupportedExtensions(String... extensions) {
        for (String ext : extensions) {
            supportedExtensions.add(ext.toLowerCase());
        }
    }

    public void setSupportedExtensions(Collection<DownloadFormat> formats) {
        for (DownloadFormat f : formats) {
            for (String ext : f.getExtensions()) {
                supportedExtensions.add(ext.toLowerCase());
            }
        }
    }

    protected void onUploadError(UploadError error) {
        String message = "n/a";
        switch (error) {
        case NoFileSelected:
            message = i18n.tr("The file name must not be empty");
            break;
        case UnSupportedExtension:
            message = i18n.tr("Unsupported Extension");
            break;
        }
        MessageDialog.error(i18n.tr("Upload error"), message);
    }

    @Override
    public void onSubmit(SubmitEvent event) {
        String name = upload.getFilename();
        if (!CommonsStringUtils.isStringSet(name)) {
            onUploadError(UploadError.NoFileSelected);
            event.cancel();
            return;
        }
        if (!supportedExtensions.isEmpty()) {
            int extIdx = name.lastIndexOf('.');
            String ext = "";
            if ((extIdx > 0) && (extIdx < name.length() - 2)) {
                ext = name.substring(extIdx + 1).toLowerCase();
            }
            if (!supportedExtensions.contains(ext)) {
                onUploadError(UploadError.UnSupportedExtension);
                event.cancel();
                return;
            }
        }
    }

    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
        String message = event.getResults();
        if (message == null) {
            message = "";
        }
        int idx = message.indexOf(UploadService.ResponsePrefix);
        if (idx >= 0) {
            message = message.substring(idx + UploadService.ResponsePrefix.length(), message.length());
            if (message.startsWith("OK")) {
                String id = message.substring(2, message.indexOf('\n')).trim();
                onUploadComplete(id);
                UploadPanel.this.setVisible(false);
            } else {
                log.error("Upload server message [{}]", message);
                MessageDialog.error("Upload error", message);
            }
        } else {
            log.error("Upload server message [{}]", message);
            MessageDialog.error("Upload error", "Error uploading file");
        }

    }

    protected void onUploadComplete(String id) {

    }

}
