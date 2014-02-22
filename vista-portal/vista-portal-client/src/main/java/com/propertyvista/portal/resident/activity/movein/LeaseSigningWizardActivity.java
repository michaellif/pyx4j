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
package com.propertyvista.portal.resident.activity.movein;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ui.movein.LeaseSigningWizardView;
import com.propertyvista.portal.resident.ui.movein.LeaseSigningWizardView.LeaseSigningWizardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class LeaseSigningWizardActivity extends AbstractWizardCrudActivity<LeaseAgreementDTO, LeaseSigningWizardView> implements LeaseSigningWizardPresenter {

    public LeaseSigningWizardActivity(AppPlace place) {
        super(LeaseSigningWizardView.class, GWT.<LeaseSigningCrudService> create(LeaseSigningCrudService.class), LeaseAgreementDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.MoveIn.MoveInWizardConfirmation());
    }
}