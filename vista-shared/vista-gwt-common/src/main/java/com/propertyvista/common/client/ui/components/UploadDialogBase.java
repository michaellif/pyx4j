/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class UploadDialogBase<U extends IEntity> extends OkCancelDialog {

    private static final I18n i18n = I18n.get(UploadDialogBase.class);

    private UploadPanel<U, AbstractIFileBlob> uploadPanel;

    private UploadReceiver uploadReciver;

    public UploadDialogBase(String caption, UploadService<U, AbstractIFileBlob> uploadService) {
        super(caption);

        uploadPanel = new UploadPanel<U, AbstractIFileBlob>(uploadService, new UploadReceiver() {

            @Override
            public void onUploadComplete(IFile<?> result) {
                UploadDialogBase.this.hide(false);
                if (UploadDialogBase.this.uploadReciver != null) {
                    UploadDialogBase.this.uploadReciver.onUploadComplete(result);
                } else {
                    if (CommonsStringUtils.isStringSet(((DownloadableUploadResponseDTO) result).message().getStringView())) {
                        MessageDialog.info(i18n.tr("Upload Complete"), ((DownloadableUploadResponseDTO) result).message().getStringView());
                    }
                }
            }
        }) {

            @Override
            protected void onUploadSubmit() {
                UploadDialogBase.this.getOkButton().setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                UploadDialogBase.this.getOkButton().setEnabled(true);
                uploadPanel.reset();
            }

            @Override
            protected U getUploadData() {
                return UploadDialogBase.this.getUploadData();
            }

        };
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("400px", "100%");

        setBody(createContent(uploadPanel));

    }

    protected IsWidget createContent(UploadPanel<U, AbstractIFileBlob> uploadPanel) {
        return uploadPanel;
    }

    public void setUploadReciver(UploadReceiver uploadReciver) {
        this.uploadReciver = uploadReciver;
    }

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
        return i18n.tr("Upload");
    }

    protected U getUploadData() {
        return null;
    }

}
