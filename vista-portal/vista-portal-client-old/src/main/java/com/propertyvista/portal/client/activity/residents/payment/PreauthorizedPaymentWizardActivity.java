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
package com.propertyvista.portal.client.activity.residents.payment;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AbstractWizardActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentWizardView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentWizardService;

public class PreauthorizedPaymentWizardActivity extends AbstractWizardActivity<PreauthorizedPaymentDTO> implements PreauthorizedPaymentWizardView.Persenter {

    public PreauthorizedPaymentWizardActivity(CrudAppPlace place) {
        super(place, PortalSite.getViewFactory().instantiate(PreauthorizedPaymentWizardView.class), GWT
                .<PreauthorizedPaymentWizardService> create(PreauthorizedPaymentWizardService.class), PreauthorizedPaymentDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((PreauthorizedPaymentWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback) {
        ((PreauthorizedPaymentWizardService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void preview(final AsyncCallback<PreauthorizedPayment> callback, PreauthorizedPaymentDTO currentValue) {
        ((PreauthorizedPaymentWizardService) getService()).preview(new DefaultAsyncCallback<PreauthorizedPayment>() {
            @Override
            public void onSuccess(PreauthorizedPayment result) {
                callback.onSuccess(result);
            }
        }, currentValue);
    }

    @Override
    public void finish() {
        if (!getView().getValue().coveredItems().isEmpty()) {
            super.finish();
        } else {
            getView().reset();
            AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
        }
    }

    @Override
    protected void onSaved(Key result) {
        getView().reset();
        AppSite.getPlaceController().goTo(new Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted(result));
    }
}
