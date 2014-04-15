/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;

public class PaymentsSummaryGadgetMetadataForm extends CEntityForm<PaymentsSummaryGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentsSummaryGadgetMetadataForm.class);

    public PaymentsSummaryGadgetMetadataForm() {
        super(PaymentsSummaryGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel p = new TwoColumnFlexFormPanel();
        int row = -1;

        p.setWidget(++row, 0, inject(proto().refreshInterval(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, inject(proto().paymentsSummaryListerSettings().pageSize(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, new HTML("&nbsp"));
        p.setWidget(++row, 0, inject(proto().customizeDate(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, inject(proto().asOf(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, new HTML("&nbsp"));
        get(proto().asOf()).setVisible(false);
        get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue() != null) {
                    get(proto().asOf()).setVisible(event.getValue());
                }
            }
        });
        CComponent<Set<PaymentStatus>> paymentStatusSelector = new CEnumSubsetSelector<PaymentStatus>(EnumSet.copyOf(PaymentStatus.processed()),
                Layout.Horizontal);
        paymentStatusSelector.addComponentValidator(new AbstractComponentValidator<Set<PaymentStatus>>() {

            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().isEmpty()) {
                    return new FieldValidationError(getComponent(), i18n.tr("Please select at least one payment status option"));
                } else {
                    return null;
                }
            }
        });
        p.setWidget(++row, 0, inject(proto().paymentStatus(), paymentStatusSelector, new FormDecoratorBuilder(50).build()));
        return p;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().asOf()).setVisible(getValue().customizeDate().getValue(false));
    }
}
