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
package com.propertyvista.portal.resident.activity.financial.payment;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.resident.ui.financial.payment.PaymentWizardView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.Financial.Payment;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentWizardService;
import com.propertyvista.portal.shared.activity.AbstractWizardFormCrudActivity;

public class PaymentWizardActivity extends AbstractWizardFormCrudActivity<PaymentDTO> implements PaymentWizardView.Presenter {

    public PaymentWizardActivity(AppPlace place) {
        super(PaymentWizardView.class, GWT.<PaymentWizardService> create(PaymentWizardService.class), PaymentDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((PaymentWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback) {
        ((PaymentWizardService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    protected void onFinish(Key result) {
        AppSite.getPlaceController().goTo(new Payment.PaymentSubmitting(result));
    }

    @Override
    public Class<? extends Place> getTermsOfUsePlace() {
        return ResidentPortalSiteMap.TermsAndConditions.class;
    }

    @Override
    public Class<? extends Place> getBillingPolicyPlace() {
        return ResidentPortalSiteMap.PadPolicy.class;
    }

    @Override
    public void showTermsOfUse() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getTermsOfUsePlace()), "_blank", null);
    }

    @Override
    public void showBillingPolicy() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getBillingPolicyPlace()), "_blank", null);
    }
}
