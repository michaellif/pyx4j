/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.UploadDialogBase;
import com.propertyvista.common.client.ui.components.UploadResponseDownloadableReciver;
import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.dto.ImportDataFormatType;

public class UpdateUploadDialog extends UploadDialogBase<ImportUploadDTO, DownloadableUploadResponseDTO> {

    private static final I18n i18n = I18n.get(UpdateUploadDialog.class);

    private CEntityForm<ImportUploadDTO> form;

    public UpdateUploadDialog() {
        super(i18n.tr("Upload Update"), GWT.<UploadService<ImportUploadDTO, DownloadableUploadResponseDTO>> create(UpdateUploadService.class),
                UpdateUploadService.supportedFormats);
        super.setUploadReciver(new UploadResponseDownloadableReciver<DownloadableUploadResponseDTO>(i18n.tr("Update")));
    }

    @Override
    protected IsWidget createContent(final UploadPanel<ImportUploadDTO, DownloadableUploadResponseDTO> uploadPanel) {

        form = new CEntityForm<ImportUploadDTO>(ImportUploadDTO.class) {
            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, uploadPanel);
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().dataFormat())).componentWidth(10).build());
                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                get(proto().dataFormat()).setEditable(false);
            }
        };
        ImportUploadDTO defaultSettings = EntityFactory.create(ImportUploadDTO.class);
        defaultSettings.dataFormat().setValue(ImportDataFormatType.unitAvailability);

        form.initContent();
        form.populate(defaultSettings);

        return form.asWidget();
    }

    @Override
    protected ImportUploadDTO getUploadData() {
        return form.getValue();
    }

}
