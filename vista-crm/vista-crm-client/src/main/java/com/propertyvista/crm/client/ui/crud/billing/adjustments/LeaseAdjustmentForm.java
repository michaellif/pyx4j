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
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.TaxType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentForm extends CrmEntityForm<LeaseAdjustment> {

    private static final I18n i18n = I18n.get(LeaseAdjustmentForm.class);

    private final SimplePanel taxHolder = new SimplePanel();

    public LeaseAdjustmentForm() {
        this(false);
    }

    public LeaseAdjustmentForm(boolean viewMode) {
        super(LeaseAdjustment.class, viewMode);
    }

    @Override
    public void createTabs() {

        FormFlexPanel left = new FormFlexPanel();
        int row = -1;

        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reason(), new CEntitySelectorHyperlink<LeaseAdjustmentReason>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(LeaseAdjustmentReason.class, getValue().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<LeaseAdjustmentReason> getSelectorDialog() {
                return new LeaseAdjustmentReasonSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            get(LeaseAdjustmentForm.this.proto().reason()).setValue(getSelectedItems().get(0));
                            recalculateTaxesAndTotal();
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionType()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().targetDate()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().receivedDate()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());

        FormFlexPanel right = new FormFlexPanel();
        row = -1;
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().overwriteDefaultTax()), 5).build());
        right.setWidget(++row, 0, taxHolder);
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().taxType()), 10).build());
        if (!isEditable()) {
            right.setBR(++row, 0, 2);
            right.setBR(++row, 0, 2);
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto()._total()), 10).build());
        }

        // tweak:
        get(proto().receivedDate()).setViewable(true);
        get(proto().executionType()).addValueChangeHandler(new ValueChangeHandler<ExecutionType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ExecutionType> event) {
                get(proto().targetDate()).setEditable(event.getValue() != ExecutionType.immediate);
                if (event.getValue() == ExecutionType.immediate) {
                    get(proto().targetDate()).setValue(new LogicalDate());
                }
            }
        });

        get(proto().overwriteDefaultTax()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                recalculateTaxesAndTotal();
            }
        });

        get(proto().taxType()).addValueChangeHandler(new ValueChangeHandler<TaxType>() {
            @Override
            public void onValueChange(ValueChangeEvent<TaxType> event) {
                bindValueEditor(event.getValue(), false);
            }
        });

        FormFlexPanel content = new FormFlexPanel();

        content.setWidget(0, 0, left);
        content.setWidget(0, 1, right);
        content.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        content.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        if (!isEditable()) {
            row = 0;
            content.setHR(++row, 0, 2);
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created()), 10).build());
            content.setWidget(row, 1, new DecoratorBuilder(inject(proto().updated()), 10).build());

            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createdBy()), 25).build());
            content.setWidget(row, 1, new DecoratorBuilder(inject(proto().status()), 10).build());
        }

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        selectTab(addTab(content, i18n.tr("General")));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().targetDate()).addValueValidator(new FutureDateValidator());
    }

    @Override
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

        bindValueEditor(getValue().taxType().getValue(), true);
        recalculateTaxesAndTotal();
    }

    private void bindValueEditor(TaxType valueType, boolean repopulate) {
        CComponent<?, ?> comp = null;
        if (valueType != null) {
            switch (valueType) {
            case value:
                comp = new CMoneyField();
                break;
            case percent:
                comp = new CPercentageField();
                break;
            }
        }

        unbind(proto().tax());

        if (comp != null) {
            taxHolder.setWidget(new DecoratorBuilder(inject(proto().tax(), comp), 10).build());

            if (repopulate) {
                get(proto().tax()).populate(getValue().tax().getValue(BigDecimal.ZERO));
            }
        }
    }

    private void recalculateTaxesAndTotal() {
        if (getValue().overwriteDefaultTax().isBooleanTrue()) {
            get(proto().tax()).setEditable(true);
            get(proto().taxType()).setEditable(true);

            recalculateTotal();
        } else {
            bindValueEditor(TaxType.percent, false);
            get(proto().taxType()).populate(TaxType.percent);

            get(proto().tax()).setEditable(false);
            get(proto().taxType()).setEditable(false);

            ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), LeaseAdjustmentPolicy.class,
                    new DefaultAsyncCallback<LeaseAdjustmentPolicy>() {
                        @Override
                        public void onSuccess(LeaseAdjustmentPolicy result) {
                            for (LeaseAdjustmentPolicyItem item : result.policyItems()) {
                                if (item.leaseAdjustmentReason().equals(getValue().reason())) {
                                    BigDecimal taxRate = BigDecimal.ZERO;
                                    for (Tax tax : item.taxes()) {
                                        taxRate = taxRate.add(tax.rate().getValue());
                                    }

                                    get(proto().tax()).populate(taxRate);
                                }
                            }

                            recalculateTotal();
                        }
                    });
        }
    }

    private void recalculateTotal() {
        BigDecimal total = BigDecimal.ZERO;

        if (!getValue().isEmpty()) {
            total = total.add(getValue().amount().getValue(BigDecimal.ZERO));

            switch (getValue().taxType().getValue()) {
            case percent:
                total = total.add(total.multiply(getValue().tax().getValue(BigDecimal.ZERO)));
                break;
            case value:
                total = total.add(getValue().tax().getValue(BigDecimal.ZERO));
                break;
            }
        }

        if (contains(proto()._total())) {
            get(proto()._total()).populate(total);
        }
    }
}