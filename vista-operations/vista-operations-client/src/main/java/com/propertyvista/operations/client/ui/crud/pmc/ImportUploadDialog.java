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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
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

    private CForm<ImportUploadDTO> form;

    public ImportUploadDialog(PmcDTO pmc) {
        super(i18n.tr("Upload Import"), GWT.<ImportUploadService> create(ImportUploadService.class));
        this.pmc = pmc;
        super.setUploadReciver(new UploadResponseDownloadableReciver(i18n.tr("Import Upload")));
    }

    @Override
    protected IsWidget createContent(final UploadPanel<ImportUploadDTO, AbstractIFileBlob> uploadPanel) {
        form = new CForm<ImportUploadDTO>(ImportUploadDTO.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, uploadPanel);
                formPanel.append(Location.Left, proto().type()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().dataFormat()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ignoreMissingMedia()).decorate().componentWidth(120);

                return formPanel;
            }
        };
        ImportUploadDTO defaultSettings = EntityFactory.create(ImportUploadDTO.class);
        defaultSettings.type().setValue(ImportUploadDTO.ImportType.newData);
        defaultSettings.dataFormat().setValue(ImportDataFormatType.vista);

        form.init();
        form.populate(defaultSettings);

        return form.asWidget();
    }

    @Override
    protected ImportUploadDTO getUploadData() {
        ImportUploadDTO dto = form.getValue();
        dto.pmcId().setPrimaryKey(pmc.getPrimaryKey());
        return dto;
    }

}
