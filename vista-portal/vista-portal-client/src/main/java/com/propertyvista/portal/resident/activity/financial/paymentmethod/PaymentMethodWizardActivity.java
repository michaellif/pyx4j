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
package com.propertyvista.portal.resident.activity.financial.paymentmethod;

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
import com.propertyvista.portal.resident.ui.financial.paymentmethod.PaymentMethodWizardView;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentMethodWizardService;
import com.propertyvista.portal.shared.activity.AbstractWizardFormCrudActivity;

public class PaymentMethodWizardActivity extends AbstractWizardFormCrudActivity<PaymentMethodDTO> implements PaymentMethodWizardView.Presenter {

    public PaymentMethodWizardActivity(AppPlace place) {
        super(PaymentMethodWizardView.class, GWT.<PaymentMethodWizardService> create(PaymentMethodWizardService.class), PaymentMethodDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((PaymentMethodWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    protected void onFinish(Key result) {
        getView().reset();
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PaymentMethods.PaymentMethodSubmitted(result));
    }

    @Override
    public Class<? extends Place> getTermsOfUsePlace() {
        return ResidentPortalSiteMap.TermsAndConditions.class;
    }

    @Override
    public void showTermsOfUse() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getTermsOfUsePlace()), "_blank", null);
    }
}
