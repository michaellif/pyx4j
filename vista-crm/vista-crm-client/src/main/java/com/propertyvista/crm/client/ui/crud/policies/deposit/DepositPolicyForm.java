/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.deposit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.rpc.services.selections.SelectFeatureItemTypeListService;
import com.propertyvista.crm.rpc.services.selections.SelectServiceItemTypeListService;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.ValueType;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public class DepositPolicyForm extends PolicyDTOTabPanelBasedForm<DepositPolicyDTO> {

    private final static I18n i18n = I18n.get(DepositPolicyForm.class);

    public DepositPolicyForm() {
        this(false);
    }

    public DepositPolicyForm(boolean viewMode) {
        super(DepositPolicyDTO.class, viewMode);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private FormFlexPanel createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Items"));
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().policyItems(), new DepositPolicyItemEditorFolder()));

        return panel;
    }

    private static class DepositPolicyItemEditorFolder extends VistaBoxFolder<DepositPolicyItem> {

        public DepositPolicyItemEditorFolder() {
            super(DepositPolicyItem.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof DepositPolicyItem) {
                return new DepositPolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        private static class DepositPolicyItemEditor extends CEntityDecoratableForm<DepositPolicyItem> {

            private final SimplePanel valueHolder = new SimplePanel();

            public DepositPolicyItemEditor() {
                super(DepositPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositType()), 20).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productType()), 20).build());
                get(proto().productType()).inheritViewable(false);
                get(proto().productType()).setViewable(true);

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().valueType()), 10).build());
                content.setWidget(++row, 0, valueHolder);

                row = -1;
                content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().description()), 20).build());

                get(proto().valueType()).addValueChangeHandler(new ValueChangeHandler<ValueType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<ValueType> event) {
                        bindValueEditor(event.getValue(), false);
                    }
                });

                content.getColumnFormatter().setWidth(0, "50%");
                content.getColumnFormatter().setWidth(1, "50%");

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                bindValueEditor(getValue().valueType().getValue(), true);
            }

            private void bindValueEditor(ValueType valueType, boolean repopulatevalue) {
                if (valueType == null)
                    return; // New item

                CComponent<?, ?> comp = null;
                switch (valueType) {
                case Monetary:
                    comp = new CMoneyField();
                    break;
                case Percentage:
                    comp = new CPercentageField();
                    break;
                }

                unbind(proto().value());

                if (comp != null) {
                    valueHolder.setWidget(new DecoratorBuilder(inject(proto().value(), comp), 6).build());

                    if (repopulatevalue) {
                        get(proto().value()).populate(getValue().value().getValue(BigDecimal.ZERO));
                    }
                }
            }
        }

        @Override
        protected void addItem() {
            final List<ProductItemType> alreadySelectedProducts = new ArrayList<ProductItemType>();
            for (DepositPolicyItem di : getValue()) {
                if (!di.productType().isNull()) {
                    alreadySelectedProducts.add(di.productType());
                }
            }

            new SelectEnumDialog<ProductItemType.Type>(i18n.tr("Select Product Type"), EnumSet.allOf(ProductItemType.Type.class)) {
                @Override
                public boolean onClickOk() {
                    switch (getSelectedType()) {
                    case Service:
                        new ProductItemTypeSelectorDialog<ServiceItemType>(ServiceItemType.class, alreadySelectedProducts).show();
                        break;
                    case Feature:
                        new ProductItemTypeSelectorDialog<FeatureItemType>(FeatureItemType.class, alreadySelectedProducts).show();
                        break;
                    default:
                        throw new Error();
                    }
                    return true;
                }

                @Override
                public String defineWidth() {
                    return "20em";
                }
            }.show();
        }

        private class ProductItemTypeSelectorDialog<PIT extends ProductItemType> extends EntitySelectorTableDialog<PIT> {

            public ProductItemTypeSelectorDialog(Class<PIT> productClass, List alreadySelectedProducts) {
                super(productClass, false, alreadySelectedProducts, i18n.tr("Select Product Type"));
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (ProductItemType type : getSelectedItems()) {
                        DepositPolicyItem newItem = EntityFactory.create(DepositPolicyItem.class);
                        newItem.depositType().setValue(DepositType.SecurityDeposit);
                        newItem.productType().set(type);
                        DepositPolicyItemEditorFolder.this.addItem(newItem);
                    }
                    return true;
                }
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build()
                );//@formatter:on
            }

            @Override
            protected AbstractListService<PIT> getSelectService() {
                if (proto() instanceof ServiceItemType) {
                    return GWT.<AbstractListService<PIT>> create(SelectServiceItemTypeListService.class);
                } else if (proto() instanceof FeatureItemType) {
                    return GWT.<AbstractListService<PIT>> create(SelectFeatureItemTypeListService.class);
                } else {
                    throw new Error();
                }
            }
        }
    }
}
