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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.decorators.IFieldDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
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
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.shared.config.VistaFeatures;

public class BillableItemViewer extends CForm<BillableItem> {

    static final I18n i18n = I18n.get(BillableItemViewer.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final BasicCFormPanel adjustmentPanel = new BasicCFormPanel(this);

    private final BasicCFormPanel depositPanel = new BasicCFormPanel(this);

    public BillableItemViewer() {
        super(BillableItem.class);
        setEditable(false);
        setViewable(true);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().item(), new CEntityLabel<ProductItem>()).decorate();
        ((CEntityLabel<ProductItem>) get(proto().item())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(getTargetPlace());
            }

            protected AppPlace getTargetPlace() {
                if (getValue().item().product().isInstanceOf(Service.ServiceV.class)) {
                    Service service = ((Service.ServiceV) getValue().item().product().cast()).holder();
                    return AppPlaceEntityMapper.resolvePlace(Service.class, service.getPrimaryKey());
                } else if (getValue().item().product().isInstanceOf(Feature.FeatureV.class)) {
                    Feature feature = ((Feature.FeatureV) getValue().item().product().cast()).holder();
                    return AppPlaceEntityMapper.resolvePlace(Feature.class, feature.getPrimaryKey());
                } else {
                    throw new IllegalArgumentException("Incorrect Product value!");
                }
            }
        });

        formPanel.append(Location.Right, proto().agreedPrice()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().effectiveDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().expirationDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().yardiChargeCode()).decorate().componentWidth(120);

        formPanel.append(Location.Full, proto().description()).decorate();

        formPanel.append(Location.Full, extraDataPanel);

        formPanel.append(Location.Full, adjustmentPanel);

        formPanel.append(Location.Full, depositPanel);

        adjustmentPanel.h3(proto().adjustments().getMeta().getCaption());
        adjustmentPanel.append(Location.Full, proto().adjustments(), new AdjustmentFolder());

        depositPanel.h3(proto().deposits().getMeta().getCaption());
        depositPanel.append(Location.Full, proto().deposits(), new DepositFolder());

        get(proto().yardiChargeCode()).setVisible(false);
        get(proto().effectiveDate()).setVisible(false);
        get(proto().expirationDate()).setVisible(false);

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // tweak UI for ProductItem:
        if (VistaFeatures.instance().yardiIntegration()) {

            get(proto().item()).setVisible(!getValue().item().isNull());
            get(proto().yardiChargeCode()).setVisible(!getValue().yardiChargeCode().isNull());

            get(proto().effectiveDate()).setVisible(!getValue().effectiveDate().isNull());
            get(proto().effectiveDate()).setTooltip(null);

            get(proto().expirationDate()).setVisible(!getValue().expirationDate().isNull());
            get(proto().expirationDate()).setTooltip(null);

            adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            depositPanel.setVisible(!getValue().deposits().isEmpty());

        } else if (!getValue().item().isEmpty()) {
            if (getValue().item().product().isInstanceOf(Service.ServiceV.class)) {
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);
            } else if (getValue().item().product().isInstanceOf(Feature.FeatureV.class)) {
                // show/hide effective dates (hide expiration for non-recurring; show in editor, hide in viewer if empty):
                boolean recurring = isRecurringFeature(getValue().item().product());
                get(proto().effectiveDate()).setVisible(!getValue().effectiveDate().isNull());
                get(proto().expirationDate()).setVisible(recurring && !getValue().expirationDate().isNull());
            }

            adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            depositPanel.setVisible(!getValue().deposits().isEmpty());

        } else {// tweak UI for empty ProductItem:
            adjustmentPanel.setVisible(false);
        }

        get(proto().description()).setVisible(!getValue().description().isNull());
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

    private boolean isMandatoryFeature(Product.ProductV product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).mandatory().getValue(false);
    }

    private boolean isRecurringFeature(Product.ProductV product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).recurring().getValue(false);
    }

    private class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        public AdjustmentFolder() {
            super(BillableItemAdjustment.class, i18n.tr("Adjustment"), false);
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
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                bindValueEditor(getValue().type().getValue(), populate);
            }

            private void bindValueEditor(Type valueType, boolean populate) {
                CField<BigDecimal, ?> comp = null;
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
                    IFieldDecorator decor = (IFieldDecorator) get((proto().value())).getDecorator();
                    unbind(proto().value());
                    inject(proto().value(), comp);
                    comp.setDecorator(decor);

                    if (populate) {
                        get(proto().value()).populate(getValue().value().getValue(BigDecimal.ZERO));
                    }
                }
            }
        }
    }

    private class DepositFolder extends VistaTableFolder<Deposit> {

        public DepositFolder() {
            super(Deposit.class, i18n.tr("Deposit"), false);
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
        protected CForm<Deposit> createItemForm(IObject<?> member) {
            return new DepositEditor();
        }

        private class DepositEditor extends CFolderRowEditor<Deposit> {

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