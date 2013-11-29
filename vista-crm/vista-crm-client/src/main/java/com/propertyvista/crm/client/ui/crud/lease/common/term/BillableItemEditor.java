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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.editors.YardiDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class BillableItemEditor extends CEntityDecoratableForm<BillableItem> {

    static final I18n i18n = I18n.get(BillableItemEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final TwoColumnFlexFormPanel adjustmentPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel depositPanel = new TwoColumnFlexFormPanel();

    private final CEntityForm<LeaseTermDTO> leaseTerm;

    private final LeaseTermEditorView leaseTermEditorView;

    private CComponent<LogicalDate> itemEffectiveDateEditor;

    private CComponent<LogicalDate> itemExpirationDateEditor;

    public BillableItemEditor(CEntityForm<LeaseTermDTO> leaseTerm, LeaseTermEditorView leaseTermEditorView) {
        super(BillableItem.class);
        this.leaseTerm = leaseTerm;
        this.leaseTermEditorView = leaseTermEditorView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel();
        int row = -1;

        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().item(), new CEntitySelectorHyperlink<ProductItem>() {
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
                return new EntitySelectorListDialog<ProductItem>(i18n.tr("Service Item Selection"), false, leaseTerm.getValue().selectedServiceItems()) {
                    @Override
                    public boolean onClickOk() {
                        List<ProductItem> selectedItems = getSelectedItems();
                        if (!selectedItems.isEmpty()) {
                            assert (leaseTermEditorView != null);
                            ((LeaseTermEditorView.Presenter) leaseTermEditorView.getPresenter()).setSelectedService(selectedItems.get(0));
                            return true;
                        } else {
                            return false;
                        }
                    }
                };

            }
        }), 20).build());

        flexPanel.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().agreedPrice()), 10).build());
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(itemEffectiveDateEditor = (CComponent<LogicalDate>) inject(proto().effectiveDate()), 9).build());
        flexPanel.setWidget(row, 1, new FormDecoratorBuilder(itemExpirationDateEditor = (CComponent<LogicalDate>) inject(proto().expirationDate()), 9).build());

        flexPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());
        flexPanel.getFlexCellFormatter().setColSpan(row, 0, 2);

        flexPanel.setWidget(++row, 0, 2, extraDataPanel);

        flexPanel.setWidget(++row, 0, 2, adjustmentPanel);

        flexPanel.setWidget(++row, 0, 2, depositPanel);

        adjustmentPanel.setH3(0, 0, 2, proto().adjustments().getMeta().getCaption());
        adjustmentPanel.setWidget(1, 0, 2, inject(proto().adjustments(), new AdjustmentFolder()));

        depositPanel.setH3(0, 0, 2, proto().deposits().getMeta().getCaption());
        depositPanel.setWidget(1, 0, 2, inject(proto().deposits(), new DepositFolder()));

        get(proto().effectiveDate()).setVisible(false);
        get(proto().expirationDate()).setVisible(false);

        return flexPanel;
    }

    void updateServiceItemEditability() {
        if (isEditable()) {
            boolean isLeaseApproved = !leaseTerm.getValue().lease().approvalDate().isNull();

            // set editable for non-approved leases (and multiple service items):
            get(proto().agreedPrice()).setEditable(!isLeaseApproved);
            get(proto().item()).setEditable(!isLeaseApproved && leaseTerm.getValue().selectedServiceItems().size() > 1);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // tweak UI for ProductItem:
        if (VistaFeatures.instance().yardiIntegration()) {

            get(proto().item()).setVisible(false);

            get(proto().effectiveDate()).setVisible(true);
            get(proto().effectiveDate()).setTooltip(null);

            get(proto().expirationDate()).setVisible(true);
            get(proto().expirationDate()).setTooltip(null);

            get(proto().agreedPrice()).setEditable(true);

            adjustmentPanel.setVisible((isEditable() || !getValue().adjustments().isEmpty()));
            depositPanel.setVisible((isEditable() || !getValue().deposits().isEmpty()));

        } else if (!getValue().item().isEmpty()) {
            if (ARCode.Type.services().contains(getValue().item().product().holder().code().type().getValue())) {
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);

                updateServiceItemEditability();
            } else if (ARCode.Type.features().contains(getValue().item().product().holder().code().type().getValue())) {
                // show/hide effective dates (hide expiration for non-recurring; show in editor, hide in viewer if empty):
                boolean recurring = isRecurringFeature(getValue().item().product());
                get(proto().effectiveDate()).setVisible((isEditable() || !getValue().effectiveDate().isNull()));
                get(proto().expirationDate()).setVisible(recurring && (isEditable() || !getValue().expirationDate().isNull()));

                if (isEditable()) {
                    get(proto().agreedPrice()).setEditable(!getValue().finalized().isBooleanTrue());
                    get(proto().item()).setEditable(false);
                }

                // correct folder item:
                if (getParent() instanceof CEntityFolderItem) {
                    CEntityFolderItem<BillableItem> item = (CEntityFolderItem<BillableItem>) getParent();

                    item.setRemovable(!isMandatoryFeature(getValue().item().product()));

                    // if lease is approved:
                    if (!leaseTerm.getValue().lease().approvalDate().isNull()) {
                        LogicalDate expirationDate = item.getValue().expirationDate().getValue();
                        if ((expirationDate != null) && expirationDate.before(ClientContext.getServerDate())) {
                            item.setViewable(true);
                            item.inheritViewable(false);

                            // compensate the fact that item.setViewable DOESN'T call kids' setViewable!?
                            for (CComponent<?> comp : item.getComponents()) {
                                comp.setViewable(true);
                            }
                        }
                    }
                }
            }

            if (isViewable()) {
                adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
                depositPanel.setVisible(!getValue().deposits().isEmpty());
            } else {
                adjustmentPanel.setVisible(true);
                depositPanel.setVisible(true);
            }

        } else {// tweak UI for empty ProductItem:
            adjustmentPanel.setVisible(false);
        }

        get(proto().description()).setVisible(VistaFeatures.instance().yardiIntegration() && (isEditable() || !getValue().description().isNull()));
    }

    @Override
    protected void onValuePropagation(BillableItem value, boolean fireEvent, boolean populate) {
        setExtraDataEditor(value, populate);
        super.onValuePropagation(value, fireEvent, populate);
    }

    @Override
    protected BillableItem preprocessValue(BillableItem value, boolean fireEvent, boolean populate) {
        if (!isValueEmpty()) {
            return super.preprocessValue(value, fireEvent, populate);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private void setExtraDataEditor(BillableItem value, boolean populate) {

        if (this.contains(proto().extraData())) {
            this.unbind(proto().extraData());
            extraDataPanel.setWidget(null);
        }

        if (value != null) {
            @SuppressWarnings("rawtypes")
            CEntityForm editor = null;
            BillableItemExtraData extraData = value.extraData();

            if (extraData.getInstanceValueClass() == YardiLeaseChargeData.class) {
                editor = new YardiDataEditor();
            } else {
                if (ARCode.Type.features().contains(value.item().product().holder().code().type().getValue())) {
                    switch (value.item().product().holder().code().type().getValue()) {
                    case Parking:
                        editor = new VehicleDataEditor();
                        if (extraData.getInstanceValueClass() != Vehicle.class) {
                            extraData.set(EntityFactory.create(Vehicle.class));
                        }
                        break;
                    case Pet:
                        editor = new PetDataEditor();
                        if (extraData.getInstanceValueClass() != Pet.class) {
                            extraData.set(EntityFactory.create(Pet.class));
                        }
                        break;
                    default:
                        // ok - there is no extra-data for other types!.. 
                    }
                }
            }

            if (editor != null) {
                this.inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        }
    }

    private boolean isMandatoryFeature(Product.ProductV<?> product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).mandatory().isBooleanTrue();
    }

    private boolean isRecurringFeature(Product.ProductV<?> product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).recurring().isBooleanTrue();
    }

    private class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        public AdjustmentFolder() {
            super(BillableItemAdjustment.class, i18n.tr("Adjustment"), !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().type(), "9em"),
                new EntityFolderColumnDescriptor(proto().value(), "5em"),
                new EntityFolderColumnDescriptor(proto().effectiveDate(), "9em"),
                new EntityFolderColumnDescriptor(proto().expirationDate(), "10em"));
            //@formatter:on
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof BillableItemAdjustment) {
                return new BillableItemAdjustmentEditor();
            }
            return super.create(member);
        }

        private class BillableItemAdjustmentEditor extends CEntityFolderRowEditor<BillableItemAdjustment> {

            public BillableItemAdjustmentEditor() {
                super(BillableItemAdjustment.class, columns());
            }

            @SuppressWarnings("unchecked")
            @Override
            protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                CComponent<?> comp = super.createCell(column);

                if (column.getObject() == proto().type()) {
                    ((CComponent<Type>) comp).addValueChangeHandler(new ValueChangeHandler<Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Type> event) {
                            bindValueEditor(event.getValue(), false);
                        }
                    });
                }

                return comp;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                bindValueEditor(getValue().type().getValue(), populate);
            }

            private void bindValueEditor(Type valueType, boolean populate) {
                CComponent<?> comp = null;
                if (valueType != null) {
                    switch (valueType) {
                    case monetary:
                        comp = new CMoneyField();
                        break;
                    case percentage:
                        comp = new CPercentageField();
                        break;
                    }
                }

                if (comp != null) {
                    @SuppressWarnings("unchecked")
                    IDecorator<CComponent<BigDecimal>> decor = get((proto().value())).getDecorator();
                    unbind(proto().value());
                    inject(proto().value(), comp);
                    comp.setDecorator(decor);

                    if (populate) {
                        get(proto().value()).populate(getValue().value().getValue(BigDecimal.ZERO));
                    }
                }
            }

            @Override
            public void addValidations() {
                super.addValidations();

                new StartEndDateValidation(get(proto().effectiveDate()), get(proto().expirationDate()));

                get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(itemEffectiveDateEditor));
                itemEffectiveDateEditor.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));

                get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));
                get(proto().effectiveDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
                    @Override
                    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                        if (value != null) {
                            if (leaseTerm.getValue().lease().status().getValue() != Lease.Status.ExistingLease && (itemEffectiveDateEditor.getValue() != null)
                                    && value.before(ClientContext.getServerDate())) {
                                return new ValidationError(component, "The date should not precede the today's date");
                            }
                            if ((itemEffectiveDateEditor.getValue() != null) && value.before(itemEffectiveDateEditor.getValue())) {
                                return new ValidationError(component, "The date should not precede the Item Effective date");
                            }
                            if ((leaseTerm.getValue().termFrom().getValue() != null) && value.before(leaseTerm.getValue().termFrom().getValue())) {
                                return new ValidationError(component, "The date should not precede the Lease Start date");
                            }
                        }
                        return null;
                    }
                });

                get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(itemExpirationDateEditor));
                itemExpirationDateEditor.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));

                get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));
                get(proto().expirationDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
                    @Override
                    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                        if (value != null) {
                            if (itemExpirationDateEditor.getValue() != null && value.after(itemExpirationDateEditor.getValue())) {
                                return new ValidationError(component, "The date should not exceed the Item Expiration date");
                            }
                            if (leaseTerm.getValue().termTo().getValue() != null && value.after(leaseTerm.getValue().termTo().getValue())) {
                                return new ValidationError(component, "The date should not exceed the Lease Expiration date");
                            }
                        }
                        return null;
                    }
                });

                get(proto().value()).addValueValidator(new EditableValueValidator<BigDecimal>() {
                    @Override
                    public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                        if (value != null) {
                            if (value.signum() < 0) {
                                // TODO : some validation here...
                            } else {
                                // TODO : some validation here...
                            }
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

        get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));
        get(proto().effectiveDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null) {
                    if (leaseTerm.getValue().termTo().getValue() != null && value.before(leaseTerm.getValue().termFrom().getValue())) {
                        return new ValidationError(component, "The date should not precede the Lease Start date");
                    }
                    for (BillableItemAdjustment a : getValue().adjustments()) {
                        if (a.effectiveDate().getValue() != null && a.effectiveDate().getValue().before(value)) {
                            return new ValidationError(component, "One or more adjustments for this item start before the specificed date");
                        }
                    }
                }
                return null;
            }
        });

        get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));
        get(proto().expirationDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null) {
                    if (leaseTerm.getValue().termTo().getValue() != null && value.after(leaseTerm.getValue().termTo().getValue())) {
                        return new ValidationError(component, "The date should not exceed the Lease Expiration date");
                    }
                    for (BillableItemAdjustment a : getValue().adjustments()) {
                        if (a.expirationDate().getValue() != null && a.expirationDate().getValue().after(value)) {
                            return new ValidationError(component, "One or more adjustments for this item expire after the specificed date");
                        }
                    }
                }
                return null;
            }
        });
    }

    private class DepositFolder extends VistaTableFolder<Deposit> {

        public DepositFolder() {
            super(Deposit.class, i18n.tr("Deposit"), !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().type(), "15em"),
                new EntityFolderColumnDescriptor(proto().amount(), "6em"),
                new EntityFolderColumnDescriptor(proto().description(), "25em"));
            //@formatter:on
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof Deposit) {
                return new DepositEditor();
            }
            return super.create(member);
        }

        private class DepositEditor extends CEntityFolderRowEditor<Deposit> {

            public DepositEditor() {
                super(Deposit.class, columns());
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                // disable editing of finalized deposits:
                setEditable(getValue().lifecycle().isNull());
            }
        }
    }
}