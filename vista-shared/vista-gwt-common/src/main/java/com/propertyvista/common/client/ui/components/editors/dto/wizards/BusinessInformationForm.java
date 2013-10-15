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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.PmcBusinessInformationDocumentFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.dto.vista2pmc.BusinessInformationDTO;

// TODO add document requirements label and validator
public class BusinessInformationForm extends CEntityDecoratableForm<BusinessInformationDTO> {

    private static final I18n i18n = I18n.get(BusinessInformationForm.class);

    private final UploadService<IEntity, IFile> service;

    private final Collection<DownloadFormat> supportedFormats;

    public BusinessInformationForm(UploadService<IEntity, IFile> service, Collection<DownloadFormat> supportedFormats) {
        super(BusinessInformationDTO.class);
        this.service = service;
        this.supportedFormats = supportedFormats;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();
        contentPanel.getFlexCellFormatter().setWidth(0, 0, "50%");
        contentPanel.getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);

        contentPanel.getFlexCellFormatter().setWidth(0, 1, "50%");
        contentPanel.getFlexCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        contentPanel.getFlexCellFormatter().getElement(0, 1).getStyle().setPaddingLeft(10, Unit.PX);

        TwoColumnFlexFormPanel mainPanel = new TwoColumnFlexFormPanel();
        int mrow = -1;
        mainPanel.setWidget(++mrow, 0, new FormDecoratorBuilder(inject(proto().companyName())).build());
        mainPanel.setWidget(++mrow, 0, new FormDecoratorBuilder(inject(proto().companyType())).build());
        mainPanel.setWidget(++mrow, 0, new HTML("&nbsp;"));
        mainPanel.setWidget(++mrow, 0, inject(proto().dto_businessAddress(), new AddressSimpleEditor()));
        mainPanel.setWidget(++mrow, 0, new HTML("&nbsp;"));
        mainPanel.setWidget(++mrow, 0, new FormDecoratorBuilder(inject(proto().businessNumber())).build());
        mainPanel.setWidget(++mrow, 0, new FormDecoratorBuilder(inject(proto().businessEstablishedDate())).build());
        contentPanel.setWidget(0, 0, mainPanel);

        TwoColumnFlexFormPanel documentsPanel = new TwoColumnFlexFormPanel();
        int drow = -1;
        Label documentsLabel = new Label();
        documentsLabel.setText(isEditable() ? i18n.tr("Attach Documentation:") : i18n.tr("Attached Documentation:"));
        documentsLabel.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        FlowPanel documentsDecorator = new FlowPanel();
        documentsDecorator.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecorator.name());
        if (!isEditable()) {
            documentsDecorator.addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.viewable.name());
        }
        documentsDecorator.add(documentsLabel);
        documentsPanel.setWidget(++drow, 0, documentsDecorator);

        if (isEditable()) {
            Label equifaxReuirements = new Label();
            equifaxReuirements.setText(i18n.tr("Equifax requires proof of two (2) of the following documents:"));
            documentsPanel.setWidget(++drow, 0, equifaxReuirements);
        }

        documentsPanel.setWidget(++drow, 0, inject(proto().documents(), new PmcBusinessInformationDocumentFolder(service, supportedFormats)));
        contentPanel.setWidget(0, 1, documentsPanel);

        return contentPanel;
    }
}
