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
package com.propertyvista.crm.client.ui.crud.tenant;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    private final VistaDecoratorsFlowPanel person = new VistaDecoratorsFlowPanel();

    private final VistaDecoratorsFlowPanel company = new VistaDecoratorsFlowPanel();

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public TenantEditorForm(IView<TenantDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public TenantEditorForm(IEditableComponentFactory factory, IView<TenantDTO> parentView) {
        super(TenantDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        //Person
        person.add(inject(proto().person().name().namePrefix()), 7);
        person.add(inject(proto().person().name().firstName()), 15);
        person.add(inject(proto().person().name().middleName()), 15);
        person.add(inject(proto().person().name().lastName()), 15);
        person.add(inject(proto().person().name().maidenName()), 15);
        person.add(inject(proto().person().name().nameSuffix()), 7);
        person.add(inject(proto().person().birthDate()), 8.2);

        person.add(inject(proto().person().homePhone()), 7);
        person.add(inject(proto().person().mobilePhone()), 7);
        person.add(inject(proto().person().workPhone()), 7);
        person.add(inject(proto().person().email()), 20);

        //Company
        company.add(inject(proto().company().name()), 25);
        company.add(inject(proto().company().website()), 25);
        SubtypeInjectors.injectPhones(company, proto().company().phones(), this);
        SubtypeInjectors.injectEmails(company, proto().company().emails(), this);

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void populate(TenantDTO value) {
        super.populate(value);
        setVisibility(value);
    }

    private void setVisibility(TenantDTO tenant) {
        tabPanel.clear();
        switch (tenant.type().getValue()) {
        case person:
            tabPanel.add(person, i18n.tr("Details"));
            tabPanel.addDisable(((TenantView) getParentView()).getScreeningListerView().asWidget(), i18n.tr("Screening"));
            break;
        case company:
            tabPanel.add(company, i18n.tr("Company"));
            break;
        }

        tabPanel.setDisableMode(isEditable());
    }
}