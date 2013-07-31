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

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;

public class EquifaxFeeQuoteForm extends CEntityForm<AbstractEquifaxFee> {

    private final boolean makeMandatory;

    public EquifaxFeeQuoteForm(boolean makeMandatory) {
        super(AbstractEquifaxFee.class);
        this.makeMandatory = makeMandatory;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().recommendationReportSetUpFee())).build());
        panel.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().fullCreditReportSetUpFee())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().recommendationReportPerApplicantFee())).build());
        panel.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().fullCreditReportPerApplicantFee())).build());

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
