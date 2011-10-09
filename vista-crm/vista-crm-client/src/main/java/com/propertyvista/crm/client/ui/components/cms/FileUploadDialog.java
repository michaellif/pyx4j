/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.cms;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.crm.rpc.services.MediaUploadService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public abstract class FileUploadDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static I18n i18n = I18n.get(FileUploadDialog.class);

    private final UploadPanel<IEntity> uploadPanel;

    private final Dialog dialog;

    @SuppressWarnings("unchecked")
    public FileUploadDialog() {
        dialog = new Dialog(i18n.tr("Upload Image file"), this);

        uploadPanel = new UploadPanel<IEntity>((UploadService<IEntity>) GWT.create(MediaUploadService.class)) {

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
            protected void onUploadComplete(UploadResponse serverUploadResponse) {
                dialog.hide();
                FileUploadDialog.this.onUploadComplete(serverUploadResponse);
            }

        };
        uploadPanel.setSupportedExtensions(MediaUploadService.supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("400px", "60px");
        uploadPanel.getElement().getStyle().setMarginTop(50, Style.Unit.PX);
        uploadPanel.getElement().getStyle().setPaddingLeft(35, Style.Unit.PX);

        dialog.setBody(uploadPanel);
        dialog.setPixelSize(460, 150);
    }

    public void show() {
        dialog.show();
    }

    protected abstract void onUploadComplete(UploadResponse serverUploadResponse);

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
