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
package com.propertyvista.portal.client.ui.residents.paymentmethod;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;

public class PaymentMethodsViewImpl extends FlowPanel implements PaymentMethodsView {

    private static final I18n i18n = I18n.get(PaymentMethodsViewImpl.class);

    private final PaymentMethodsForm form;

    private Presenter presenter;

    public PaymentMethodsViewImpl() {
        add(new UserMessagePanel());

        form = new PaymentMethodsForm();
        form.initContent();
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.setPresenter(presenter);
    }

    @Override
    public void populate(List<PaymentMethod> paymentMethods) {
        form.reset();
        form.populate(paymentMethods);
    }
}
