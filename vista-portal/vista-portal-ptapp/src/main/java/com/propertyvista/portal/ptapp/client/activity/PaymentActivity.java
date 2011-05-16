/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.portal.domain.ptapp.PaymentInfo;
import com.propertyvista.portal.ptapp.client.ui.PaymentView;
import com.propertyvista.portal.ptapp.client.ui.PaymentView.PaymentPresenter;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;

public class PaymentActivity extends WizardStepActivity<PaymentInfo, PaymentPresenter> implements PaymentPresenter {

    @Inject
    public PaymentActivity(PaymentView view) {
        super(view, PaymentInfo.class, (PaymentService) GWT.create(PaymentService.class));
    }

}
