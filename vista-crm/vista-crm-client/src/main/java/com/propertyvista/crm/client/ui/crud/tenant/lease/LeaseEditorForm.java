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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.components.CEmailLabel;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderItemDecorator;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ChargeItemAdjustment;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.ApplicationStatusDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    private Widget appStatusTab;

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
        tabPanel.add(createServiceAgreementTab(), i18n.tr("Service Agreement"));

        appStatusTab = createAppStatustab();

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void populate(LeaseDTO value) {

        if (value != null && Lease.Status.ApplicationInProgress.equals(value.status().getValue())) {
            tabPanel.add(appStatusTab, i18n.tr("Application Status"));
        } else {
            tabPanel.remove(appStatusTab);
        }

        super.populate(value);
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
        main.add(inject(proto().type()), 15);
        main.add(inject(proto().status()), 15);

        HorizontalPanel unitPanel = new HorizontalPanel();
        unitPanel.add(main.createDecorator(inject(proto().unit(), new CEntityLabel()), 25));
        if (isEditable()) {
            unitPanel.add(new Button(i18n.tr("Select..."), new ClickHandler() {
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

        main.add(inject(proto().tenants(), createTenantsListViewer()));

        return new CrmScrollPanel(main);
    }

    private Widget createServiceAgreementTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        HorizontalPanel serviceItemPanel = new HorizontalPanel();
        serviceItemPanel.add(main.createDecorator(inject(proto().serviceAgreement().serviceItem(), new CEntityLabel()), 35));
        if (isEditable()) {
            serviceItemPanel.add(new Button("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (getValue().selectedBuilding() == null || getValue().selectedBuilding().isNull()) {
                        MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select Building/Unit first!"));
                    } else {
                        new ShowPopUpBox<SelectServiceItemBox>(new SelectServiceItemBox()) {
                            @Override
                            protected void onClose(SelectServiceItemBox box) {
                                if (box.getSelectedItem() != null) {
                                    ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                                    newItem.item().set(box.getSelectedItem());
                                    newItem.price().setValue(box.getSelectedItem().price().getValue());
                                    get(proto().serviceAgreement().serviceItem()).setValue(newItem);
                                    ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedService(box.getSelectedItem());
                                }
                            }
                        };
                    }
                }
            }));
        }
        main.add(serviceItemPanel);

        main.add(new CrmSectionSeparator(i18n.tr("Charge Items:")));
        main.add(inject(proto().serviceAgreement().featureItems(), createFeaturesFolderEditor()));

        main.add(new CrmSectionSeparator(i18n.tr("Concessions:")));
        main.add(inject(proto().serviceAgreement().concessions(), createConcessionsFolderEditor()));

        main.add(new HTML("&nbsp"));
        main.add(inject(proto().serviceAgreement().account()), 15);

        return new CrmScrollPanel(main);
    }

    private Widget createAppStatustab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new CrmSectionSeparator(proto().masterApplicationStatus().individualApplications()));
        main.add(inject(proto().masterApplicationStatus().individualApplications(), createAppStatusListViewer()));

        return new CrmScrollPanel(main);
    }

