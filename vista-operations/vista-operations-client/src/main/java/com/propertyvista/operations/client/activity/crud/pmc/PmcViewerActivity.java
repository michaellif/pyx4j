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
package com.propertyvista.operations.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.pmc.PmcViewerView;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;

public class PmcViewerActivity extends AdminViewerActivity<PmcDTO> implements PmcViewerView.Presenter {

    private static final I18n i18n = I18n.get(PmcViewerActivity.class);

    @SuppressWarnings("unchecked")
    public PmcViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(PmcViewerView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class));

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
        ((PmcCrudService) getService()).suspend(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, getEntityId());
    }

    @Override
    public void cancelPmc() {
        ((PmcCrudService) getService()).cancelPmc(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, getEntityId());
    }

    @Override
    public void runProcess(PmcProcessType pmcProcessType, ScheduleDataDTO date) {
        LogicalDate forDate = null;
        if (date != null) {
            forDate = date.date().getValue();
        }
        ((PmcCrudService) getService()).runPmcProcess(new DefaultAsyncCallback<Run>() {

            @Override
            public void onSuccess(Run result) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.TriggerRun().formViewerPlace(result.getPrimaryKey()));
            }
        }, getEntityId(), pmcProcessType, forDate);
    }

    @Override
    public ListerDataSource<PmcMerchantAccountDTO> getOnboardingMerchantAccountsSource() {
        return new ListerDataSource<PmcMerchantAccountDTO>(PmcMerchantAccountDTO.class,
                GWT.<AbstractListService<PmcMerchantAccountDTO>> create(PmcMerchantAccountCrudService.class));
    }

}
