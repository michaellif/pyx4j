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
package com.propertyvista.portal.client.activity.residents.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AbstractWizardActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodWizardView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.PaymentMethods;
import com.propertyvista.portal.rpc.portal.dto.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodWizardService;

public class PaymentMethodWizardActivity extends AbstractWizardActivity<PaymentMethodDTO> implements PaymentMethodWizardView.Persenter {

    public PaymentMethodWizardActivity(AppPlace place) {
        super(place, ResidentsViewFactory.instance(PaymentMethodWizardView.class), GWT.<PaymentMethodWizardService> create(PaymentMethodWizardService.class),
                PaymentMethodDTO.class);
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
    protected void onSaved(Key result) {
        getView().reset();
        AppSite.getPlaceController().goTo(new PaymentMethods.PaymentMethodSubmitted(result));
    }
}
