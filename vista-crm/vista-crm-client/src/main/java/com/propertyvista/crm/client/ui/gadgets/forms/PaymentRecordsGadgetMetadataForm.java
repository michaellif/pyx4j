/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 31, 2012
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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentRecordsGadgetMetadataForm extends CEntityDecoratableForm<PaymentRecordsGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentRecordsGadgetMetadataForm.class);

    public PaymentRecordsGadgetMetadataForm() {
        super(PaymentRecordsGadgetMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel p = new FormFlexPanel();
        int row = -1;

        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().refreshInterval())).build());
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentRecordsListerSettings().pageSize())).build());
        p.setWidget(++row, 0, new HTML("&nbsp"));
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customizeTargetDate())).build());
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().targetDate())).build());
        get(proto().targetDate()).setVisible(false);
        get(proto().customizeTargetDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue() != null) {
                    get(proto().targetDate()).setVisible(event.getValue());
                }
            }
        });
        p.setWidget(++row, 0, new HTML("&nbsp"));

        CComponent<Set<PaymentType>> paymentTypeSelector = new CEnumSubsetSelector<PaymentType>(PaymentType.class, Layout.Horizontal);
        paymentTypeSelector.addValueValidator(new EditableValueValidator<Set<PaymentType>>() {
            @Override
            public ValidationError isValid(CComponent<Set<PaymentType>> component, Set<PaymentType> value) {
                if (value != null && value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("Please select at least one payment method option"));
                } else {
                    return null;
                }
            }
        });
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentMethodFilter(), paymentTypeSelector), 50).build());

        // TODO we don't use PaymentStatus.Processing that's why we choose this constructor
        CComponent<Set<PaymentStatus>> paymentStatusSelector = new CEnumSubsetSelector<PaymentStatus>(EnumSet.complementOf(EnumSet
                .of(PaymentStatus.Processing)), Layout.Horizontal);
        paymentStatusSelector.addValueValidator(new EditableValueValidator<Set<PaymentRecord.PaymentStatus>>() {
            @Override
            public ValidationError isValid(CComponent<Set<PaymentStatus>> component, Set<PaymentStatus> value) {
                if (value != null && value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("Please select at least one payment status option"));
                } else {
                    return null;
                }
            }
        });
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentStatusFilter(), paymentStatusSelector), 50).build());

        return p;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().targetDate()).setVisible(getValue().customizeTargetDate().isBooleanTrue());
    }
}
