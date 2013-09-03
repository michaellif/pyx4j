/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractWizardService;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.tenantsure.TenantSurePurchaseRequestDTO;
import com.propertyvista.portal.web.client.activity.AbstractWizardActivity;
import com.propertyvista.portal.web.client.ui.IWizardView;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSurePurchaseWizardView;

public class TenantSurePurchaseWizardActivity extends AbstractWizardActivity<TenantSurePurchaseRequestDTO> implements TenantSurePurchaseWizardView.Presenter {

    public TenantSurePurchaseWizardActivity(IWizardView<TenantSurePurchaseRequestDTO> view, AbstractWizardService<TenantSurePurchaseRequestDTO> service,
            Class<TenantSurePurchaseRequestDTO> entityClass) {
        super(view, service, entityClass);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        // TODO Auto-generated method stub

    }

}
