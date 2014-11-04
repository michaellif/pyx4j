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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CMoneyPercentCombo;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.ExecutionType;

public class LeaseAdjustmentForm extends CrmEntityForm<LeaseAdjustment> {

    private static final I18n i18n = I18n.get(LeaseAdjustmentForm.class);

    public LeaseAdjustmentForm(IForm<LeaseAdjustment> view) {
        super(LeaseAdjustment.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().code(), new CEntitySelectorHyperlink<ARCode>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(ARCode.class, getValue().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new LeaseAdjustmentReasonSelectionDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItem().isNull()) {
                            get(LeaseAdjustmentForm.this.proto().code()).setValue(getSelectedItem());
                            recalculateTaxesAndTotal();
                        }
                        return true;
                    }
                };
            }
        }).decorate();
        formPanel.append(Location.Left, proto().amount()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().overwriteDefaultTax()).decorate().componentWidth(80);
        get(proto().overwriteDefaultTax()).setVisible(isEditable());
        formPanel.append(Location.Left, proto().taxType()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().tax(), new CMoneyPercentCombo()).decorate().componentWidth(100);
        if (!isEditable()) {
            formPanel.append(Location.Left, proto()._total()).decorate().componentWidth(120);
        }

        formPanel.append(Location.Right, proto().executionType()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().targetDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().receivedDate(), new CDateLabel()).decorate().componentWidth(90);

        formPanel.append(Location.Dual, proto().description()).decorate();

        // tweak:
        get(proto().taxType()).addValueChangeHandler(new ValueChangeHandler<ValueType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ValueType> event) {
                ((CMoneyPercentCombo) get(proto().tax())).setAmountType(event.getValue());
            }
        });
        get(proto().executionType()).addValueChangeHandler(new ValueChangeHandler<ExecutionType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ExecutionType> event) {
                get(proto().targetDate()).setEditable(event.getValue() != ExecutionType.immediate);
                if (event.getValue() == ExecutionType.immediate) {
                    get(proto().targetDate()).setValue(new LogicalDate(ClientContext.getServerDate()));
                    recalculateTaxesAndTotal();
                }
            }
        });
        get(proto().overwriteDefaultTax()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                recalculateTaxesAndTotal();
            }
        });

        if (!isEditable()) {
            formPanel.hr();

            formPanel.append(Location.Left, proto().created()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(120);

            formPanel.append(Location.Left, proto().createdBy()).decorate();
            formPanel.append(Location.Right, proto().status()).decorate().componentWidth(120);
        }

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().targetDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // sync tax type
        if (getValue() != null) {
            ((CMoneyPercentCombo) get(proto().tax())).setAmountType(getValue().taxType().getValue());
        }

        recalculateTaxesAndTotal();
    }

    private void recalculateTaxesAndTotal() {
        if (getValue().overwriteDefaultTax().getValue(false)) {
            get(proto().taxType()).setEditable(true);
            get(proto().tax()).setEditable(true);

            recalculateTotal();
        } else {
            get(proto().taxType()).setEditable(false);
            get(proto().tax()).setEditable(false);

            LeaseAdjustmentPresenter presenter;
            if (isEditable()) {
                presenter = (LeaseAdjustmentPresenter) ((LeaseAdjustmentEditorView) getParentView()).getPresenter();
            } else {
                presenter = (LeaseAdjustmentPresenter) ((LeaseAdjustmentViewerView) getParentView()).getPresenter();
            }
            presenter.calculateTax(new DefaultAsyncCallback<IMoneyPercentAmount>() {
                @Override
                public void onSuccess(IMoneyPercentAmount result) {
                    get(proto().tax()).populate(result);
                    recalculateTotal();
                }
            }, getValue());
        }
    }

    private void recalculateTotal() {
        BigDecimal total = BigDecimal.ZERO;

        if (!getValue().isEmpty()) {
            total = total.add(getValue().amount().getValue(BigDecimal.ZERO));

            if (!getValue().tax().isNull()) {
                switch (getValue().taxType().getValue()) {
                case Percentage:
                    total = total.add(total.multiply(getValue().tax().percent().getValue(BigDecimal.ZERO)));
                    break;
                case Monetary:
                    total = total.add(getValue().tax().amount().getValue(BigDecimal.ZERO));
                    break;
                }
            }
        }

        if (contains(proto()._total())) {
            get(proto()._total()).populate(total);
        }
    }
}