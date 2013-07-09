/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck.components;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.NFocusField;

import com.propertyvista.domain.pmc.CreditCheckReportType;

public class NCreditCheckReportTypeSelector extends NFocusField<CreditCheckReportType, CreditCheckReportTypeSelector, CCreditCheckReportTypeSelector, HTML> {

    private final ReportTypeDetailsResources reportDetailsResources;

    public NCreditCheckReportTypeSelector(CCreditCheckReportTypeSelector cComponent, ReportTypeDetailsResources reportDetailsResources) {
        super(cComponent);
        this.reportDetailsResources = reportDetailsResources;
    }

    @Override
    public void setNativeValue(CreditCheckReportType value) {
        if (isEditable()) {
            getEditor().setCreditCheckReportType(value);
        }
    }

    @Override
    public CreditCheckReportType getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getCreditCheckReportType();
        }
    }

    public void setFees(CreditCheckReportType reportType, BigDecimal setupFee, BigDecimal perApplicantFee) {
        if (isViewable()) {
            assert false : "this is not implemented yet!";
        } else {
            getEditor().setFees(reportType, setupFee, perApplicantFee);
        }
    }

    @Override
    protected CreditCheckReportTypeSelector createEditor() {
        return new CreditCheckReportTypeSelector(reportDetailsResources);
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addValueChangeHandler(new ValueChangeHandler<CreditCheckReportType>() {
            @Override
            public void onValueChange(ValueChangeEvent<CreditCheckReportType> event) {
                getCComponent().onEditingStop();
            }
        });
    }

    @Override
    protected HTML createViewer() {
        assert false : "this is not implemented yet!";
        return new HTML();
    }

}
