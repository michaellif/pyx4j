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
 */
package com.propertyvista.operations.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;

public class EquifaxFeeQuoteForm extends CForm<AbstractEquifaxFee> {

    private final boolean systemDefault;

    private final boolean makeMandatory;

    public EquifaxFeeQuoteForm(boolean systemDefault, boolean makeMandatory) {
        super(AbstractEquifaxFee.class);
        this.systemDefault = systemDefault;
        this.makeMandatory = makeMandatory;
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().recommendationReportSetUpFee()).decorate();
        formPanel.append(Location.Right, proto().fullCreditReportSetUpFee()).decorate();
        formPanel.append(Location.Left, proto().recommendationReportPerApplicantFee()).decorate();
        formPanel.append(Location.Right, proto().fullCreditReportPerApplicantFee()).decorate();

        if (systemDefault) {
            formPanel.append(Location.Dual, proto().taxRate()).decorate();
        }

        if (makeMandatory) {
            get(proto().recommendationReportSetUpFee()).setMandatory(true);
            get(proto().recommendationReportPerApplicantFee()).setMandatory(true);
            get(proto().fullCreditReportSetUpFee()).setMandatory(true);
            get(proto().fullCreditReportPerApplicantFee()).setMandatory(true);
        }

        return formPanel;
    }

    void setMandatory() {

    }

}
