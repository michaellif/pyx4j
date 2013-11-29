/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.site.client.ui.prime.AbstractPrimePane;

public class MoneyInCreateBatchViewImpl extends AbstractPrimePane implements MoneyInCreateBatchView {

    private LayoutPanel viewPanel;

    private com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView.Presenter presenter;

    public MoneyInCreateBatchViewImpl() {
        initViewPanel();
    }

    @Override
    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
    }

    private void initViewPanel() {
        viewPanel = new LayoutPanel();
        setContentPane(viewPanel);
        setSize("100%", "100%");
    }

}
