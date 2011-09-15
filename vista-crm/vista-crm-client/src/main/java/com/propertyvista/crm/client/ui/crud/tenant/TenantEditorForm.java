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
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    private final VistaDecoratorsFlowPanel person = new VistaDecoratorsFlowPanel(!isEditable());

    private final VistaDecoratorsFlowPanel company = new VistaDecoratorsFlowPanel(!isEditable());

    private final VistaDecoratorsFlowPanel contacts = new VistaDecoratorsFlowPanel(!isEditable());

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public TenantEditorForm(IFormView<TenantDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public TenantEditorForm(IEditableComponentFactory factory, IFormView<TenantDTO> parentView) {
        super(TenantDTO.class, factory);
        setParentView(parentView);
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
        SubtypeInjectors.injectPhones(company, proto().company().phones(), this);
        SubtypeInjectors.injectEmails(company, proto().company().emails(), this);

        contacts.add(inject(proto().emergencyContacts(), createEmergencyContactFolderEditor()));

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

    private CEntityFolderEditor<EmergencyContact> createEmergencyContactFolderEditor() {
        return new CrmEntityFolder<EmergencyContact>(EmergencyContact.class, i18n.tr("Contact"), isEditable()) {
            private final CrmEntityFolder<EmergencyContact> parent = this;

            @Override
            protected IFolderEditorDecorator<EmergencyContact> createFolderDecorator() {
                return new CrmBoxFolderDecorator<EmergencyContact>(parent);
            }

            @Override
            protected CEntityFolderItemEditor<EmergencyContact> createItem() {
                return new CEntityFolderItemEditor<EmergencyContact>(EmergencyContact.class) {
                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!parent.isEditable());
                        main.add(split);

                        if (parent.isEditable()) {
                            split.getLeftPanel().add(inject(proto().name().namePrefix()), 6);
                            split.getLeftPanel().add(inject(proto().name().firstName()), 12);
                            split.getLeftPanel().add(inject(proto().name().middleName()), 12);
                            split.getLeftPanel().add(inject(proto().name().lastName()), 20);
                        } else {
                            split.getLeftPanel().add(inject(proto().name(), new CEntityLabel()), 20, "Contactee");
                            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
                        }

                        split.getRightPanel().add(inject(proto().homePhone()), 15);
                        split.getRightPanel().add(inject(proto().mobilePhone()), 15);
                        split.getRightPanel().add(inject(proto().workPhone()), 15);

                        VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!parent.isEditable());
                        main.add(new VistaLineSeparator());
                        main.add(split2);

                        AddressUtils.injectIAddress(split2, proto().address(), this);

                        return main;
                    }

                    @Override
                    public IFolderItemEditorDecorator<EmergencyContact> createFolderItemDecorator() {
                        return new CrmBoxFolderItemDecorator<EmergencyContact>(parent, !isFirst() && parent.isEditable());
                    }
                };
            }

            @Override
            public void populate(IList<EmergencyContact> value) {
                super.populate(value);
                if (parent.isEditable() && value.isEmpty()) {
                    addItem(); // at least one Emergency Contact should be present!..
                }
            }

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return null;
            }
        };
    }
}