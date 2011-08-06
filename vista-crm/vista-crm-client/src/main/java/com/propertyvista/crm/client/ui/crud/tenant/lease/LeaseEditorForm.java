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
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.CrmTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.OkCancelBox;
import com.propertyvista.crm.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Pet.WeightUnit;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public LeaseEditorForm(IFormView<LeaseDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public LeaseEditorForm(IEditableComponentFactory factory, IFormView<LeaseDTO> parentView) {
        super(LeaseDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(createTenantsTab(), i18n.tr("Tenants"));
        tabPanel.add(createAddonsTab(), i18n.tr("Add-ons"));
        tabPanel.add(createFinancialsTab(), i18n.tr("Financials"));

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
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().leaseID()), 15);
        main.add(inject(proto().status()), 15);

        HorizontalPanel unitPanel = new HorizontalPanel();
        unitPanel.add(main.createDecorator(inject(proto().unit(), new CEntityLabel()), 15));
        if (isEditable()) {
            unitPanel.add(new Button("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new ShowPopUpBox<SelectUnitBox>(new SelectUnitBox()) {
                        @Override
                        protected void onClose(SelectUnitBox box) {
                            if (box.getSelectedUnit() != null) {
                                get(proto().unit()).setValue(box.getSelectedUnit());
                            }
                        }
                    };
                }
            }));
        }
        main.add(unitPanel);

        main.add(inject(proto().leaseFrom()), 8.2);
        main.add(inject(proto().leaseTo()), 8.2);
        main.add(inject(proto().expectedMoveIn()), 8.2);
        main.add(inject(proto().expectedMoveOut()), 8.2);
        main.add(inject(proto().actualMoveIn()), 8.2);
        main.add(inject(proto().actualMoveOut()), 8.2);
        main.add(inject(proto().signDate()), 8.2);

        return new CrmScrollPanel(main);
    }

    private Widget createTenantsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().tenants(), createTenantsEditorColumns()));

        return new CrmScrollPanel(main);
    }

    private Widget createAddonsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new CrmHeader2Decorator(proto().pets()));
        main.add(inject(proto().pets(), createPetListViewer()));
        main.add(new CrmHeader2Decorator(proto().vehicles()));
        main.add(inject(proto().vehicles(), createVehicleListViewer()));

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

