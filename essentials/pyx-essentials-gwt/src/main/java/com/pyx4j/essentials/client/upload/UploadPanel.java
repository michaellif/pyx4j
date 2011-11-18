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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.DeferredProgressPanel;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadId;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.IServiceBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class UploadPanel<E extends IEntity> extends SimplePanel implements FormPanel.SubmitCompleteHandler, FormPanel.SubmitHandler {

    private final static Logger log = LoggerFactory.getLogger(UploadPanel.class);

    private static I18n i18n = I18n.get(UploadPanel.class);

    private final UploadService<E> service;

    private final FormPanel uploadForm;

    private final FileUpload upload;

    private final Set<String> supportedExtensions = new HashSet<String>();

    private Hidden postCorrelationId;

    private Hidden postUploadKey;

    private Hidden postDescription;

    private UploadId uploadId;

    private final DeferredProgressPanel deferredProgressPanel;

    public static enum UploadError {

        NoFileSelected,

        UnSupportedExtension,

        ServerMessage;

    }

    public UploadPanel(UploadService<E> service) {
        this.service = service;
        uploadForm = new FormPanel();
        uploadForm.setAction("upload/" + ((IServiceBase) service).getServiceClassId());
        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);
        uploadForm.addSubmitCompleteHandler(this);
        uploadForm.addSubmitHandler(this);

        this.setWidget(uploadForm);
        FlowPanel content = new FlowPanel();
        uploadForm.setWidget(content);

        content.add(postCorrelationId = new Hidden(UploadService.PostCorrelationID));
        content.add(postUploadKey = new Hidden(UploadService.PostUploadKey));
        content.add(postDescription = new Hidden(UploadService.PostUploadDescription));

        upload = new FileUpload();
        upload.setName("upload");

        HorizontalPanel line = new HorizontalPanel();
        content.add(line);

        line.add(upload);
        line.add(deferredProgressPanel = new DeferredProgressPanel("70px", "20px") {

            @Override
            protected void onDeferredSuccess(DeferredProcessProgressResponse result) {
                UploadPanel.this.onDeferredSuccess(result);
            }

            @Override
            protected void onDeferredError(DeferredProcessProgressResponse result) {
                UploadPanel.this.onDeferredError(result);
            }

        });
        deferredProgressPanel.getElement().getStyle().setPaddingLeft(25, Style.Unit.PX);
        deferredProgressPanel.setVisible(false);
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
        supportedExtensions.addAll(DownloadFormat.getExtensions(formats));
    }

    public void setDescription(String description) {
        postDescription.setValue(description);
    }

    protected E getUploadData() {
        return null;
    }

    public final void uploadSubmit() {
        if (isFileValid()) {
            service.prepareUpload(new DefaultAsyncCallback<UploadId>() {
                @Override
                public void onSuccess(UploadId result) {
                    uploadId = result;
                    postCorrelationId.setValue(uploadId.getDeferredCorrelationId());
                    if (uploadId.getUploadKey() != null) {
                        postUploadKey.setValue(uploadId.getUploadKey().toString());
                    }
                    uploadForm.submit();
                }
            }, getUploadData());
        }
    }

    protected void onUploadSubmit() {

    }

    public final void uploadCancel() {
        deferredProgressPanel.cancelProgress();
        onUploadCancel();
        reset();
    }

    protected void onUploadCancel() {

    }

    protected void onUploadError(UploadError error, String args) {
        String message = "n/a";
        switch (error) {
        case NoFileSelected:
            message = i18n.tr("The File Name Cannot Be Empty");
            break;
        case UnSupportedExtension:
            message = i18n.tr("Unsupported Extension ''{0}''", args);
            break;
        case ServerMessage:
            message = args;
            break;
        }
        MessageDialog.error(i18n.tr("Upload Error"), message);
    }

    protected boolean isFileValid() {
        String name = upload.getFilename();
        if (!CommonsStringUtils.isStringSet(name)) {
            onUploadError(UploadError.NoFileSelected, null);
            return false;
        }
        if (!supportedExtensions.isEmpty()) {
            int extIdx = name.lastIndexOf('.');
            String ext = "";
            if ((extIdx > 0) && (extIdx < name.length() - 2)) {
                ext = name.substring(extIdx + 1).toLowerCase();
            }
            if (!supportedExtensions.contains(ext)) {
                onUploadError(UploadError.UnSupportedExtension, ext);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSubmit(SubmitEvent event) {
        if (isFileValid()) {
            onUploadSubmit();
            deferredProgressPanel.setVisible(true);
            deferredProgressPanel.startProgress(uploadId.getDeferredCorrelationId());
        } else {
            event.cancel();
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
            if (message.startsWith(UploadService.ResponseOk)) {
                onSubmitUploadComplete();
                reset();
            } else if (message.startsWith(UploadService.ResponseProcessWillContinue)) {
                //Continue using deferredProgressPanel
            } else {
                log.error("Upload server message [{}]", message);
                onUploadError(UploadError.ServerMessage, message);
            }
        } else {
            log.error("Upload server message [{}]", message);
            onUploadError(UploadError.ServerMessage, "Error uploading file");
        }

    }

    public void reset() {
        deferredProgressPanel.reset();
        uploadForm.reset();
        uploadId = null;
    }

    private void onDeferredSuccess(DeferredProcessProgressResponse result) {
        onSubmitUploadComplete();
        reset();
    }

    private void onDeferredError(DeferredProcessProgressResponse result) {
        onUploadError(UploadError.ServerMessage, result.getMessage());
    }

    private final void onSubmitUploadComplete() {
        if (uploadId != null) {
            service.getUploadResponse(new DefaultAsyncCallback<UploadResponse>() {
                @Override
                public void onSuccess(UploadResponse result) {
                    onUploadComplete(result);
                }
            }, uploadId);
        }
    }

    protected void onUploadComplete(UploadResponse serverUploadResponse) {
    }

}
