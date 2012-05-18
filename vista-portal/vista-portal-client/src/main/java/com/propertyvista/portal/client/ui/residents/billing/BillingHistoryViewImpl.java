/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.domain.dto.BillListDTO;

public class BillingHistoryViewImpl extends SimplePanel implements BillingHistoryView {

    private final BillingHistoryForm form;

    public BillingHistoryViewImpl() {
        form = new BillingHistoryForm();
        form.initContent();
        setWidget(form);
    }

    @Override
    public void populate(BillListDTO bills) {
        form.populate(bills);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        form.setPresenter(presenter);
    }
}
