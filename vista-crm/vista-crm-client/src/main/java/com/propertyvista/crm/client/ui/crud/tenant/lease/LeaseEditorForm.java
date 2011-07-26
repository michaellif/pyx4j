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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.CrmFolderItemDecorator;
import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Pet.WeightUnit;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public LeaseEditorForm(IView<LeaseDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public LeaseEditorForm(IEditableComponentFactory factory, IView<LeaseDTO> parentView) {
        super(LeaseDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createBuildingTab(), i18n.tr("Building"));
        tabPanel.add(createUnitTab(), i18n.tr("Unit"));
        tabPanel.addDisable(((LeaseView) getParentView()).getTenantListerView().asWidget(), i18n.tr("Tenants"));
        tabPanel.add(new ScrollPanel(createPetsTab()), i18n.tr("Add-ons"));

        tabPanel.add(new ScrollPanel(createDetailsTab()), i18n.tr("Details"));
        tabPanel.add(new ScrollPanel(createFinancialsTab()), i18n.tr("Financials"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createBuildingTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new HTML("&nbsp"));
        main.add(inject(proto().selectedBuilding(), new CEntityLabel()), 50);
        if (isEditable()) {
            main.add(((LeaseView) getParentView()).getBuildingListerView().asWidget());
        }

        return main;
    }

    private Widget createUnitTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new HTML("&nbsp"));
        main.add(inject(proto().unit(), new CEntityLabel()), 50);
        if (isEditable()) {
            main.add(((LeaseView) getParentView()).getUnitListerView().asWidget());
        }

        return main;
    }

    private Widget createPetsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().pets(), createPetListViewer()));

        return main;
    }

    private Widget createDetailsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().leaseID()), 15);
        main.add(inject(proto().leaseFrom()), 8.2);
        main.add(inject(proto().leaseTo()), 8.2);
        main.add(inject(proto().expectedMoveIn()), 8.2);
        main.add(inject(proto().expectedMoveOut()), 8.2);
        main.add(inject(proto().actualMoveIn()), 8.2);
        main.add(inject(proto().actualMoveOut()), 8.2);
        main.add(inject(proto().signDate()), 8.2);

        return main;
    }

    private Widget createFinancialsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

//        main.add(inject(proto().accountNumber()), 15);
//        main.add(inject(proto().currentRent()), 7);
//        main.add(inject(proto().paymentAccepted()), 15);
//        main.add(inject(proto().charges(), createChargesListViewer()));
//        main.add(inject(proto().specialStatus()), 15);

        return main;

    }

    private CEntityFolderEditor<ChargeLine> createChargesListViewer() {
        return new CrmEntityFolder<ChargeLine>(ChargeLine.class, i18n.tr("Charge Line"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().label(), "10em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<Pet> createPetListViewer() {
        return new CrmEntityFolder<Pet>(Pet.class, i18n.tr("Pet"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "14em"));
                columns.add(new EntityFolderColumnDescriptor(proto().color(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto().breed(), "13em"));
                columns.add(new EntityFolderColumnDescriptor(proto().weight(), "4em"));
                columns.add(new EntityFolderColumnDescriptor(proto().weightUnit(), "4em"));
                columns.add(new EntityFolderColumnDescriptor(proto().birthDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().chargeLine(), "7em"));
                return columns;
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
                return new CEntityFolderRowEditor<Pet>(Pet.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<Pet> createFolderItemDecorator() {
                        return new CrmFolderItemDecorator<Pet>(LeaseEditorForm.this.i18n.tr("Remove Pet"), LeaseEditorForm.this.isEditable());
                    }

//                    @Override
//                    public void addValidations() {
//                        EditableValueValidator<Integer> weightValidator = new EditableValueValidator<Integer>() {
//                            @Override
//                            public boolean isValid(CEditableComponent<Integer, ?> component, Integer value) {
//                                return (value == null)
//                                        || DomainUtil.getWeightKg(value, getValue().weightUnit().getValue()) <= LeaseEditorForm.this.getValue().pets()
//                                                .maxPetWeight().getValue();
//                            }
//
//                            @Override
//                            public String getValidationMessage(CEditableComponent<Integer, ?> component, Integer value) {
//                                return i18n.tr("Max allowed weight {0} {1} ",
//                                        DomainUtil.getWeightKgToUnit(LeaseEditorForm.this.getValue().pets().maxPetWeight(), getValue().weightUnit()),
//                                        getValue().weightUnit().getStringView());
//                            }
//                        };
//
//                        get(proto().weight()).addValueValidator(weightValidator);
//                        get(proto().weightUnit()).addValueChangeHandler(new RevalidationTrigger<WeightUnit>(get(proto().weight())));
//
//                        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());
//                    }

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