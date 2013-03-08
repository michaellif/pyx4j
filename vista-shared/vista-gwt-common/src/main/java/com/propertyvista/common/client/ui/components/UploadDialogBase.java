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

import java.util.Collection;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public class UploadDialogBase<U extends IEntity, R extends IEntity> extends OkCancelDialog {

    private static final I18n i18n = I18n.get(UploadDialogBase.class);

    private UploadPanel<U, R> uploadPanel;

    public UploadDialogBase(String caption, UploadService<U, R> uploadService, String uploadServletPath, Collection<DownloadFormat> supportedFormats) {
        super(caption);

        uploadPanel = new UploadPanel<U, R>(uploadService) {

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
            protected void onUploadComplete(UploadResponse<R> serverUploadResponse) {
                UploadDialogBase.this.hide();
                UploadDialogBase.this.onUploadComplete(serverUploadResponse);
            }

            @Override
            protected U getUploadData() {
                return null;
            }
        };
        uploadPanel.setSupportedExtensions(supportedFormats);
        uploadPanel.setServletPath(uploadServletPath);
        uploadPanel.setSize("400px", "100%");

        setBody(uploadPanel);

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

    protected void onUploadComplete(UploadResponse<R> serverUploadResponse) {

    }

}
