/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.autopay;

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;

public class AutoPayListViewImpl extends SimplePanel implements AutoPayListView {

    private final AutoPayListForm form;

    public AutoPayListViewImpl() {
        form = new AutoPayListForm();
        form.initContent();
        setWidget(form);
    }

    @Override
    public void setPresenter(AutoPayListView.Presenter presenter) {
        form.setPresenter(presenter);
    }

    @Override
    public void populate(AutoPaySummaryDTO preauthorizedPayments) {
        form.reset();
        form.populate(preauthorizedPayments);
    }
}
