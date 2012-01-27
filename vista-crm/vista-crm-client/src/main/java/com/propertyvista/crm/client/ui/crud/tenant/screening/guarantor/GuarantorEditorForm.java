/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.person.Name;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorEditorForm extends CrmEntityForm<GuarantorDTO> {

    private static final I18n i18n = I18n.get(GuarantorEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public GuarantorEditorForm() {
        this(false);
    }

    public GuarantorEditorForm(boolean viewMode) {
        super(GuarantorDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.addDisable(isEditable() ? new HTML() : ((GuarantorViewerView) getParentView()).getScreeningListerView().asWidget(), i18n.tr("Screening"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().middleName()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 25).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().maidenName()), 25).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().nameSuffix()), 5).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Guarantor"))
                    .build());
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().birthDate()), 9).build());

        main.setBR(++row, 0, 1);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 25).build());

        return new CrmScrollPanel(main);
    }

}