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
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationDocumentUploadDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;

public abstract class ApplicationDocumentUploaderDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static I18n i18n = I18n.get(ApplicationDocumentUploaderDialog.class);

    private final UploadPanel<ApplicationDocumentUploadDTO> uploadPanel;

    private final Dialog dialog;

    @SuppressWarnings("unchecked")
    public ApplicationDocumentUploaderDialog(String title, final Key tenantId) {
        dialog = new Dialog(title, this);

        uploadPanel = new UploadPanel<ApplicationDocumentUploadDTO>(
                (UploadService<ApplicationDocumentUploadDTO>) GWT.create(ApplicationDocumentUploadService.class)) {

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
                ApplicationDocumentUploaderDialog.this.onUploadComplete(serverUploadResponse);
            }

            @Override
            protected ApplicationDocumentUploadDTO getUploadData() {
                ApplicationDocumentUploadDTO dto = EntityFactory.create(ApplicationDocumentUploadDTO.class);
                dto.tenantId().setValue(tenantId);
                return dto;
            }

        };
        uploadPanel.setSupportedExtensions(ApplicationDocumentUploadService.supportedFormats);
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
