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
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;

public class PreauthorizedPaymentsViewImpl extends FlowPanel implements PreauthorizedPaymentsView {

    protected final UserMessagePanel messagePanel;

    private final PreauthorizedPaymentsForm form;

    public PreauthorizedPaymentsViewImpl() {
        add(messagePanel = new UserMessagePanel());

        form = new PreauthorizedPaymentsForm();
        form.initContent();
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        form.setPresenter(presenter);
    }

    @Override
    public void populate(PreauthorizedPaymentListDTO preauthorizedPayments) {
        messagePanel.clearMessage();

        form.reset();
        form.populate(preauthorizedPayments);
    }
}