//        main.add(inject(proto().accountNumber()), 15);
//        main.add(inject(proto().currentRent()), 7);
//        main.add(inject(proto().paymentAccepted()), 15);
//        main.add(inject(proto().charges(), createChargesListViewer()));
//        main.add(inject(proto().specialStatus()), 15);

        return new CrmScrollPanel(main);

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

    private CEntityFolderEditor<TenantInLease> createTenantsEditorColumns() {

        return new CrmEntityFolder<TenantInLease>(TenantInLease.class, i18n.tr("Tenant"), isEditable()) {
            private final CrmEntityFolder<TenantInLease> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().tenant(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().birthDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().email(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "8.5em"));
//                columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "5em"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<TenantInLease> createFolderDecorator() {
                IFolderEditorDecorator<TenantInLease> decor = new CrmTableFolderDecorator<TenantInLease>(columns(), parent);
                setExternalAddItemProcessing(true);
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        new ShowPopUpBox<SelectTenantBox>(new SelectTenantBox()) {
                            @Override
                            protected void onClose(SelectTenantBox box) {
                                if (box.getSelectedTenant() != null) {
                                    TenantInLease newTenantInLease = EntityFactory.create(TenantInLease.class);
                                    newTenantInLease.tenant().set(box.getSelectedTenant());
                                    addItem(newTenantInLease);
                                }
                            }
                        };
                    }
                });
                return decor;
            }

            @Override
            protected CEntityFolderItemEditor<TenantInLease> createItem() {
                return new CEntityFolderRowEditor<TenantInLease>(TenantInLease.class, columns()) {
                    @SuppressWarnings("rawtypes")
                    @Override
                    public IsWidget createContent() {
                        if (isFirst()) {
                            HorizontalPanel main = new HorizontalPanel();
                            for (EntityFolderColumnDescriptor column : columns) {
                                CComponent<?> component = createCell(column);
                                // Don't show relation and takeOwnership 
                                if (column.getObject() == proto().relationship() || column.getObject() == proto().takeOwnership()) {
                                    component.setVisible(false);
                                } else if (column.getObject() == proto().tenant().person().email()) {
                                    ((CEditableComponent) component).setEditable(false);
                                }
                                main.add(createCellDecorator(column, component, column.getWidth()));
                            }
                            main.setWidth("100%");
                            return main;
                        } else {
                            return super.createContent();
                        }
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = null;
                        if (isFirst() && proto().status() == column.getObject()) {
                            CTextField textComp = new CTextField();
                            textComp.setEditable(false);
                            textComp.setValue(TenantInLease.Status.Applicant.name());
                            comp = textComp;
                        } else if (proto().tenant() == column.getObject()) {
                            comp = inject(column.getObject(), new CEntityLabel());
                        } else if (proto().tenant().person().birthDate() == column.getObject()) {
                            comp = inject(column.getObject(), new CLabel());
                        } else if (proto().tenant().person().email() == column.getObject()) {
                            comp = inject(column.getObject(), new CLabel());
                        } else {
                            comp = super.createCell(column);

                            if (proto().status() == column.getObject() && comp instanceof CComboBox) {
                                Collection<TenantInLease.Status> status = EnumSet.allOf(TenantInLease.Status.class);
                                status.remove(TenantInLease.Status.Applicant);
                                ((CComboBox) comp).setOptions(status);
                            }
                        }
                        return comp;
                    }

                    @Override
                    public void populate(TenantInLease value) {
                        super.populate(value);

                        if (!isFirst() && !value.tenant().person().birthDate().isNull()) {
                            if (ValidationUtils.isOlderThen18(value.tenant().person().birthDate().getValue())) {
                                enableStatusAndOwnership();
                            } else {
                                setMandatoryDependant();
                            }
                        }
                    }

                    @Override
                    public void addValidations() {

                        get(proto().tenant().person().birthDate()).addValueValidator(new OldAgeValidator());
                        get(proto().tenant().person().birthDate()).addValueValidator(new BirthdayDateValidator());
                        get(proto().tenant().person().birthDate()).addValueValidator(new EditableValueValidator<Date>() {
                            @Override
                            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                                TenantInLease.Status status = getValue().status().getValue();
                                if ((status == TenantInLease.Status.Applicant) || (status == TenantInLease.Status.CoApplicant)) {
                                    // TODO I Believe that this is not correct, this logic has to be applied to Dependents as well, as per VISTA-273
                                    return ValidationUtils.isOlderThen18(value);
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                                return LeaseEditorForm.i18n.tr("Applicant and co-applicant should be at least 18 years old");
                            }
                        });

                        if (!isFirst()) { // all this stuff isn't for primary applicant:  
                            get(proto().tenant().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

                                @Override
                                public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                                    TenantInLease.Status status = getValue().status().getValue();
                                    if ((status == null) || (status == TenantInLease.Status.Dependant)) {
                                        if (ValidationUtils.isOlderThen18(event.getValue())) {
                                            boolean currentEditableState = get(proto().status()).isEditable();
                                            enableStatusAndOwnership();
                                            if (!currentEditableState) {
                                                get(proto().status()).setValue(null);
                                            }
                                        } else {
                                            setMandatoryDependant();
                                        }
                                    }
                                }
                            });

                            get(proto().status()).addValueChangeHandler(
                                    new RevalidationTrigger<TenantInLease.Status>(get(proto().tenant().person().birthDate())));
                        }
                    }

                    @Override
                    public IFolderItemEditorDecorator<TenantInLease> createFolderItemDecorator() {
                        return new CrmFolderItemDecorator<TenantInLease>(parent, parent.isEditable() && !isFirst());
                    }

                    private void setMandatoryDependant() {
                        get(proto().status()).setValue(TenantInLease.Status.Dependant);
                        get(proto().status()).setEditable(false);

//                        get(proto().takeOwnership()).setValue(true);
//                        get(proto().takeOwnership()).setEnabled(false);
                    }

                    private void enableStatusAndOwnership() {
                        get(proto().status()).setEditable(true);
//                        get(proto().takeOwnership()).setEnabled(true);
                    }
                };
            }
        };
    }

    private CEntityFolderEditor<Pet> createPetListViewer() {
        return new CrmEntityFolder<Pet>(Pet.class, i18n.tr("Pet"), isEditable()) {
            private final CrmEntityFolder<Pet> parent = this;

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
                        return new CrmFolderItemDecorator<Pet>(parent);
                    }

                    @Override
                    public void addValidations() {
//                        
// TODO find out what to do with weight validation:                        
//                        
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

    private CEntityFolderEditor<Vehicle> createVehicleListViewer() {
        return new CrmEntityFolder<Vehicle>(Vehicle.class, i18n.tr("Vehicle"), isEditable()) {
            private final CrmEntityFolder<Vehicle> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().plateNumber(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().make(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().country(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().province(), "16em"));
//                columns.add(new EntityFolderColumnDescriptor(proto().parkingSpot(), "13em"));
                //  TODO : filter that parking spot on available spots only and from current building!..                  
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<Vehicle> createItem() {
                return new CEntityFolderRowEditor<Vehicle>(Vehicle.class, columns()) {

                    @Override
                    public IFolderItemEditorDecorator<Vehicle> createFolderItemDecorator() {
                        return new CrmFolderItemDecorator<Vehicle>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        if (column.getObject() == proto().year() && comp instanceof CMonthYearPicker) {
                            ((CMonthYearPicker) comp).setYearRange(new Range(1900, TimeUtils.today().getYear() + 1));
                        }
                        return comp;
                    }

                    @Override
                    public void addValidations() {
                        ProvinceContryFilters.attachFilters(get(proto().province()), get(proto().country()), new OptionsFilter<Province>() {
                            @Override
                            public boolean acceptOption(Province entity) {
                                if (getValue() == null) {
                                    return true;
                                } else {
                                    Country country = getValue().country();
                                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                                }
                            }
                        });
                    }

                };
            }
        };
    }

//
// Selection Boxes:

    private class SelectUnitBox extends OkCancelBox {

        private AptUnit selectedUnit;

        public SelectUnitBox() {
            super("Unit Selection");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);
            ((LeaseView) getParentView()).getUnitListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<AptUnit>() {
                @Override
                public void onSelect(AptUnit selectedItem) {
                    selectedUnit = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(new CrmHeader2Decorator(i18n.tr("Select Building:")));
            vPanel.add(((LeaseView) getParentView()).getBuildingListerView().asWidget());
            vPanel.add(new CrmHeader2Decorator(i18n.tr("Select Unit:")));
            vPanel.add(((LeaseView) getParentView()).getUnitListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("900px", "500px");
        }

        @Override
        protected void onCancel() {
            selectedUnit = null;
        }

        protected AptUnit getSelectedUnit() {
            return selectedUnit;
        }
    }

    private class SelectTenantBox extends OkCancelBox {

        private Tenant selectedTenant;

        public SelectTenantBox() {
            super("Select Tenant");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);
            ((LeaseView) getParentView()).getTenantListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Tenant>() {
                @Override
                public void onSelect(Tenant selectedItem) {
                    selectedTenant = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(((LeaseView) getParentView()).getTenantListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "400px");
        }

        @Override
        protected void onCancel() {
            selectedTenant = null;
        }

        protected Tenant getSelectedTenant() {
            return selectedTenant;
        }
    }
}
