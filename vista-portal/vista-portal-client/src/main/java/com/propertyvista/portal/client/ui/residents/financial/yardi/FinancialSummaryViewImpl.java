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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.domain.dto.FinancialSummaryDTO;

public class FinancialSummaryViewImpl extends Composite implements FinancialSummaryView {

    private final SimplePanel viewPanel;

    private Presenter presenter;

    private final FinancialSummaryForm form;

    public FinancialSummaryViewImpl() {
        viewPanel = new SimplePanel();
        form = new FinancialSummaryForm(new Command() {
            @Override
            public void execute() {
                presenter.payNow();
            }
        });
        form.initContent();
        viewPanel.add(form);
        initWidget(viewPanel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEnablePayments(boolean enable) {
        form.setPayNowVisible(enable);
    }

    @Override
    public void populate(FinancialSummaryDTO financialSummary) {
        form.setVisited(false);
        form.populate(financialSummary);
    }

}
