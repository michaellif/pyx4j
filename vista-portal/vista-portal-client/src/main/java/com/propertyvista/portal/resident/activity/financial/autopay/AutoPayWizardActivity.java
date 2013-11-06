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
package com.propertyvista.portal.resident.activity.financial.autopay;

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
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.resident.activity.AbstractWizardCrudActivity;
import com.propertyvista.portal.resident.ui.financial.autopay.AutoPayWizardView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PortalTerms;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.AutoPayWizardService;

public class AutoPayWizardActivity extends AbstractWizardCrudActivity<AutoPayDTO> implements AutoPayWizardView.Presenter {

    public AutoPayWizardActivity(AppPlace place) {
        super(AutoPayWizardView.class, GWT.<AutoPayWizardService> create(AutoPayWizardService.class), AutoPayDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((AutoPayWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback) {
        ((AutoPayWizardService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void preview(final AsyncCallback<AutopayAgreement> callback, AutoPayDTO currentValue) {
        ((AutoPayWizardService) getService()).preview(new DefaultAsyncCallback<AutopayAgreement>() {
            @Override
            public void onSuccess(AutopayAgreement result) {
                callback.onSuccess(result);
            }
        }, currentValue);
    }

    @Override
    public void submit() {
        if (!getView().getValue().coveredItems().isEmpty()) {
            super.submit();
        } else {
            getView().reset();
            AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
        }
    }

    @Override
    protected void onFinish(Key result) {
        getView().reset();
        AppSite.getPlaceController().goTo(new Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted(result));
    }

    @Override
    public Class<? extends Place> getTermsOfUsePlace() {
        return PortalTerms.TermsAndConditions.class;
    }

    @Override
    public Class<? extends Place> getPadPolicyPlace() {
        return PortalTerms.PadPolicy.class;
    }

    @Override
    public Class<? extends Place> getCcPolicyPlace() {
        return PortalTerms.CreditCardPolicy.class;
    }

    @Override
    public void showTermsOfUse() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getTermsOfUsePlace()), "_blank", null);
    }

    @Override
    public void showPadPolicy() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getPadPolicyPlace()), "_blank", null);
    }

    @Override
    public void showCcPolicy() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getCcPolicyPlace()), "_blank", null);
    }
}
