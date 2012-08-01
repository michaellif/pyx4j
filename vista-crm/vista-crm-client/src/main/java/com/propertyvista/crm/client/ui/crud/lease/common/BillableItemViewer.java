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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

public class BillableItemViewer extends CEntityDecoratableForm<BillableItem> {

    static final I18n i18n = I18n.get(BillableItemViewer.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

    private final FormFlexPanel depositPanel = new FormFlexPanel();

    public BillableItemViewer() {
        super(BillableItem.class);
        setEditable(false);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item(), new CEntityHyperlink<ProductItem>(new Command() {
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
                    return null;
                }
            }
        })), 25).build());

        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().agreedPrice()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().effectiveDate()), 9).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().expirationDate()), 9).build());

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, adjustmentPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, depositPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        adjustmentPanel.setH3(0, 0, 1, proto().adjustments().getMeta().getCaption());
        adjustmentPanel.setWidget(1, 0, inject(proto().adjustments(), new AdjustmentFolder()));

        depositPanel.setH3(0, 0, 1, proto().deposits().getMeta().getCaption());
        depositPanel.setWidget(1, 0, inject(proto().deposits(), new DepositFolder()));

        get(proto().effectiveDate()).setVisible(false);
        get(proto().expirationDate()).setVisible(false);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // tweak UI for ProductItem:
        if (!getValue().item().isEmpty()) {
            if (getValue().item().type().isInstanceOf(ServiceItemType.class)) {
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);
            } else if (getValue().item().type().isInstanceOf(FeatureItemType.class)) {
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

        if (getValue() != null && getValue().item().type().isInstanceOf(FeatureItemType.class)) {
            @SuppressWarnings("rawtypes")
            CEntityForm editor = null;
            BillableItemExtraData extraData = value.extraData();

            switch (getValue().item().type().<FeatureItemType> cast().featureType().getValue()) {
            case parking:
                editor = new VehicleDataEditor();
                if (extraData.getInstanceValueClass() != Vehicle.class) {
                    extraData.set(EntityFactory.create(Vehicle.class));
                }
                break;
            case pet:
                editor = new PetDataEditor();
                if (extraData.getInstanceValueClass() != Pet.class) {
                    extraData.set(EntityFactory.create(Pet.class));
                }
                break;
            default:
                // ok - there is no extra-data for other types!.. 
            }

            if (editor != null) {
                this.inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        }
    }

    private boolean isMandatoryFeature(Product.ProductV product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).mandatory().isBooleanTrue();
    }

    private boolean isRecurringFeature(Product.ProductV product) {
        return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).recurring().isBooleanTrue();
    }

    private class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        public AdjustmentFolder() {
            super(BillableItemAdjustment.class, i18n.tr("Adjustment"), false);
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
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof BillableItemAdjustment) {
                return new BillableItemAdjustmentEditor();
            }
            return super.create(member);
        }

        private class BillableItemAdjustmentEditor extends CEntityFolderRowEditor<BillableItemAdjustment> {

            public BillableItemAdjustmentEditor() {
                super(BillableItemAdjustment.class, columns());
            }

            @Override
            protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                if (column.getObject() == proto().value()) {
                    // TODO : inject value place holder here:
                    return super.createCell(column);
                }
                return super.createCell(column);
            }
        }
    }

    private class DepositFolder extends VistaTableFolder<Deposit> {

        public DepositFolder() {
            super(Deposit.class, i18n.tr("Deposit"), false);
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
        public CComponent<?, ?> create(IObject<?> member) {
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