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
package com.propertyvista.crm.client.ui.crud.application;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.Pet;
import com.propertyvista.portal.domain.ptapp.Pet.WeightUnit;
import com.propertyvista.portal.domain.util.DomainUtil;

public class ApplicationEditorForm extends CrmEntityForm<ApplicationDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public ApplicationEditorForm(IView<ApplicationDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public ApplicationEditorForm(IEditableComponentFactory factory, IView<ApplicationDTO> parentView) {
        super(ApplicationDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.addDisable(((ApplicationView) getParentView()).getUnitListerView().asWidget(), i18n.tr("Selected Unit"));
        tabPanel.addDisable(((ApplicationView) getParentView()).getTenantListerView().asWidget(), i18n.tr("Tenants"));
        tabPanel.add(new ScrollPanel(createPetsTab()), i18n.tr("Pets"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createPetsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().pets().pets(), createPetsListEditor()));

        main.setWidth("100%");
        return main;
    }

    private CEntityFolderEditor<Pet> createPetsListEditor() {

        return new CEntityFolderEditor<Pet>(Pet.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "14em"));
                columns.add(new EntityFolderColumnDescriptor(proto().color(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto().breed(), "13em"));
                columns.add(new EntityFolderColumnDescriptor(proto().weight(), "4em"));
                columns.add(new EntityFolderColumnDescriptor(proto().weightUnit(), "4em"));
                columns.add(new EntityFolderColumnDescriptor(proto().birthDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().chargeLine(), "7em"));
            }

            @Override
            protected IFolderEditorDecorator<Pet> createFolderDecorator() {
                return new TableFolderEditorDecorator<Pet>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add a pet"));
            }

            @Override
            public CEditableComponent<?, ?> create(IObject<?> member) {
                if (member instanceof ChargeLine) {
                    return new CEntityLabel();
                } else {
                    return super.create(member);
                }
            }

            @Override
            protected CEntityFolderItemEditor<Pet> createItem() {
                return new CEntityFolderRowEditor<Pet>(Pet.class, columns) {

                    @Override
                    public IFolderItemEditorDecorator<Pet> createFolderItemDecorator() {
                        return new TableFolderItemEditorDecorator<Pet>(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove pet"));
                    }

                    @Override
                    public void addValidations() {
                        EditableValueValidator<Integer> weightValidator = new EditableValueValidator<Integer>() {
                            @Override
                            public boolean isValid(CEditableComponent<Integer, ?> component, Integer value) {
                                return (value == null)
                                        || DomainUtil.getWeightKg(value, getValue().weightUnit().getValue()) <= ApplicationEditorForm.this.getValue().pets()
                                                .petWeightMaximum().getValue();
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Integer, ?> component, Integer value) {
                                return i18n.tr("Max allowed weight {0} {1} ",
                                        DomainUtil.getWeightKgToUnit(ApplicationEditorForm.this.getValue().pets().petWeightMaximum(), getValue().weightUnit()),
                                        getValue().weightUnit().getStringView());
                            }
                        };

                        get(proto().weight()).addValueValidator(weightValidator);
                        get(proto().weightUnit()).addValueChangeHandler(new RevalidationTrigger<WeightUnit>(get(proto().weight())));

                        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());
                    }

                };
            }

            @Override
            protected void createNewEntity(Pet newEntity, AsyncCallback<Pet> callback) {
                newEntity.weightUnit().setValue(WeightUnit.lb);
//              
//TODO: find out where to get that ChargesSharedCalculation()...
//              
//                ChargesSharedCalculation.calculatePetCharges(PetsViewForm.this.getValue().petChargeRule(), newEntity);
                super.createNewEntity(newEntity, callback);
            }

        };

    }
}