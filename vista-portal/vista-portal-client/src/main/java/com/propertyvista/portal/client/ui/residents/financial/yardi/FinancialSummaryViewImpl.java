/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.financial.yardi;

import com.google.gwt.user.client.Command;

import com.propertyvista.portal.client.ui.residents.View;
import com.propertyvista.portal.client.ui.residents.ViewImpl;
import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;

public class FinancialSummaryViewImpl extends ViewImpl<YardiFinancialSummaryDTO> implements FinancialSummaryView {

    private FinancialSummaryView.Presenter presenter;

    private final FinancialSummaryForm form;

    public FinancialSummaryViewImpl() {
        super(true, true);
        setForm(form = new FinancialSummaryForm(new Command() {
            @Override
            public void execute() {
                presenter.payNow();
            }
        }));
    }

    @Override
    public void setPresenter(View.Presenter<YardiFinancialSummaryDTO> presenter) {
        this.presenter = (FinancialSummaryView.Presenter) presenter;
    }

    @Override
    public void setEnablePayments(boolean enable) {
        form.setPayNowVisible(enable);
    }
}
