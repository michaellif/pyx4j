/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.payment;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.domain.dto.financial.PaymentDTO;
import com.propertyvista.portal.web.client.ui.IWizardView;

public interface PaymentWizardView extends IWizardView<PaymentDTO> {

    interface Presenter extends IWizardPresenter<PaymentDTO> {

        void getCurrentAddress(AsyncCallback<AddressSimple> callback);

        void getProfiledPaymentMethods(AsyncCallback<List<LeasePaymentMethod>> callback);

        Class<? extends Place> getTermsOfUsePlace();

        Class<? extends Place> getPrivacyPolicyPlace();

        Class<? extends Place> getBillingPolicyPlace();

        void showTermsOfUse();

        void showPrivacyPolicy();

        void showBillingPolicy();
    }
}