//
// List Viewers:

    private CEntityFolder<TenantInLease> createTenantsListViewer() {
        return new VistaEntityFolder<TenantInLease>(TenantInLease.class, isEditable()) {
            private final VistaEntityFolder<TenantInLease> parent = this;

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
            protected void addItem() {
                new ShowPopUpBox<SelectTenantBox>(new SelectTenantBox()) {
                    @Override
                    protected void onClose(SelectTenantBox box) {
                        if (box.getSelectedTenant() != null) {
                            TenantInLease newTenantInLease = EntityFactory.create(TenantInLease.class);
                            newTenantInLease.lease().setPrimaryKey(LeaseEditorForm.this.getValue().getPrimaryKey());
                            newTenantInLease.tenant().set(box.getSelectedTenant());
                            addItem(newTenantInLease);
                        }
                    }
                };
            }

            @Override
            protected IFolderDecorator<TenantInLease> createDecorator() {
                IFolderDecorator<TenantInLease> decor = new VistaTableFolderDecorator<TenantInLease>(columns(), parent);
                return decor;
            }

            @Override
            protected void removeItem(CEntityFolderItemEditor<TenantInLease> item) {
                ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).removeTenat(item.getValue());
                super.removeItem(item);
            }

            @Override
            protected CEntityFolderItemEditor<TenantInLease> createItem() {
                return new CEntityFolderRowEditor<TenantInLease>(TenantInLease.class, columns()) {
                    @Override
                    public IsWidget createContent() {
                        if (isFirst()) {
                            HorizontalPanel main = new HorizontalPanel();
                            for (EntityFolderColumnDescriptor column : columns) {
                                CComponent<?> component = createCell(column);
                                // Don't show relation and takeOwnership 
                                if (column.getObject() == proto().relationship() || column.getObject() == proto().takeOwnership()) {
                                    component.setVisible(false);
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
                            comp = inject(column.getObject(), new CEmailLabel());
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
                                return LeaseEditorForm.i18n.tr("Applicant and Co-applicant must be at least 18 years old");
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
                    public IFolderItemDecorator<TenantInLease> createDecorator() {
                        return new VistaTableFolderItemDecorator<TenantInLease>(parent, parent.isEditable() && !isFirst());
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

    private CEntityFolder<ApplicationStatusDTO> createAppStatusListViewer() {
        return new VistaEntityFolder<ApplicationStatusDTO>(ApplicationStatusDTO.class, i18n.tr("Status"), false) {

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().progress(), "10em"));
                return columns;
            }
        };
    }

    private CEntityFolder<ChargeItem> createFeaturesFolderEditor() {
        return new VistaEntityFolder<ChargeItem>(ChargeItem.class, isEditable()) {
            private final VistaEntityFolder<ChargeItem> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return null;
            }

            @Override
            protected void addItem() {
                if (LeaseEditorForm.this.getValue().serviceAgreement().serviceItem().isNull()) {
                    MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select Service Item first!"));
                } else {
                    new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox()) {
                        @Override
                        protected void onClose(SelectFeatureBox box) {
                            if (box.getSelectedItems() != null) {
                                for (ServiceItem item : box.getSelectedItems()) {
                                    ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                                    newItem.item().set(item);
                                    newItem.price().setValue(item.price().getValue());
                                    newItem.adjustedPrice().setValue(item.price().getValue());
                                    addItem(newItem);
                                }
                            }
                        }
                    };
                }
            }

            @Override
            protected IFolderDecorator<ChargeItem> createDecorator() {
                VistaBoxFolderDecorator<ChargeItem> decor = new VistaBoxFolderDecorator<ChargeItem>(parent);
                return decor;
            }

            @Override
            protected CEntityFolderItemEditor<ChargeItem> createItem() {
                return new CEntityFolderRowEditor<ChargeItem>(ChargeItem.class, columns()) {
                    private final CEntityFolderRowEditor<ChargeItem> chargeItemEditor = this;

                    private final SimplePanel extraDataPanel = new SimplePanel();

                    private VistaDecoratorsFlowPanel adjustmentPanel;

                    private final VistaBoxFolderItemDecorator<ChargeItem> decor = new VistaBoxFolderItemDecorator<ChargeItem>(parent);

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable(), 10);
                        VistaDecoratorsSplitFlowPanel split;

                        CLabel lb;
                        main.add(split = new VistaDecoratorsSplitFlowPanel(!parent.isEditable(), 10, 22));
                        split.getLeftPanel().add(inject(proto().item().type().name(), lb = new CLabel()));
                        lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

                        CNumberLabel nl;
                        split.getRightPanel().add(inject(proto().price(), nl = new CNumberLabel()), 6);
                        nl.setNumberFormat(proto().price().getMeta().getFormat());

                        split.getRightPanel().add(inject(proto().adjustedPrice(), nl = new CNumberLabel()), 6);
                        nl.setNumberFormat(proto().adjustedPrice().getMeta().getFormat());
                        nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

                        main.add(extraDataPanel);

                        adjustmentPanel = new VistaDecoratorsFlowPanel(!parent.isEditable(), 10);
                        adjustmentPanel.add(new CrmSectionSeparator(VistaEntityFolder.i18n.tr("Adjustments:")));
                        adjustmentPanel.add(inject(proto().adjustments(), createItemAdjustmentListView(chargeItemEditor)));
                        main.add(adjustmentPanel);
                        return main;
                    }

                    @Override
                    public IFolderItemDecorator<ChargeItem> createDecorator() {
                        return decor;
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public void populate(ChargeItem value) {
                        super.populate(value);

                        if (value.item().type().featureType().getValue() == Feature.Type.utility) {
                            decor.setRemovable(false);
                        }

                        if (!parent.isEditable()) {
                            adjustmentPanel.setVisible(!value.adjustments().isEmpty());
                            get(proto().adjustedPrice()).setVisible(!value.adjustments().isEmpty());
                        }

                        CEntityEditor editor = null;
                        switch (value.item().type().featureType().getValue()) {
                        case parking:
                            editor = new CEntityEditor<Vehicle>(Vehicle.class) {
                                @Override
                                public IsWidget createContent() {
                                    VistaDecoratorsFlowPanel panel = new VistaDecoratorsFlowPanel(!parent.isEditable(), 10);
                                    panel.add(new CrmSectionSeparator(VistaEntityFolder.i18n.tr("Vehicle data:")));
                                    panel.add(inject(proto().plateNumber()), 8);
                                    panel.add(inject(proto().year()), 5);
                                    panel.add(inject(proto().make()), 8);
                                    panel.add(inject(proto().model()), 8);
                                    panel.add(inject(proto().country()), 9);
                                    panel.add(inject(proto().province()), 16);
                                    return panel;
                                }
                            };

                            if (value.extraData().isNull()) {
                                value.extraData().set(EntityFactory.create(Vehicle.class));
                            }
                            break;
                        case pet:
                            editor = new CEntityEditor<Pet>(Pet.class) {
                                @Override
                                public IsWidget createContent() {
                                    VistaDecoratorsFlowPanel panel = new VistaDecoratorsFlowPanel(!parent.isEditable(), 10);
                                    panel.add(new CrmSectionSeparator(VistaEntityFolder.i18n.tr("Pet data:")));
                                    panel.add(inject(proto().name()), 14);
                                    panel.add(inject(proto().color()), 6);
                                    panel.add(inject(proto().breed()), 13);
                                    panel.add(inject(proto().weight()), 4);
                                    panel.add(inject(proto().weightUnit()), 4);
                                    panel.add(inject(proto().birthDate()), 8.2);
                                    return panel;
                                }
                            };

                            if (value.extraData().isNull()) {
                                value.extraData().set(EntityFactory.create(Pet.class));
                            }
                            break;
                        }

                        if (editor != null) {
                            editor.onBound(chargeItemEditor);
                            editor.populate(value.extraData().cast());
                            extraDataPanel.setWidget(editor);
                        }
                    }

                    @Override
                    public void addValidations() {
                        super.addValidations();
                        addValueChangeHandler(new ValueChangeHandler<ChargeItem>() {
                            @Override
                            public void onValueChange(ValueChangeEvent<ChargeItem> event) {
                                calculateAdjustments();
                            }
                        });
                    }

                    private void calculateAdjustments() {
                        if (parent.isEditable()) {
                            ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).calculateChargeItemAdjustments(
                                    new AsyncCallback<Double>() {

                                        @Override
                                        public void onSuccess(Double result) {
                                            get(proto().adjustedPrice()).setValue(result);
                                        }

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            // TODO Auto-generated method stub
                                        }
                                    }, getValue());
                        }
                    }
                };
            }

            @Override
            public void populate(IList<ChargeItem> value) {
                super.populate(value);

                // prepopulate utilities for the new item: 
                if (parent.isEditable() && value.isEmpty()) {
                    for (ServiceItem item : LeaseEditorForm.this.getValue().selectedUtilityItems()) {
                        ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                        newItem.item().set(item);
                        newItem.price().setValue(item.price().getValue());
                        addItem(newItem);
                    }
                }
            }
        };
    }

    private CEntityFolder<ServiceConcession> createConcessionsFolderEditor() {
        return new VistaEntityFolder<ServiceConcession>(ServiceConcession.class, isEditable()) {
            private final VistaEntityFolder<ServiceConcession> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().concession(), "50em"));
                return columns;
            }

            @Override
            protected void addItem() {
                if (LeaseEditorForm.this.getValue().serviceAgreement().serviceItem().isNull()) {
                    MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select Service Item first!"));
                } else {
                    new ShowPopUpBox<SelectConcessionBox>(new SelectConcessionBox()) {
                        @Override
                        protected void onClose(SelectConcessionBox box) {
                            if (box.getSelectedItems() != null) {
                                for (Concession item : box.getSelectedItems()) {
                                    ServiceConcession newItem = EntityFactory.create(ServiceConcession.class);
                                    newItem.concession().set(item);
                                    addItem(newItem);
                                }
                            }
                        }
                    };
                }
            }

            @Override
            protected IFolderDecorator<ServiceConcession> createDecorator() {
                VistaTableFolderDecorator<ServiceConcession> decor = new VistaTableFolderDecorator<ServiceConcession>(columns(), parent);
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderItemEditor<ServiceConcession> createItem() {
                return new CEntityFolderRowEditor<ServiceConcession>(ServiceConcession.class, columns()) {
                    @Override
                    public IFolderItemDecorator<ServiceConcession> createDecorator() {
                        return new VistaTableFolderItemDecorator<ServiceConcession>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject() == proto().concession()) {
                            return inject(column.getObject(), new CEntityLabel());
                        }
                        return super.createCell(column);
                    }
                };
            }
        };
    }

    private CEntityFolder<ChargeItemAdjustment> createItemAdjustmentListView(final CEntityFolderRowEditor<ChargeItem> chargeItemEditor) {
        return new VistaEntityFolder<ChargeItemAdjustment>(ChargeItemAdjustment.class, isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().termType(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                return columns;
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
            ((LeaseEditorView) getParentView()).getUnitListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<AptUnit>() {
                @Override
                public void onSelect(AptUnit selectedItem) {
                    selectedUnit = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Building:")));
            vPanel.add(((LeaseEditorView) getParentView()).getBuildingListerView().asWidget());
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Unit:")));
            vPanel.add(((LeaseEditorView) getParentView()).getUnitListerView().asWidget());
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
            ((LeaseEditorView) getParentView()).getTenantListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Tenant>() {
                @Override
                public void onSelect(Tenant selectedItem) {
                    selectedTenant = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(((LeaseEditorView) getParentView()).getTenantListerView().asWidget());
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

    private class SelectServiceItemBox extends OkCancelBox {

        private CComboBox<ServiceItem> combo;

        private ServiceItem selectedItem;

        public SelectServiceItemBox() {
            super("Service Item Selection");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!getValue().selectedServiceItems().isEmpty()) {
                okButton.setEnabled(true);
                combo = new CComboBox<ServiceItem>() {
                    @Override
                    public String getItemName(ServiceItem o) {
                        if (o == null) {
                            return super.getItemName(o);
                        } else {
                            return o.getStringView();
                        }
                    }
                };
                combo.setOptions(getValue().selectedServiceItems());
                combo.setValue(combo.getOptions().get(0));
                combo.addValueChangeHandler(new ValueChangeHandler<ServiceItem>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<ServiceItem> event) {
                        okButton.setEnabled(event.getValue() != null);
                    }
                });
                combo.setWidth("100%");
                return combo.asWidget();
            } else {
                return new HTML(i18n.tr("There are no Service Items"));
            }

        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItem = combo.getValue();
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItem = null;
        }

        protected ServiceItem getSelectedItem() {
            return selectedItem;
        }
    }

    private class SelectFeatureBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItem> selectedItems;

        public SelectFeatureBox() {
            super("Select Features");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!getValue().selectedFeatureItems().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                List<ServiceItem> alreadySelected = new ArrayList<ServiceItem>();
                for (ChargeItem item : getValue().serviceAgreement().featureItems()) {
                    alreadySelected.add(item.item());
                }

                for (ServiceItem item : getValue().selectedFeatureItems()) {
                    if (!alreadySelected.contains(item)) {
                        list.addItem(item.getStringView());
                        list.setValue(list.getItemCount() - 1, item.id().toString());
                    }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All features have been already selected!"));
                }
            } else {
                return new HTML(i18n.tr("There are no features for this service!"));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<ServiceItem>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (ServiceItem item : getValue().selectedFeatureItems()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItems = null;
        }

        protected List<ServiceItem> getSelectedItems() {
            return selectedItems;
        }
    }

    private class SelectConcessionBox extends OkCancelBox {

        private ListBox list;

        private List<Concession> selectedItems;

        public SelectConcessionBox() {
            super("Select Concessions");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!getValue().selectedConcessions().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                List<Concession> alreadySelected = new ArrayList<Concession>();
                for (ServiceConcession item : getValue().serviceAgreement().concessions()) {
                    alreadySelected.add(item.concession());
                }

                for (Concession item : getValue().selectedConcessions()) {
                    if (!alreadySelected.contains(item)) {
                        list.addItem(item.getStringView());
                        list.setValue(list.getItemCount() - 1, item.id().toString());
                    }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All concessions have been already selected!"));
                }
            } else {
                return new HTML(i18n.tr("There are no concessions for this service!"));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<Concession>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (Concession item : getValue().selectedConcessions()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItems = null;
        }

        protected List<Concession> getSelectedItems() {
            return selectedItems;
        }
    }
}
