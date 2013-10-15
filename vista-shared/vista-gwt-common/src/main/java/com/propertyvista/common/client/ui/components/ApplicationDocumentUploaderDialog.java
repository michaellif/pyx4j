/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;

public abstract class ApplicationDocumentUploaderDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static final I18n i18n = I18n.get(ApplicationDocumentUploaderDialog.class);

    private final UploadPanel<IEntity, IFile> uploadPanel;

    private final Dialog dialog;

    @SuppressWarnings("unchecked")
    public ApplicationDocumentUploaderDialog(String title) {
        dialog = new Dialog(title, this, null);

        uploadPanel = new UploadPanel<IEntity, IFile>((UploadService<IEntity, IFile>) GWT.create(ApplicationDocumentUploadService.class)) {

            @Override
            protected void onUploadSubmit() {
                dialog.getOkButton().setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                dialog.getOkButton().setEnabled(true);
                uploadPanel.reset();
            }

            @Override
            protected void onUploadComplete(UploadResponse<IFile> serverUploadResponse) {
                dialog.hide();
                ApplicationDocumentUploaderDialog.this.onUploadComplete(serverUploadResponse);
            }

            @Override
            protected IEntity getUploadData() {
                return null;
            }
        };
        uploadPanel.setSupportedExtensions(ApplicationDocumentUploadService.supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("460px", "100%");

        VerticalPanel dialogBody = new VerticalPanel();
        dialogBody.setSize("460px", "100%");
        dialogBody.add(uploadPanel);
        dialogBody.setCellHorizontalAlignment(uploadPanel, HasHorizontalAlignment.ALIGN_CENTER);

        dialog.setBody(dialogBody);
        dialog.setPixelSize(460, 80);
    }

    public void show() {
        dialog.show();
    }

    protected abstract void onUploadComplete(UploadResponse<IFile> serverUploadResponse);

    @Override
    public boolean onClickOk() {
        uploadPanel.uploadSubmit();
        return false;

    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Upload");
    }

    @Override
    public boolean onClickCancel() {
        uploadPanel.uploadCancel();
        return true;
    }

}
