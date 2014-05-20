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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentRecordsGadgetMetadataForm extends CForm<PaymentRecordsGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentRecordsGadgetMetadataForm.class);

    public PaymentRecordsGadgetMetadataForm() {
        super(PaymentRecordsGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Left, proto().refreshInterval()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().paymentRecordsListerSettings().pageSize()).decorate().componentWidth(80);
        formPanel.append(Location.Left, new HTML("&nbsp"));
        formPanel.append(Location.Left, proto().customizeTargetDate()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().targetDate()).decorate().componentWidth(80);
        get(proto().targetDate()).setVisible(false);
        get(proto().customizeTargetDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue() != null) {
                    get(proto().targetDate()).setVisible(event.getValue());
                }
            }
        });
        formPanel.append(Location.Left, new HTML("&nbsp"));

        CComponent<?, Set<PaymentType>, ?> paymentTypeSelector = new CEnumSubsetSelector<PaymentType>(PaymentType.class, Layout.Horizontal);
        paymentTypeSelector.addComponentValidator(new AbstractComponentValidator<Set<PaymentType>>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().isEmpty()) {
                    return new BasicValidationError(getComponent(), i18n.tr("Please select at least one payment method option"));
                } else {
                    return null;
                }
            }
        });
        formPanel.append(Location.Left, proto().paymentMethodFilter(), paymentTypeSelector);

        // TODO we don't use PaymentStatus.Processing that's why we choose this constructor
        CComponent<?, Set<PaymentStatus>, ?> paymentStatusSelector = new CEnumSubsetSelector<PaymentStatus>(EnumSet.complementOf(EnumSet
                .of(PaymentStatus.Processing)), Layout.Horizontal);
        paymentStatusSelector.addComponentValidator(new AbstractComponentValidator<Set<PaymentRecord.PaymentStatus>>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().isEmpty()) {
                    return new BasicValidationError(getComponent(), i18n.tr("Please select at least one payment status option"));
                } else {
                    return null;
                }
            }
        });
        formPanel.append(Location.Left, proto().paymentStatusFilter(), paymentStatusSelector);

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().targetDate()).setVisible(getValue().customizeTargetDate().getValue(false));
    }
}
