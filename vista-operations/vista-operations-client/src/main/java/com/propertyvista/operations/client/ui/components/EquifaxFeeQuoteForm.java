/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;

public class EquifaxFeeQuoteForm extends CForm<AbstractEquifaxFee> {

    private final boolean makeMandatory;

    public EquifaxFeeQuoteForm(boolean makeMandatory) {
        super(AbstractEquifaxFee.class);
        this.makeMandatory = makeMandatory;
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, inject(proto().recommendationReportSetUpFee(), new FieldDecoratorBuilder().build()));
        panel.setWidget(row, 1, inject(proto().fullCreditReportSetUpFee(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().recommendationReportPerApplicantFee(), new FieldDecoratorBuilder().build()));
        panel.setWidget(row, 1, inject(proto().fullCreditReportPerApplicantFee(), new FieldDecoratorBuilder().build()));

        if (makeMandatory) {
            get(proto().recommendationReportSetUpFee()).setMandatory(true);
            get(proto().recommendationReportPerApplicantFee()).setMandatory(true);
            get(proto().fullCreditReportSetUpFee()).setMandatory(true);
            get(proto().fullCreditReportPerApplicantFee()).setMandatory(true);
        }

        return panel;
    }

    void setMandatory() {

    }

}
