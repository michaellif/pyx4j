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
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;

public class PaymentMethodsViewImpl extends SimplePanel implements PaymentMethodsView {

    private final PaymentMethodsForm form;

    public PaymentMethodsViewImpl() {
        form = new PaymentMethodsForm();
        form.initialize();
        setWidget(form);

    }

    @Override
    public void populate(PaymentMethodListDTO paymentMethods) {
        form.populate(paymentMethods);
    }

}
