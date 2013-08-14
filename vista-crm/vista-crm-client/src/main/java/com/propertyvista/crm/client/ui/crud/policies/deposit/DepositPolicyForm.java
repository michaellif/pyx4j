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
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.rpc.services.selections.SelectProductCodeListService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.ValueType;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public class DepositPolicyForm extends PolicyDTOTabPanelBasedForm<DepositPolicyDTO> {

    private final static I18n i18n = I18n.get(DepositPolicyForm.class);

    public DepositPolicyForm(IForm<DepositPolicyDTO> view) {
        super(DepositPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private TwoColumnFlexFormPanel createItemsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Items"));
        int row = -1;

        panel.setWidget(++row, 0, 2, inject(proto().policyItems(), new DepositPolicyItemEditorFolder()));

        return panel;
    }

    private static class DepositPolicyItemEditorFolder extends VistaBoxFolder<DepositPolicyItem> {

        public DepositPolicyItemEditorFolder() {
            super(DepositPolicyItem.class);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
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
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

                int row = -1;
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().depositType())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().productCode())).build());
                get(proto().productCode()).setEditable(false);
                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());

                row = -1;
                content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().valueType())).build());
                content.setWidget(++row, 1, valueHolder);

                get(proto().valueType()).addValueChangeHandler(new ValueChangeHandler<ValueType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<ValueType> event) {
                        bindValueEditor(event.getValue(), false);
                    }
                });

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

                CComponent<?> comp = null;
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
                    valueHolder.setWidget(new FormDecoratorBuilder(inject(proto().value(), comp), 6).build());

                    if (repopulatevalue) {
                        get(proto().value()).populate(getValue().value().getValue(BigDecimal.ZERO));
                    }
                }
            }
        }

        @Override
        protected void addItem() {
            final List<ARCode> alreadySelectedProducts = new ArrayList<ARCode>();
            for (DepositPolicyItem di : getValue()) {
                if (!di.productCode().isNull()) {
                    alreadySelectedProducts.add(di.productCode());
                }
            }

            new ProductSelectorDialog(alreadySelectedProducts).show();
        }

        private class ProductSelectorDialog extends EntitySelectorTableDialog<ARCode> {

            public ProductSelectorDialog(List<ARCode> alreadySelectedProducts) {
                super(ARCode.class, false, alreadySelectedProducts, i18n.tr("Select Product Type"));
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (ARCode code : getSelectedItems()) {
                        DepositPolicyItem newItem = EntityFactory.create(DepositPolicyItem.class);
                        newItem.depositType().setValue(DepositType.SecurityDeposit);
                        newItem.productCode().set(code);
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
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListService<ARCode> getSelectService() {
                return GWT.<AbstractListService<ARCode>> create(SelectProductCodeListService.class);
            }
        }
    }
}
