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
package com.propertyvista.crm.client.ui.gadgets.payments;

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;

public class PaymentsSummaryGadgetMetadataForm extends CEntityDecoratableForm<PaymentsSummaryGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentsSummaryGadgetMetadataForm.class);

    public PaymentsSummaryGadgetMetadataForm() {
        super(PaymentsSummaryGadgetMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel p = new FormFlexPanel();
        int row = -1;

        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pageSize())).build());
        p.setWidget(++row, 0, new HTML("&nbsp"));
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customizeDate())).build());
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).build());
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
        CComponent<Set<PaymentStatus>, ?> paymentStatusSelector = new CEnumSubsetSelector<PaymentStatus>(EnumSet.copyOf(PaymentStatus.processed()),
                Layout.Horizontal);
        paymentStatusSelector.addValueValidator(new EditableValueValidator<Set<PaymentStatus>>() {

            @Override
            public ValidationError isValid(CComponent<Set<PaymentStatus>, ?> component, Set<PaymentStatus> value) {
                if (value != null && value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("Please select at least one payment status option"));
                } else {
                    return null;
                }
            }
        });
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentStatus(), paymentStatusSelector), 50).build());
        return p;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().asOf()).setVisible(getValue().customizeDate().isBooleanTrue());
    }
}
