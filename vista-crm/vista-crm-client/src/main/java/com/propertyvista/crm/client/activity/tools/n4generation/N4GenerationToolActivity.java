/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.n4generation;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.n4generation.N4GenerationToolView;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationInitParamsDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationQueryDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationSettingsDTO;
import com.propertyvista.crm.rpc.services.legal.N4GenerationToolService;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4GenerationToolActivity extends AbstractBulkOperationToolActivity<N4GenerationSettingsDTO, LegalNoticeCandidateDTO, N4GenerationQueryDTO> {

    public N4GenerationToolActivity(AppPlace place) {
        super(place, CrmSite.getViewFactory().instantiate(N4GenerationToolView.class), GWT.<N4GenerationToolService> create(N4GenerationToolService.class),
                N4GenerationSettingsDTO.class);
    }

    @Override
    protected N4GenerationQueryDTO makeProducedItems(List<LegalNoticeCandidateDTO> selectedItems) {
        N4GenerationQueryDTO query = getView().getSettings().query().duplicate(N4GenerationQueryDTO.class);

        for (LegalNoticeCandidateDTO noticeCandidate : selectedItems) {
            query.targetDelinquentLeases().add(noticeCandidate.leaseId().<Lease> duplicate());
        }

        return query;
    }

    @Override
    protected void onSelectedProccessSuccess(DeferredProcessProgressResponse result) {

    }

    @Override
    protected void initViewAsync(final AsyncCallback<N4GenerationSettingsDTO> callback) {
        (GWT.<N4GenerationToolService> create(N4GenerationToolService.class)).initSettings(new DefaultAsyncCallback<N4GenerationInitParamsDTO>() {
            @Override
            public void onSuccess(N4GenerationInitParamsDTO result) {
                ((N4GenerationToolView) getView()).setAgents(result.availableAgents());
                callback.onSuccess(result.settings());
            }
        });
    }
}
