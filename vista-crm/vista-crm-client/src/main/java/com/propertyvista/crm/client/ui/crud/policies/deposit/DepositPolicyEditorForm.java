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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

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
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.crm.rpc.services.selections.SelectProductItemTypeListService;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.RepaymentMode;

public class DepositPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<DepositPolicyDTO> {

    private final static I18n i18n = I18n.get(DepositPolicyEditorForm.class);

    public DepositPolicyEditorForm() {
        this(false);
    }

    public DepositPolicyEditorForm(boolean viewMode) {
        super(DepositPolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createItemsPanel(), i18n.tr("Items"))
        );//@formatter:on
    }

    private Widget createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel();
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

        @Override
        protected void addItem() {
            List<ProductItemType> alreadySelectedProducts = new ArrayList<ProductItemType>();
            for (DepositPolicyItem di : getValue()) {
                if (!di.appliedTo().isNull()) {
                    alreadySelectedProducts.add(di.appliedTo());
                }
            }
            new ProductItemTypeSelectorDialog(alreadySelectedProducts) {

                @Override
                protected void addItem(DepositPolicyItem newItem) {
                    DepositPolicyItemEditorFolder.this.addItem(newItem);
                }

            }.show();
        }

        private static class DepositPolicyItemEditor extends CEntityDecoratableEditor<DepositPolicyItem> {

            private final FormFlexPanel content = new FormFlexPanel();

            private int valueRow;

            public DepositPolicyItemEditor() {
                super(DepositPolicyItem.class);
            }

//            @Override
//            public CComponent<?, ?> create(IObject<?> member) {
//                if (member.getFieldName() == proto().appliedTo().getFieldName()) {
//                    unbind(proto().appliedTo());
//                    return inject(proto().appliedTo(), new CLabel());
//                } else {
//                    return super.create(member);
//                }
//            }

            @Override
            public IsWidget createContent() {
                //FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().appliedTo()), 20).build());
                get(proto().appliedTo()).inheritViewable(false);
                get(proto().appliedTo()).setViewable(true);

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().valueType()), 10).build());
                valueRow = ++row;

                content.getFlexCellFormatter().setColSpan(valueRow, 0, 2);

                row = -1;
                content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().description()), 20).build());
                content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().repaymentMode()), 20).build());

                get(proto().valueType()).addValueChangeHandler(new ValueChangeHandler<Deposit.ValueType>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<Deposit.ValueType> event) {
                        bindValueEditor(event.getValue(), false);
                    }
                });

                content.getColumnFormatter().setWidth(0, "50%");
                content.getColumnFormatter().setWidth(1, "50%");

                return content;
            }

            @Override
            protected void onPopulate() {
                super.onPopulate();

                bindValueEditor(getValue().valueType().getValue(), true);
            }

            private void bindValueEditor(Deposit.ValueType valueType, boolean repopulatevalue) {
                CComponent<?, ?> comp = null;

                if (valueType == Deposit.ValueType.amount) {
                    comp = new CMoneyField();

                } else if (valueType == Deposit.ValueType.percentage) {
                    comp = new CPercentageField();
                }

                if (comp != null) {
                    unbind(proto().value());
                    content.setWidget(valueRow, 0, new DecoratorBuilder(inject(proto().value(), comp), 10).build());

                    if (repopulatevalue) {
                        get(proto().value()).populate(getValue().value().getValue());
                    }
                }
            }

        }
    }

    private static abstract class ProductItemTypeSelectorDialog extends EntitySelectorDialog<ProductItemType> {

        public ProductItemTypeSelectorDialog(List<ProductItemType> alreadySelectedProducts) {
            super(ProductItemType.class, false, alreadySelectedProducts, i18n.tr("Select Product Item Type"));
        }

        @Override
        public boolean onClickOk() {
            List<ProductItemType> productItemTypes = getSelectedItems();

            if (!productItemTypes.isEmpty()) {
                for (ProductItemType type : productItemTypes) {
                    DepositPolicyItem newItem = EntityFactory.create(DepositPolicyItem.class);
                    newItem.repaymentMode().setValue(RepaymentMode.returnAtLeaseEnd);
                    newItem.appliedTo().set(type);
                    addItem(newItem);
                }
                return true;
            } else {
                return false;
            }

        }

        protected abstract void addItem(DepositPolicyItem newItem);

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().name()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<ProductItemType> getSelectService() {
            return GWT.<AbstractListService<ProductItemType>> create(SelectProductItemTypeListService.class);
        }
    };

}
