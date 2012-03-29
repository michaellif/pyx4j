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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
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
        protected void addItem(DepositPolicyItem newEntity) {
            if (newEntity.isEmpty()) {
                newEntity.repaymentMode().setValue(RepaymentMode.returnAtLeaseEnd);
            }

            super.addItem(newEntity);
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
}
