/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.notes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.rpc.dto.NoteAttachmentUploadDTO;
import com.propertyvista.crm.rpc.services.MediaUploadService;
import com.propertyvista.crm.rpc.services.NoteAttachmentUploadService;
import com.propertyvista.domain.File;
import com.propertyvista.portal.rpc.DeploymentConsts;

public abstract class NotesAttachmentUploadDialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(NotesAttachmentUploadDialog.class);

    private final UploadPanel<NoteAttachmentUploadDTO, File> uploadPanel;

    public NotesAttachmentUploadDialog() {
        super(i18n.tr("Upload Attachment File"));

        uploadPanel = new UploadPanel<NoteAttachmentUploadDTO, File>(GWT.<NoteAttachmentUploadService> create(NoteAttachmentUploadService.class)) {

            @Override
            protected void onUploadSubmit() {
                NotesAttachmentUploadDialog.this.getOkButton().setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                NotesAttachmentUploadDialog.this.getOkButton().setEnabled(true);
                uploadPanel.reset();
            }

            @Override
            protected void onUploadComplete(UploadResponse<File> serverUploadResponse) {
                NotesAttachmentUploadDialog.this.hide(false);
                NotesAttachmentUploadDialog.this.onUploadComplete(serverUploadResponse);
            }

        };
        uploadPanel.setSupportedExtensions(MediaUploadService.supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("400px", "60px");
        uploadPanel.getElement().getStyle().setMarginTop(50, Style.Unit.PX);
        uploadPanel.getElement().getStyle().setPaddingLeft(35, Style.Unit.PX);

        this.setBody(uploadPanel);
        this.setDialogPixelWidth(460);
    }

    protected abstract void onUploadComplete(UploadResponse<File> serverUploadResponse);

    @Override
    public boolean onClickOk() {
        uploadPanel.uploadSubmit();
        return false;
    }

    @Override
    public boolean onClickCancel() {
        uploadPanel.uploadCancel();
        return true;
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Upload Attachment");
    }

}
