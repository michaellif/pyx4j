/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.pmc.PmcViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.OnboardingMerchantAccountCrudService;
import com.propertyvista.admin.rpc.services.PmcCrudService;

public class PmcViewerActivity extends AdminViewerActivity<PmcDTO> implements PmcViewerView.Presenter {

    private static final I18n i18n = I18n.get(PmcViewerActivity.class);

    @SuppressWarnings("unchecked")
    public PmcViewerActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(PmcViewerView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class));

    }

    @Override
    public void resetCache() {
        ((PmcCrudService) getService()).resetCache(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                Window.alert("The cache was reset successfully");
            }
        }, getEntityId());

    }

    @Override
    public void activate() {
        ((PmcCrudService) getService()).activate(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("PMC Activation"), i18n.tr("Activating PMC ..."), false) {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        PmcViewerActivity.this.refresh();
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, getEntityId());

    }

    @Override
    public void suspend() {
        ((PmcCrudService) getService()).suspend(new DefaultAsyncCallback<PmcDTO>() {

            @Override
            public void onSuccess(PmcDTO result) {
                populateView(result);
            }
        }, getEntityId());
    }

    @Override
    public void cancelPmc() {
        ((PmcCrudService) getService()).cancelPmc(new DefaultAsyncCallback<PmcDTO>() {

            @Override
            public void onSuccess(PmcDTO result) {
                if (result == null) {
                    cancel();
                } else {
                    populateView(result);
                }
            }
        }, getEntityId());
    }

    @Override
    public ListerDataSource<OnboardingMerchantAccountDTO> getOnboardingMerchantAccountsSource() {
        return new ListerDataSource<OnboardingMerchantAccountDTO>(OnboardingMerchantAccountDTO.class,
                GWT.<AbstractListService<OnboardingMerchantAccountDTO>> create(OnboardingMerchantAccountCrudService.class));
    }
}
