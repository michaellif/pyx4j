/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.autopayreview;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.formatters.MoneyFormat;
import com.pyx4j.forms.client.ui.formatters.PercentageFormat;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.tools.autopayreview.PapReviewFolder.Styles;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO.ChangeType;

public final class PapChargeReviewForm extends CEntityDecoratableForm<PapChargeReviewDTO> {

    public PapChargeReviewForm() {
        super(PapChargeReviewDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.setStylePrimaryName(Styles.AutoPayCharge.name());

        panel.add(new MiniDecorator(inject(proto().chargeName()), Styles.AutoPayChargeNameColumn.name()));
        get(proto().chargeName()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().changeType()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().changeType()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().suspendedPrice()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().suspendedPrice()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().suspendedPapAmount()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().suspendedPapAmount()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().suspendedPapPercent()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().suspendedPapPercent()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().newPrice()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().newPrice()).setViewable(true);

        CMoneyField newPapAmount = new CMoneyField();
        newPapAmount.setFormat(new AmountByPercentCalculatorFormat());
        panel.add(new MiniDecorator(inject(proto().newPapAmount(), newPapAmount), Styles.AutoPayChargeNumberColumn.name()));

        panel.add(new MiniDecorator(inject(proto().newPapPercent()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().newPapPercent()).setViewable(true);

        get(proto().newPapAmount()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
            @Override
            public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                BigDecimal newPercent = event.getValue() != null ? event.getValue().divide(get(proto().newPrice()).getValue(), MathContext.DECIMAL32) : null;
                get(proto().newPapPercent()).setValue(newPercent, false);
                updateChangePercent();
            }
        });
        get(proto().newPapPercent()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
            @Override
            public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                BigDecimal newAmount = event.getValue() != null ? get(proto().newPrice()).getValue().multiply(event.getValue()) : null;
                get(proto().newPapAmount()).setValue(newAmount, false);
                updateChangePercent();
            }
        });

        panel.add(new MiniDecorator(inject(proto().changePercent()), Styles.AutoPayChargeNumberColumn.name()));
        get(proto().changePercent()).setViewable(true);

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().newPrice()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
        get(proto().newPapAmount()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
        get(proto().newPapPercent()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
    }

    private void updateChangePercent() {

        BigDecimal changePercent = new BigDecimal("0.00");
        if (getValue().changeType().getValue() == ChangeType.New) {
            BigDecimal newPapAmount = get(proto().newPapAmount()).getValue();
            changePercent = newPapAmount != null && (newPapAmount.compareTo(BigDecimal.ZERO) != 0) ? new BigDecimal("1.00") : BigDecimal.ZERO;
        }
        if (getValue().changeType().getValue() == ChangeType.Removed) {
            changePercent = new BigDecimal("-1.00");
        }
        if (getValue().changeType().getValue() == ChangeType.Changed || getValue().changeType().getValue() == ChangeType.Unchanged) {
            if (get(proto().newPapAmount()).getValue() != null) {
                BigDecimal change = get(proto().newPapAmount()).getValue().subtract(get(proto().suspendedPapAmount()).getValue());
                changePercent = change.divide(get(proto().suspendedPapAmount()).getValue(), MathContext.DECIMAL32);
            } else {
                changePercent = null;
            }
        }
        get(proto().changePercent()).setValue(changePercent, false);
    }

    private final class AmountByPercentCalculatorFormat implements IFormat<BigDecimal> {

        private final MoneyFormat moneyFormat = new MoneyFormat();

        private final PercentageFormat percentageFormat = new PercentageFormat();

        @Override
        public String format(BigDecimal value) {
            return moneyFormat.format(value);
        }

        @Override
        public BigDecimal parse(String string) throws ParseException {
            if (string.endsWith("%")) {
                return calculateAmount(percentageFormat.parse(string));
            } else {
                return moneyFormat.parse(string);
            }
        }

        private BigDecimal calculateAmount(BigDecimal percent) {
            BigDecimal suspendedPrice = PapChargeReviewForm.this.get(PapChargeReviewForm.this.proto().suspendedPrice()).getValue();
            suspendedPrice = suspendedPrice == null ? new BigDecimal("0.00") : suspendedPrice;
            return suspendedPrice.multiply(percent);
        }

    }
}