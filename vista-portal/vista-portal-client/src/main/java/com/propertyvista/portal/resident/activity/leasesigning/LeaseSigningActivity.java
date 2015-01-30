/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 */
package com.propertyvista.portal.resident.activity.leasesigning;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ui.leasesigning.LeaseSigningView;
import com.propertyvista.portal.resident.ui.leasesigning.LeaseSigningView.LeaseSigningPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;

public class LeaseSigningActivity extends AbstractEditorActivity<LeaseAgreementDTO> implements LeaseSigningPresenter {

    public LeaseSigningActivity(AppPlace place) {
        super(LeaseSigningView.class, GWT.<LeaseSigningCrudService> create(LeaseSigningCrudService.class), place);

        getView().reset();
        getView().setEditable(true);
    }

    @Override
    public void retreive() {
        getService().init(new DefaultAsyncCallback<LeaseAgreementDTO>() {
            @Override
            public void onSuccess(LeaseAgreementDTO result) {
                getView().populate(result);
            }
        }, null);
    }

    @Override
    public void submit() {
        if (!getView().isDirty()) {
            getService().create(new DefaultAsyncCallback<Key>() {
                @Override
                public void onSuccess(Key result) {
                    getView().reset();
                    ReferenceDataManager.invalidate(LeaseAgreementDTO.class);
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizardConfirmation());
                }
            }, getView().getValue());
        }
    }
}
