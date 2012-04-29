/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.misc.VistaTODO;

public class BillableItemEditor extends CEntityDecoratableEditor<BillableItem> {

    private static final I18n i18n = I18n.get(BillableItemEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

    private final CEntityEditor<? extends LeaseDTO> lease;

    private final IEditorView<? extends LeaseDTO> leaseEditorView;

    private boolean isService = false;

    private CComponent<LogicalDate, ?> itemEffectiveDateEditor;

    private CComponent<LogicalDate, ?> itemExpirationDateEditor;

    public BillableItemEditor(CEntityEditor<? extends LeaseDTO> lease, IEditorView<? extends LeaseDTO> leaseEditorView, boolean isService) {
        this(lease, leaseEditorView);
        this.isService = isService;
    }

    public BillableItemEditor(CEntityEditor<? extends LeaseDTO> lease, IEditorView<? extends LeaseDTO> leaseEditorView) {
        super(BillableItem.class);
        this.lease = lease;
        this.leaseEditorView = leaseEditorView;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item(), new CEntitySelectorHyperlink<ProductItem>() {
            @Override
            protected AppPlace getTargetPlace() {
                if (getValue().product().isInstanceOf(Service.ServiceV.class)) {
                    Service service = ((Service.ServiceV) getValue().product().cast()).holder();
                    return AppPlaceEntityMapper.resolvePlace(Service.class, service.getPrimaryKey());
                } else if (getValue().product().isInstanceOf(Feature.FeatureV.class)) {
                    Feature feature = ((Feature.FeatureV) getValue().product().cast()).holder();
                    return AppPlaceEntityMapper.resolvePlace(Feature.class, feature.getPrimaryKey());
                } else {
                    return null;
                }
            }

            @Override
            protected EntitySelectorListDialog<ProductItem> getSelectorDialog() {
                return new EntitySelectorListDialog<ProductItem>(i18n.tr("Service Item Selection"), false, lease.getValue().selectedServiceItems()) {
                    @Override
                    public boolean onClickOk() {
                        List<ProductItem> selectedItems = getSelectedItems();
                        if (!selectedItems.isEmpty()) {
                            ((LeaseEditorPresenterBase) leaseEditorView.getPresenter()).setSelectedService(selectedItems.get(0));
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public String defineHeight() {
                        return "100px";
                    };

                    @Override
                    public String defineWidth() {
                        return "400px";
                    }

                    @Override
                    public void show() {
                        if (lease.getValue().selectedBuilding() == null || lease.getValue().selectedBuilding().isNull()) {
                            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select Unit First"));
                        } else {
                            super.show();
                        }
                    }
                };

            }
        }), 30).build());

        get(proto().item()).setViewable(!isService);

        main.setWidget(++row, 0, new DecoratorBuilder(itemEffectiveDateEditor = (CComponent<LogicalDate, ?>) inject(proto().effectiveDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(itemExpirationDateEditor = (CComponent<LogicalDate, ?>) inject(proto().expirationDate()), 9).build());

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, adjustmentPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        if (!VistaTODO.operationDataRemovedForProduction) {
            adjustmentPanel.setH3(0, 0, 1, i18n.tr("Adjustments"));
            adjustmentPanel.setWidget(1, 0, inject(proto().adjustments(), new AdjustmentFolder()));
        }

        get(proto().effectiveDate()).setVisible(false);
        get(proto().expirationDate()).setVisible(false);

        return main;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void onPopulate() {
        super.onPopulate();

        if (!getValue().item().isEmpty()) {
            if (getValue().item().type().isInstanceOf(FeatureItemType.class)) {
                // show effective dates:
                get(proto().effectiveDate()).setVisible(true);
                get(proto().expirationDate()).setVisible(true);

                if (getValue().item().type().<FeatureItemType> cast().featureType().getValue() == Feature.Type.utility) {
                    // correct folder item:
                    if (getParent() instanceof CEntityFolderItem) {
                        CEntityFolderItem<BillableItem> item = (CEntityFolderItem<BillableItem>) getParent();
                        item.setRemovable(false);
                    }
                }
            } else if (getValue().item().type().isInstanceOf(ServiceItemType.class)) {
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);
            }

            if (isViewable()) {
                adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            } else {
                adjustmentPanel.setVisible(true);
            }

            CEntityEditor editor = null;
            BillableItemExtraData extraData = getValue().extraData();

            // add extraData editor if necessary:
            if (getValue().item().type().isInstanceOf(FeatureItemType.class)) {
                switch (getValue().item().type().<FeatureItemType> cast().featureType().getValue()) {
                case parking:
                    editor = new VehicleDataEditor();
                    if (extraData.isNull()) {
                        extraData.set(EntityFactory.create(Vehicle.class));
                    }
                    break;
                case pet:
                    editor = new PetDataEditor();
                    if (extraData.isNull()) {
                        extraData.set(EntityFactory.create(Pet.class));
                    }
                    break;
                }
            }

            if (editor != null) {
                if (this.contains(proto().extraData())) {
                    this.unbind(proto().extraData());
                }
                this.inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        } else {// tweak UI for empty ProductItem:
            adjustmentPanel.setVisible(false);
        }

        if (isEditable()) {
            get(proto().item()).setViewable(false);
            if (isService) {
                get(proto().item()).setEditable(lease.getValue().approvalDate().isNull());
            } else {
                get(proto().item()).setEditable(false);
            }
        } else {
            get(proto().item()).setViewable(true);
        }
    }

    @Override
    public void applyViewabilityRules() {
        super.applyViewabilityRules();
        if (adjustmentPanel != null && getValue() != null) {
            if (isViewable() && !getValue().isNull()) {
                adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            } else {
                adjustmentPanel.setVisible(true);
            }
        }
    }

    private class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        private final List<BillableItemAdjustment> populatedValues = new LinkedList<BillableItemAdjustment>();

        public AdjustmentFolder() {
            super(BillableItemAdjustment.class, !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().adjustmentType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().executionType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().effectiveDate(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().createdBy(), "15em"));
            return columns;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof BillableItemAdjustment) {
                return new BillableItemAdjustmentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void onPopulate() {
            super.onPopulate();

            // memorize populated values:
            populatedValues.clear();
            populatedValues.addAll(getValue());
        }

        @Override
        protected void addItem(BillableItemAdjustment newEntity) {
            if (newEntity.isEmpty()) {
                newEntity.effectiveDate().setValue(new LogicalDate());
            }
            super.addItem(newEntity);
        }

        @Override
        protected void removeItem(CEntityFolderItem<BillableItemAdjustment> item) {
            if (!lease.getValue().approvalDate().isNull() && populatedValues.contains(item.getValue())) {
                item.getValue().expirationDate().setValue(new LogicalDate());
                item.setValue(item.getValue(), false);
                item.setEditable(false);
                ValueChangeEvent.fire(this, getValue());
            } else {
                super.removeItem(item);
            }
        }

        private class BillableItemAdjustmentEditor extends CEntityFolderRowEditor<BillableItemAdjustment> {

            public BillableItemAdjustmentEditor() {
                super(BillableItemAdjustment.class, columns());
                setViewable(false);
            }

            @Override
            protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                CComponent<?, ?> cell = super.createCell(column);
                if (column.getObject() == proto().expirationDate()) {
                    try {
                        ((CComponent<LogicalDate, ?>) cell).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(itemEffectiveDateEditor));
                        itemEffectiveDateEditor.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(cell));
                        ((CComponent<LogicalDate, ?>) cell).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(itemExpirationDateEditor));
                        itemExpirationDateEditor.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(cell));
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
                return cell;
            }

            @Override
            public void addValidations() {
                super.addValidations();

                new StartEndDateValidation(get(proto().effectiveDate()), get(proto().expirationDate()));
                get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));
                get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));

                get(proto().expirationDate()).addValueValidator(new EditableValueValidator<Date>() {
                    @Override
                    public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                        if (!(value == null) && !(getValue().billableItem() == null) && !(getValue().billableItem().expirationDate().getValue() == null)
                                && value.after(getValue().billableItem().expirationDate().getValue())) {
                            return new ValidationFailure("The date should not exceed the item expiration date");
                        }
                        if (!(value == null) && (getValue().billableItem().expirationDate().getValue() == null)
                                && value.after(lease.getValue().leaseTo().getValue())) {
                            return new ValidationFailure("The date should not exceed the lease expiration date");
                        }

                        return null;
                    }

                });

                get(proto().effectiveDate()).addValueValidator(new EditableValueValidator<Date>() {
                    @Override
                    public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                        if (!(value == null) && !(getValue().billableItem() == null) && !(getValue().billableItem().effectiveDate().getValue() == null)
                                && value.before(getValue().billableItem().effectiveDate().getValue())) {
                            return new ValidationFailure("The date should not precede the item effective date");
                        }
                        if (!(value == null) && (getValue().billableItem().effectiveDate().getValue() == null)
                                && value.before(lease.getValue().leaseTo().getValue())) {
                            return new ValidationFailure("The date should not precede the lease effective date");
                        }

                        return null;
                    }

                });

            }
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        new StartEndDateValidation(get(proto().effectiveDate()), get(proto().expirationDate()));
        get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));
        get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));

        get(proto().expirationDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (!(value == null) && value.after(lease.getValue().leaseTo().getValue())) {
                    return new ValidationFailure("The date should not exceed the lease expiration date");
                }
                for (BillableItemAdjustment a : getValue().adjustments()) {
                    if (!(value == null) && !(a.expirationDate().getValue() == null) && a.expirationDate().getValue().after(value)) {
                        return new ValidationFailure("One or more adjustments for this item expire after the specificed date");
                    }
                }

                return null;
            }

        });

        get(proto().effectiveDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (!(value == null) && value.before(lease.getValue().leaseFrom().getValue())) {
                    return new ValidationFailure("The date should not precede the lease start date");
                }
                for (BillableItemAdjustment a : getValue().adjustments()) {
                    if (!(value == null) && !(a.effectiveDate().getValue() == null) && a.effectiveDate().getValue().before(value)) {
                        return new ValidationFailure("One or more adjustments for this item start before the specificed date");
                    }
                }

                return null;
            }

        });
    }
}