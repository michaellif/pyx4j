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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.UploadDialogBase;
import com.propertyvista.common.client.ui.components.UploadResponseDownloadableReciver;
import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.dto.ImportDataFormatType;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.ImportUploadService;

public class ImportUploadDialog extends UploadDialogBase<ImportUploadDTO> {

    private static final I18n i18n = I18n.get(ImportUploadDialog.class);

    private final PmcDTO pmc;

    private CEntityForm<ImportUploadDTO> form;

    public ImportUploadDialog(PmcDTO pmc) {
        super(i18n.tr("Upload Import"), GWT.<ImportUploadService> create(ImportUploadService.class));
        this.pmc = pmc;
        super.setUploadReciver(new UploadResponseDownloadableReciver(i18n.tr("Import Upload")));
    }

    @Override
    protected IsWidget createContent(final UploadPanel<ImportUploadDTO, AbstractIFileBlob> uploadPanel) {
        form = new CEntityForm<ImportUploadDTO>(ImportUploadDTO.class) {
            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, uploadPanel);
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().type())).componentWidth(10).build());
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().dataFormat())).componentWidth(10).build());
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().ignoreMissingMedia())).componentWidth(10).build());
                return main;
            }
        };
        ImportUploadDTO defaultSettings = EntityFactory.create(ImportUploadDTO.class);
        defaultSettings.type().setValue(ImportUploadDTO.ImportType.newData);
        defaultSettings.dataFormat().setValue(ImportDataFormatType.vista);

        form.initContent();
        form.populate(defaultSettings);

        return form.asWidget();
    }

    @Override
    protected ImportUploadDTO getUploadData() {
        ImportUploadDTO dto = form.getValue();
        dto.setPrimaryKey(pmc.getPrimaryKey());
        return dto;
    }

}
