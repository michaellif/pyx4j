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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyPercentCombo;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class BillableItemEditor extends CForm<BillableItem> {

    static final I18n i18n = I18n.get(BillableItemEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormPanel adjustmentPanel = new FormPanel(this);

    private final FormPanel depositPanel = new FormPanel(this);

    private final CForm<LeaseTermDTO> leaseTerm;

    private final LeaseTermEditorView leaseTermEditorView;

    private CComponent<?, LogicalDate, ?> itemEffectiveDateEditor;

    private CComponent<?, LogicalDate, ?> itemExpirationDateEditor;

    public BillableItemEditor(CForm<LeaseTermDTO> leaseTerm, LeaseTermEditorView leaseTermEditorView) {
        super(BillableItem.class);
        this.leaseTerm = leaseTerm;
        this.leaseTermEditorView = leaseTermEditorView;
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().item(), new CEntitySelectorHyperlink<ProductItem>() {
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
        }).decorate();

        formPanel.append(Location.Left, proto().agreedPrice()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().effectiveDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().expirationDate()).decorate().componentWidth(120);

        formPanel.append(Location.Dual, proto().description()).decorate();
        formPanel.append(Location.Dual, extraDataPanel);
        formPanel.append(Location.Dual, adjustmentPanel);
        formPanel.append(Location.Dual, depositPanel);

        if (!VistaFeatures.instance().yardiIntegration()) {
            adjustmentPanel.h3(proto().adjustments().getMeta().getCaption());
            adjustmentPanel.append(Location.Dual, proto().adjustments(), new AdjustmentFolder());
        }

        depositPanel.h3(proto().deposits().getMeta().getCaption());
        depositPanel.append(Location.Dual, proto().deposits(), new DepositFolder());

        itemEffectiveDateEditor = get(proto().effectiveDate());
        itemExpirationDateEditor = get(proto().expirationDate());

        get(proto().effectiveDate()).setVisible(false);
        get(proto().expirationDate()).setVisible(false);

        return formPanel;
    }

    @Override
    public void onReset() {
        super.onReset();

        if (isEditable()) {
            get(proto().item()).setEditable(true);

            get(proto().agreedPrice()).setEditable(false);
            get(proto().agreedPrice()).setMandatory(false);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // tweak UI for ProductItem:
        if (!getValue().item().isEmpty()) {
            if (getValue().item().product().isInstanceOf(Service.ServiceV.class)) {
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);

                if (isEditable()) {
                    boolean isLeaseApproved = !leaseTerm.getValue().lease().approvalDate().isNull();
                    // set editable for non-approved leases (and multiple service items):
                    get(proto().item()).setEditable(!isLeaseApproved && leaseTerm.getValue().selectedServiceItems().size() > 1);
                    get(proto().agreedPrice()).setEditable(!isLeaseApproved);
                    get(proto().agreedPrice()).setMandatory(!leaseTerm.getValue().unit().isNull());
                }
            } else if (getValue().item().product().isInstanceOf(Feature.FeatureV.class)) {
                // show/hide effective dates (hide expiration for non-recurring; show in editor, hide in viewer if empty):
                boolean recurring = isRecurringFeature(getValue().item().product());
                get(proto().effectiveDate()).setVisible((isEditable() || !getValue().effectiveDate().isNull()));
                get(proto().expirationDate()).setVisible(recurring && (isEditable() || !getValue().expirationDate().isNull()));

                if (isEditable()) {
                    get(proto().item()).setEditable(false);
                    get(proto().agreedPrice()).setEditable(!getValue().finalized().getValue(false));
                    get(proto().agreedPrice()).setMandatory(true);
                }

                // correct folder item:
                if (getParent() instanceof CFolderItem) {
                    CFolderItem<BillableItem> item = (CFolderItem<BillableItem>) getParent();

                    item.setRemovable(!isMandatoryFeature(getValue().item().product()));

                    // if lease is approved:
                    if (!leaseTerm.getValue().lease().approvalDate().isNull()) {
                        LogicalDate expirationDate = item.getValue().expirationDate().getValue();
                        if ((expirationDate != null) && expirationDate.before(ClientContext.getServerDate())) {
                            item.setViewable(true);
                            item.inheritViewable(false);

                            // compensate the fact that item.setViewable DOESN'T call kids' setViewable!?
                            for (CComponent<?, ?, ?> comp : item.getComponents()) {
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

            if (isEditable()) {
                get(proto().item()).setEditable(!leaseTerm.getValue().selectedServiceItems().isEmpty());

                get(proto().agreedPrice()).setEditable(false);
                get(proto().agreedPrice()).setMandatory(false);
            }
        }

        get(proto().description()).setVisible(isEditable() || !getValue().description().isNull());

        // Yardi mode correction:
        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().item()).setVisible(isEditable() || !getValue().item().isNull());

            get(proto().agreedPrice()).setEditable(false);
            get(proto().agreedPrice()).setMandatory(false);

//            get(proto().effectiveDate()).setVisible(!getValue().effectiveDate().isNull());
//            get(proto().expirationDate()).setVisible(!getValue().expirationDate().isNull());
            get(proto().effectiveDate()).setVisible(false);
            get(proto().expirationDate()).setVisible(false);

//            adjustmentPanel.setVisible((isEditable() || !getValue().adjustments().isEmpty()));
            adjustmentPanel.setVisible(false);

            if (getValue().item().product().isInstanceOf(Service.ServiceV.class)) {
                depositPanel.setVisible((isEditable() || !getValue().deposits().isEmpty()));
            } else if (getValue().item().product().isInstanceOf(Feature.FeatureV.class)) {
                depositPanel.setVisible(false);
            }
        }
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
            CForm editor = null;
            BillableItemExtraData extraData = value.extraData();

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

            if (editor != null) {
                this.inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        }
    }

    private boolean isMandatoryFeature(Product.ProductV<?> product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).mandatory().getValue(false);
    }

    private boolean isRecurringFeature(Product.ProductV<?> product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).recurring().getValue(false);
    }

    private class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        public AdjustmentFolder() {
            super(BillableItemAdjustment.class, i18n.tr("Adjustment"), !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().type(), "9em"),
                new FolderColumnDescriptor(proto().value(), "5em"),
                new FolderColumnDescriptor(proto().effectiveDate(), "9em"),
                new FolderColumnDescriptor(proto().expirationDate(), "10em"));
            //@formatter:on
        }

        @Override
        protected CForm<? extends BillableItemAdjustment> createItemForm(IObject<?> member) {
            return new BillableItemAdjustmentEditor();
        }

        private class BillableItemAdjustmentEditor extends CFolderRowEditor<BillableItemAdjustment> {

            public BillableItemAdjustmentEditor() {
                super(BillableItemAdjustment.class, columns());
            }

            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().value()) {
                    return inject(column.getObject(), new CMoneyPercentCombo());
                } else {
                    return super.createCell(column);
                }
            }

            @Override
            protected IsWidget createContent() {
                IsWidget content = super.createContent();
                final CMoneyPercentCombo moneyPct = (CMoneyPercentCombo) get(proto().value());
                get(proto().type()).addPropertyChangeHandler(new PropertyChangeHandler() {
                    @Override
                    public void onPropertyChange(PropertyChangeEvent event) {
                        moneyPct.setAmountType(get(proto().type()).getValue());
                    }
                });
                return content;
            }

            @Override
            public void addValidations() {
                super.addValidations();

                new StartEndDateValidation(get(proto().effectiveDate()), get(proto().expirationDate()));

                get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(itemEffectiveDateEditor));
                itemEffectiveDateEditor.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));

                get(proto().effectiveDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));
                get(proto().effectiveDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                    @Override
                    public BasicValidationError isValid() {
                        if (getComponent().getValue() != null) {
                            if (leaseTerm.getValue().lease().status().getValue() != Lease.Status.ExistingLease && (itemEffectiveDateEditor.getValue() != null)
                                    && getComponent().getValue().before(ClientContext.getServerDate())) {
                                return new BasicValidationError(getComponent(), "The date should not precede the today's date");
                            }
                            if ((itemEffectiveDateEditor.getValue() != null) && getComponent().getValue().before(itemEffectiveDateEditor.getValue())) {
                                return new BasicValidationError(getComponent(), "The date should not precede the Item Effective date");
                            }
                            if ((leaseTerm.getValue().termFrom().getValue() != null)
                                    && getComponent().getValue().before(leaseTerm.getValue().termFrom().getValue())) {
                                return new BasicValidationError(getComponent(), "The date should not precede the Lease Start date");
                            }
                        }
                        return null;
                    }
                });

                get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(itemExpirationDateEditor));
                itemExpirationDateEditor.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expirationDate())));

                get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));
                get(proto().expirationDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                    @Override
                    public BasicValidationError isValid() {
                        if (getComponent().getValue() != null) {
                            if (itemExpirationDateEditor.getValue() != null && getComponent().getValue().after(itemExpirationDateEditor.getValue())) {
                                return new BasicValidationError(getComponent(), "The date should not exceed the Item Expiration date");
                            }
                            if (leaseTerm.getValue().termTo().getValue() != null && getComponent().getValue().after(leaseTerm.getValue().termTo().getValue())) {
                                return new BasicValidationError(getComponent(), "The date should not exceed the Lease Expiration date");
                            }
                        }
                        return null;
                    }
                });

                get(proto().value()).addComponentValidator(new AbstractComponentValidator<IMoneyPercentAmount>() {
                    @Override
                    public BasicValidationError isValid() {
                        if (getComponent().getValue() != null) {
                            // TODO : some validation here...
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
        get(proto().effectiveDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (leaseTerm.getValue().termTo().getValue() != null && getComponent().getValue().before(leaseTerm.getValue().termFrom().getValue())) {
                        return new BasicValidationError(getComponent(), "The date should not precede the Lease Start date");
                    }
                    for (BillableItemAdjustment a : getValue().adjustments()) {
                        if (a.effectiveDate().getValue() != null && a.effectiveDate().getValue().before(getComponent().getValue())) {
                            return new BasicValidationError(getComponent(), "One or more adjustments for this item start before the specified date");
                        }
                    }
                }
                return null;
            }
        });

        get(proto().expirationDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().effectiveDate())));
        get(proto().expirationDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (leaseTerm.getValue().termTo().getValue() != null && getComponent().getValue().after(leaseTerm.getValue().termTo().getValue())) {
                        return new BasicValidationError(getComponent(), "The date should not exceed the Lease Expiration date");
                    }
                    for (BillableItemAdjustment a : getValue().adjustments()) {
                        if (a.expirationDate().getValue() != null && a.expirationDate().getValue().after(getComponent().getValue())) {
                            return new BasicValidationError(getComponent(), "One or more adjustments for this item expire after the specificed date");
                        }
                    }
                }
                return null;
            }
        });
    }

    private class DepositFolder extends VistaTableFolder<Deposit> {

        public DepositFolder() {
            super(Deposit.class, i18n.tr("Deposit"), !VistaFeatures.instance().yardiIntegration() && !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().type(), "15em"),
                new FolderColumnDescriptor(proto().amount(), "6em"),
                new FolderColumnDescriptor(proto().description(), "25em"));
            //@formatter:on
        }

        @Override
        protected void addItem() {
            assert (leaseTermEditorView != null);
            ((LeaseTermEditorView.Presenter) leaseTermEditorView.getPresenter()).retirveAvailableDeposits(new DefaultAsyncCallback<List<Deposit>>() {
                @Override
                public void onSuccess(List<Deposit> result) {
                    new EntitySelectorListDialog<Deposit>(i18n.tr("Select Deposits"), true, result) {
                        @Override
                        public boolean onClickOk() {
                            for (Deposit item : getSelectedItems()) {
                                addItem(item);
                            }
                            return true;
                        }
                    }.show();
                }
            }, BillableItemEditor.this.getValue());
        }

        @Override
        protected CForm<? extends Deposit> createItemForm(IObject<?> member) {
            return new DepositEditor();
        }

        private class DepositEditor extends CFolderRowEditor<Deposit> {

            public DepositEditor() {
                super(Deposit.class, columns());

                if (VistaFeatures.instance().yardiIntegration()) {
                    setEditable(false);
                }
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                if (!VistaFeatures.instance().yardiIntegration()) {
                    // disable editing of finalized deposits:
                    setEditable(getValue().lifecycle().isNull());
                }
            }

            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().type()) {
                    return inject(column.getObject(), new CEnumLabel());
                }

                CField<?, ?> comp = super.createCell(column);

                if (VistaFeatures.instance().yardiIntegration()) {
                    if (column.getObject() == proto().description()) {
                        comp.setEditable(true);
                        comp.inheritEditable(false);
                    }
                }

                return comp;
            }
        }
    }
}