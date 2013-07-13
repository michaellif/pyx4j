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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.TaxType;

public class LeaseAdjustmentForm extends CrmEntityForm<LeaseAdjustment> {

    private static final I18n i18n = I18n.get(LeaseAdjustmentForm.class);

    private final SimplePanel taxHolder = new SimplePanel();

    public LeaseAdjustmentForm(IForm<LeaseAdjustment> view) {
        super(LeaseAdjustment.class, view);

        TwoColumnFlexFormPanel left = new TwoColumnFlexFormPanel();
        int row = -1;

        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().code(), new CEntitySelectorHyperlink<ARCode>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(ARCode.class, getValue().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<ARCode> getSelectorDialog() {
                return new LeaseAdjustmentReasonSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            get(LeaseAdjustmentForm.this.proto().code()).setValue(getSelectedItems().get(0));
                            recalculateTaxesAndTotal();
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionType()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().targetDate()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().receivedDate()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 25).build());

        TwoColumnFlexFormPanel right = new TwoColumnFlexFormPanel();
        row = -1;
        right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().overwriteDefaultTax()), 5).build());
        right.setWidget(++row, 0, taxHolder);
        right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().taxType()), 10).build());
        if (!isEditable()) {
            right.setBR(++row, 0, 2);
            right.setBR(++row, 0, 2);
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto()._total()), 10).build());
        }

        // tweak:
        get(proto().receivedDate()).setViewable(true);
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

        get(proto().taxType()).addValueChangeHandler(new ValueChangeHandler<TaxType>() {
            @Override
            public void onValueChange(ValueChangeEvent<TaxType> event) {
                bindValueEditor(event.getValue(), false);
            }
        });

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        content.setWidget(0, 0, left);
        content.setWidget(0, 1, right);
        content.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        content.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        if (!isEditable()) {
            row = 0;
            content.setHR(++row, 0, 2);
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().created()), 10).build());
            content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().updated()), 10).build());

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().createdBy()), 25).build());
            content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().status()), 10).build());
        }

        selectTab(addTab(content));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().targetDate()).addValueValidator(new FutureDateValidator());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        bindValueEditor(getValue().taxType().getValue(), true);
        recalculateTaxesAndTotal();
    }

    private void bindValueEditor(TaxType valueType, boolean repopulate) {
        CComponent<?> comp = null;
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
            taxHolder.setWidget(new FormDecoratorBuilder(inject(proto().tax(), comp), 10).build());

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

            LeaseAdjustmentPresenter presenter;
            if (isEditable()) {
                presenter = (LeaseAdjustmentPresenter) ((LeaseAdjustmentEditorView) getParentView()).getPresenter();
            } else {
                presenter = (LeaseAdjustmentPresenter) ((LeaseAdjustmentViewerView) getParentView()).getPresenter();
            }
            presenter.calculateTax(new DefaultAsyncCallback<BigDecimal>() {
                @Override
                public void onSuccess(BigDecimal result) {
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