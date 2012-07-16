/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.common.client.ui.components.DownloadLinkDialog;
import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;
import com.propertyvista.dto.ImportDataFormatType;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class ImportUploadDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static final I18n i18n = I18n.get(ImportUploadDialog.class);

    private final CEntityForm<ImportUploadDTO> form;

    private final UploadPanel<ImportUploadDTO, ImportUploadResponseDTO> uploadPanel;

    private final Dialog dialog;

    private String downloadServletPath;

    @SuppressWarnings("unchecked")
    public ImportUploadDialog(PmcDTO pmc) {
        dialog = new Dialog(i18n.tr("Upload Import"), this, null);

        uploadPanel = new UploadPanel<ImportUploadDTO, ImportUploadResponseDTO>(
                (UploadService<ImportUploadDTO, ImportUploadResponseDTO>) GWT.create(ImportUploadService.class)) {
            @Override
            protected ImportUploadDTO getUploadData() {
                return form.getValue();
            }

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
            protected void onUploadComplete(UploadResponse<ImportUploadResponseDTO> serverUploadResponse) {
                dialog.hide();
                if (serverUploadResponse.data.resultUrl().isNull()) {
                    if (serverUploadResponse.data.success().getValue()) {
                        MessageDialog.info(i18n.tr("Import Completeated"), serverUploadResponse.message);
                    } else {
                        MessageDialog.error(i18n.tr("Import Error"), serverUploadResponse.message);
                    }
                } else {
                    if (serverUploadResponse.data.success().getValue()) {
                        DownloadLinkDialog d = new DownloadLinkDialog(i18n.tr("Import Completeated"));
                        if (downloadServletPath != null) {
                            d.setDownloadServletPath(downloadServletPath);
                        }
                        d.show(serverUploadResponse.message, i18n.tr("Download processing results"), serverUploadResponse.data.resultUrl().getValue());
                    } else {
                        DownloadLinkDialog d = new DownloadLinkDialog(i18n.tr("Import Error"));
                        if (downloadServletPath != null) {
                            d.setDownloadServletPath(downloadServletPath);
                        }
                        d.show(serverUploadResponse.message, i18n.tr("Download messages"), serverUploadResponse.data.resultUrl().getValue());
                    }
                }
            }

        };
        uploadPanel.setSupportedExtensions(ImportUploadService.supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("400px", "60px");
        uploadPanel.getElement().getStyle().setMarginTop(50, Style.Unit.PX);
        uploadPanel.getElement().getStyle().setPaddingLeft(35, Style.Unit.PX);

        form = new CEntityForm<ImportUploadDTO>(ImportUploadDTO.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, uploadPanel);
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().type())).componentWidth(10).build());
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().dataFormat())).componentWidth(10).build());
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().ignoreMissingMedia())).componentWidth(10).build());
                return main;
            }
        };
        ImportUploadDTO defaultSettings = EntityFactory.create(ImportUploadDTO.class);
        defaultSettings.setPrimaryKey(pmc.getPrimaryKey());
        defaultSettings.type().setValue(ImportUploadDTO.ImportType.newData);
        defaultSettings.dataFormat().setValue(ImportDataFormatType.vista);

        form.initContent();
        form.populate(defaultSettings);

        dialog.setBody(form.asWidget());
        dialog.setPixelSize(460, 150);
    }

    public void show() {
        dialog.show();
    }

    public void setDownloadServletPath(String path) {
        downloadServletPath = path;
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

}
