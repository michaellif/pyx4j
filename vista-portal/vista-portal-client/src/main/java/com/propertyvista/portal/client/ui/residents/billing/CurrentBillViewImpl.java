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
package com.propertyvista.portal.client.ui.residents.billing;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.domain.dto.BillDataDTO;

public class CurrentBillViewImpl extends FlowPanel implements CurrentBillView {

    private final CurrentBillForm billForm;

    private static final I18n i18n = I18n.get(CurrentBillViewImpl.class);

    public CurrentBillViewImpl() {
        billForm = new CurrentBillForm();
        billForm.initContent();
        add(billForm);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        billForm.setPresenter(presenter);

    }

    @Override
    public void populate(BillDataDTO bill) {
        billForm.populate(bill);
    }
}
