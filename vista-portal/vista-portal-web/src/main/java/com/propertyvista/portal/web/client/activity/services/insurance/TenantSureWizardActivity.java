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
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementDTO;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.AbstractWizardActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSureWizardView;

public class TenantSureWizardActivity extends AbstractWizardActivity<TenantSureAgreementDTO> implements TenantSureWizardView.Persenter {

    public TenantSureWizardActivity(AppPlace place) {
        super(PortalWebSite.getViewFactory().instantiate(TenantSureWizardView.class), GWT.<TenantSurePurchaseService> create(TenantSurePurchaseService.class),
                TenantSureAgreementDTO.class);
    }

    @Override
    protected void onSaved(Key result) {
        AppSite.getPlaceController().goTo(new Financial.PaymentSubmitting(result));
    }
}
