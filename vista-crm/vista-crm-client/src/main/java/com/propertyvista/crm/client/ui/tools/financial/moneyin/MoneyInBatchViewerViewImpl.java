/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.forms.MoneyInBatchForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchViewerViewImpl extends CrmViewerViewImplBase<MoneyInBatchDTO> implements MoneyInBatchViewerView {

    private static final I18n i18n = I18n.get(MoneyInBatchViewerViewImpl.class);

    public MoneyInBatchViewerViewImpl() {
        setBreadcrumbsBar(null);
        setForm(new MoneyInBatchForm(this));
        addHeaderToolbarItem(new Button(i18n.tr("Create Deposit Slip"), new Command() {
            @Override
            public void execute() {
                MoneyInBatchViewerViewImpl.this.createDepositSlip();
            }
        }));
    }

    @Override
    protected void populateBreadcrumbs(MoneyInBatchDTO value) {

    }

    private void createDepositSlip() {
        ((MoneyInBatchViewerView.Presenter) getPresenter()).createDownloadableDepositSlipPrintout();
    }
}
