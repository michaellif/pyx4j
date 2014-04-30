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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.PmcBusinessInformationDocumentFolder;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.dto.vista2pmc.BusinessInformationDTO;

// TODO add document requirements label and validator
public class BusinessInformationForm extends CForm<BusinessInformationDTO> {

    private static final I18n i18n = I18n.get(BusinessInformationForm.class);

    public BusinessInformationForm() {
        super(BusinessInformationDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().companyName()).decorate();
        formPanel.append(Location.Left, proto().companyType()).decorate();

        formPanel.append(Location.Right, proto().businessNumber()).decorate();
        formPanel.append(Location.Right, proto().businessEstablishedDate()).decorate();

        formPanel.append(Location.Full, proto().dto_businessAddress(), new AddressSimpleEditor());

        Label documentsLabel = new Label();
        documentsLabel.setText(isEditable() ? i18n.tr("Attach Documentation:") : i18n.tr("Attached Documentation:"));
        documentsLabel.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        FlowPanel documentsDecorator = new FlowPanel();
        documentsDecorator.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecorator.name());
        if (!isEditable()) {
            documentsDecorator.addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.viewable.name());
        }
        documentsDecorator.add(documentsLabel);
        formPanel.append(Location.Full, documentsDecorator);

        if (isEditable()) {
            Label equifaxReuirements = new Label();
            equifaxReuirements.setText(i18n.tr("Equifax requires proof of two (2) of the following documents:"));
            formPanel.append(Location.Left, equifaxReuirements);
        }

        formPanel.append(Location.Full, proto().documents(), new PmcBusinessInformationDocumentFolder());

        return formPanel;
    }
}
