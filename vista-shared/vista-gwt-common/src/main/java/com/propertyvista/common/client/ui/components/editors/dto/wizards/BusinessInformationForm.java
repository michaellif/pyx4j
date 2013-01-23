/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.dto.wizards;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.PmcBusinessInformationDocumentFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.dto.vista2pmc.BusinessInformationDTO;

public class BusinessInformationForm extends CEntityDecoratableForm<BusinessInformationDTO> {

    private static final I18n i18n = I18n.get(BusinessInformationForm.class);

    private final UploadService<IEntity, IEntity> service;

    private final Collection<DownloadFormat> supportedFormats;

    public BusinessInformationForm(UploadService<IEntity, IEntity> service, Collection<DownloadFormat> supportedFormats) {
        super(BusinessInformationDTO.class);
        this.service = service;
        this.supportedFormats = supportedFormats;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        contentPanel.getFlexCellFormatter().setWidth(0, 0, "50%");
        contentPanel.getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);

        contentPanel.getFlexCellFormatter().setWidth(0, 1, "50%");
        contentPanel.getFlexCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        contentPanel.getFlexCellFormatter().getElement(0, 1).getStyle().setPadding(10, Unit.PX);

        FormFlexPanel mainPanel = new FormFlexPanel();
        int mrow = -1;
        mainPanel.setWidget(++mrow, 0, new DecoratorBuilder(inject(proto().companyName())).build());
        mainPanel.setWidget(++mrow, 0, new DecoratorBuilder(inject(proto().companyType())).build());
        mainPanel.setWidget(++mrow, 0, new HTML("&nbsp;"));
        mainPanel.setWidget(++mrow, 0, inject(proto().dto_businessAddress(), new AddressSimpleEditor()));
        mainPanel.setWidget(++mrow, 0, new HTML("&nbsp;"));
        mainPanel.setWidget(++mrow, 0, new DecoratorBuilder(inject(proto().businessNumber())).build());
        mainPanel.setWidget(++mrow, 0, new DecoratorBuilder(inject(proto().businessEstablishedDate())).build());
        contentPanel.setWidget(0, 0, mainPanel);

        FormFlexPanel documentsPanel = new FormFlexPanel();
        int drow = -1;
        Label equifaxReuirements = new Label();
        equifaxReuirements.setText(i18n.tr("Equifax requires proof of two (2) of the following documents"));
        documentsPanel.setWidget(++drow, 0, equifaxReuirements);
        documentsPanel.setWidget(++drow, 0, inject(proto().documents(), new PmcBusinessInformationDocumentFolder(service, supportedFormats)));
        contentPanel.setWidget(0, 1, documentsPanel);

        return contentPanel;
    }
}
