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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
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

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().code(), new CEntitySelectorHyperlink<ARCode>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(ARCode.class, getValue().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new LeaseAdjustmentReasonSelectorDialog(getParentView()) {
                    @Override
                    public void onClickOk() {
                        if (!getSelectedItem().isNull()) {
                            get(LeaseAdjustmentForm.this.proto().code()).setValue(getSelectedItem());
                            recalculateTaxesAndTotal();
                        }
                    }
                };
            }
        }).decorate();
        formPanel.append(Location.Left, proto().amount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().executionType()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().targetDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().receivedDate(), new CDateLabel()).decorate().componentWidth(90);

        formPanel.append(Location.Right, proto().overwriteDefaultTax()).decorate().componentWidth(80);
        formPanel.append(Location.Right, taxHolder);
        formPanel.append(Location.Right, proto().taxType()).decorate().componentWidth(120);
        if (!isEditable()) {
            formPanel.append(Location.Right, new HTML("&nbsp;"));
            formPanel.append(Location.Right, proto()._total()).decorate().componentWidth(120);
        }

        formPanel.append(Location.Dual, proto().description()).decorate();

        // tweak:
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

        if (!isEditable()) {
            formPanel.hr();

            formPanel.append(Location.Left, injectAndDecorate(proto().created(), 10));
            formPanel.append(Location.Right, injectAndDecorate(proto().updated(), 10));

            formPanel.append(Location.Left, injectAndDecorate(proto().createdBy(), 25));
            formPanel.append(Location.Right, injectAndDecorate(proto().status(), 10));
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

        bindValueEditor(getValue().taxType().getValue(), true);
        recalculateTaxesAndTotal();
    }

    private void bindValueEditor(TaxType valueType, boolean repopulate) {
        CField<BigDecimal, ?> comp = null;
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
            taxHolder.setWidget(injectAndDecorate(proto().tax(), comp, 10));

            if (repopulate) {
                get(proto().tax()).populate(getValue().tax().getValue(BigDecimal.ZERO));
            }
        }
    }

    private void recalculateTaxesAndTotal() {
        if (getValue().overwriteDefaultTax().getValue(false)) {
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