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
package com.propertyvista.crm.client.ui.components.media;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.crm.rpc.dto.MediaUploadDTO;
import com.propertyvista.crm.rpc.services.MediaUploadService;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public abstract class MediaUploadDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static I18n i18n = I18nFactory.getI18n(MediaUploadDialog.class);

    private final UploadPanel<MediaUploadDTO> uploadPanel;

    private final Dialog dialog;

    @SuppressWarnings("unchecked")
    public MediaUploadDialog() {
        dialog = new Dialog(i18n.tr("Upload Image file"), this);

        uploadPanel = new UploadPanel<MediaUploadDTO>((UploadService<MediaUploadDTO>) GWT.create(MediaUploadService.class)) {

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
                MediaUploadDialog.this.onUploadComplete(serverUploadResponse);
            }

            @Override
            protected MediaUploadDTO getUploadData() {
                MediaUploadDTO dto = EntityFactory.create(MediaUploadDTO.class);
                dto.target().setValue(getImageTarget());
                return dto;
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

    protected abstract ImageTarget getImageTarget();

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
