/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.activity.AbstractEditorActivity;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSurePageView;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSurePageView.TenantSurePagePresenter;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureInsurancePolicyCrudService;

public class TenantSurePageActivity extends AbstractEditorActivity<TenantSureInsurancePolicyDTO> implements TenantSurePagePresenter {

    public TenantSurePageActivity(AppPlace place) {
        super(TenantSurePageView.class, GWT.<TenantSureInsurancePolicyCrudService> create(TenantSureInsurancePolicyCrudService.class), place);
    }

    @Override
    public void sendCertificate(String email) {
        ((TenantSureInsurancePolicyCrudService) getService()).sendCertificate(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ((TenantSurePageView) getView()).acknowledgeSentCertificateSuccesfully(result);
            }
        }, email);
    }

    @Override
    public void cancelTenantSure() {
        ((TenantSureInsurancePolicyCrudService) getService()).cancelTenantSure(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                TenantSurePageActivity.this.populate();
            }
        });
    }

    @Override
    public void reinstate() {
        ((TenantSureInsurancePolicyCrudService) getService()).reinstate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                TenantSurePageActivity.this.populate();
            }
        });
    }

    @Override
    public void makeAClaim() {
        ((TenantSurePageView) getView()).displayMakeAClaimDialog();
    }

    @Override
    public void updateCreditCardDetails() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard());
    }

    @Override
    public void viewFaq() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.Faq());
    }

    @Override
    public void viewAboutTenantSure() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.About());
    }

}
