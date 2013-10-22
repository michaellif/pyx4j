/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.domain.pmc.info.PmcDocumentFile;
import com.propertyvista.portal.rpc.DeploymentConsts;

public abstract class PmcDocumentFileUploaderDialog extends Composite implements OkCancelOption, OkOptionText {

    private static final I18n i18n = I18n.get(PmcDocumentFileUploaderDialog.class);

    private final UploadPanel<IEntity, PmcDocumentFile> uploadPanel;

    private final Dialog dialog;

    @SuppressWarnings("unchecked")
    public PmcDocumentFileUploaderDialog(UploadService<IEntity, PmcDocumentFile> uploadService, Collection<DownloadFormat> supportedFormats) {
        dialog = new Dialog(i18n.tr("Upload Document"), this, null);

        uploadPanel = new UploadPanel<IEntity, PmcDocumentFile>(uploadService) {
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
            protected void onUploadComplete(PmcDocumentFile serverUploadResponse) {
                dialog.hide(false);
                PmcDocumentFileUploaderDialog.this.onUploadComplete(serverUploadResponse);
            }

        };
        uploadPanel.setSupportedExtensions(supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("460px", "100%");

        VerticalPanel dialogBody = new VerticalPanel();
        dialogBody.setSize("460px", "100%");
        dialogBody.add(uploadPanel);
        dialogBody.setCellHorizontalAlignment(uploadPanel, HasHorizontalAlignment.ALIGN_CENTER);

        dialog.setBody(dialogBody);
        dialog.setDialogPixelWidth(460);
    }

    public void show() {
        dialog.show();
    }

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

    protected abstract void onUploadComplete(PmcDocumentFile serverUploadResponse);

}
