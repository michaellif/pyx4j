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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.components.folders.PhoneFolder;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    private final FormFlexPanel person = new FormFlexPanel();

    private final FormFlexPanel company = new FormFlexPanel();

    private final FormFlexPanel contacts = new FormFlexPanel();

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public TenantEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public TenantEditorForm(IEditableComponentFactory factory) {
        super(TenantDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        int row = -1;

        //Person
        if (isEditable()) {
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().namePrefix()), 5).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().middleName()), 5).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 25).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().maidenName()), 25).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().nameSuffix()), 5).build());
        } else {
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name(), new CEntityLabel()), 25).customLabel(i18n.tr("Tenant")).build());
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().birthDate()), 8.2).build());

        person.setWidget(++row, 0, new HTML("&nbsp"));

        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 25).build());

        //Company
        company.setWidget(++row, 0, new DecoratorBuilder(inject(proto().company().name()), 25).build());
        company.setWidget(++row, 0, new DecoratorBuilder(inject(proto().company().website()), 25).build());

        company.setHeader(++row, 0, 1, proto().company().phones().getMeta().getCaption());
        company.setWidget(++row, 0, inject(proto().company().phones(), new PhoneFolder(isEditable())));

        company.setHeader(++row, 0, 1, proto().company().emails().getMeta().getCaption());
        company.setWidget(++row, 0, inject(proto().company().emails(), new EmailFolder(isEditable())));

        contacts.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable())));

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