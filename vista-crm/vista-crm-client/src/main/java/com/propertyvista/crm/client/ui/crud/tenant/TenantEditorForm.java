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

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.components.folders.PhoneFolder;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    private final VistaDecoratorsFlowPanel person = new VistaDecoratorsFlowPanel(!isEditable());

    private final VistaDecoratorsFlowPanel company = new VistaDecoratorsFlowPanel(!isEditable());

    private final VistaDecoratorsFlowPanel contacts = new VistaDecoratorsFlowPanel(!isEditable());

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public TenantEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public TenantEditorForm(IEditableComponentFactory factory) {
        super(TenantDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        //Person
        if (isEditable()) {
            person.add(inject(proto().person().name().namePrefix()), 6);
            person.add(inject(proto().person().name().firstName()), 15);
            person.add(inject(proto().person().name().middleName()), 15);
            person.add(inject(proto().person().name().lastName()), 15);
            person.add(inject(proto().person().name().maidenName()), 15);
            person.add(inject(proto().person().name().nameSuffix()), 6);
        } else {
            person.add(inject(proto().person().name(), new CEntityLabel()), 25, "Tenant");
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }
        person.add(inject(proto().person().birthDate()), 8.2);

        person.add(inject(proto().person().homePhone()), 15);
        person.add(inject(proto().person().mobilePhone()), 15);
        person.add(inject(proto().person().workPhone()), 15);
        person.add(inject(proto().person().email()), 25);

        //Company
        company.add(inject(proto().company().name()), 25);
        company.add(inject(proto().company().website()), 25);

        company.add(inject(proto().company().phones(), new PhoneFolder(isEditable())));
        company.add(inject(proto().company().emails(), new EmailFolder(isEditable())));

        contacts.add(inject(proto().emergencyContacts(), new EmergencyContactFolder()));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        if (index < tabPanel.getWidgetCount()) {
            tabPanel.selectTab(index);
        }
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    @Override
    public void populate(TenantDTO value) {
        super.populate(value);
        setVisibility(value);
    }

    @Override
    public void addValidations() {
        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<Map<String, Object>>>() {

            @Override
            public boolean isValid(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts());
            }

            @Override
            public String getValidationMessage(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return i18n.tr("Duplicate contacts specified");
            }
        });
    }

    private void setVisibility(TenantDTO tenant) {
        tabPanel.clear();
        switch (tenant.type().getValue()) {
        case person:
            tabPanel.add(new CrmScrollPanel(person), i18n.tr("Details"));
            tabPanel.addDisable(isEditable() ? new HTML() : ((TenantViewerView) getParentView()).getScreeningListerView().asWidget(), i18n.tr("Screening"));
            break;
        case company:
            tabPanel.add(new CrmScrollPanel(company), proto().company().getMeta().getCaption());
            break;
        }

        tabPanel.add(new ScrollPanel(contacts), proto().emergencyContacts().getMeta().getCaption());
        tabPanel.setDisableMode(isEditable());
    }
}