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
package com.propertyvista.crm.client.activity.tools.legal.n4;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.visor.IVisorEditor;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.tools.common.AbstractBulkOperationToolActivity;
import com.propertyvista.crm.client.ui.tools.legal.n4.N4GenerationToolView;
import com.propertyvista.crm.client.ui.tools.legal.n4.visors.N4BatchSettingsVisor;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDefaultParamsDTO;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Lease;

// TODO refactor this: this should be redesigned and refactored to fix the quick and dirty changes that have been made to deal with 'deferred' way of search for n4 candidates 
public class N4CreateBatchActivity extends AbstractBulkOperationToolActivity<N4CandidateSearchCriteriaDTO, LegalNoticeCandidateDTO, N4BatchRequestDTO> {

    private static final I18n i18n = I18n.get(N4CreateBatchActivity.class);

    protected N4BatchRequestDTO batchRequest;

    protected IList<Employee> agents;

    private final N4CreateBatchService service;

    public N4CreateBatchActivity(AppPlace place) {
        super(place, CrmSite.getViewFactory().getView(N4GenerationToolView.class), GWT.<N4CreateBatchService> create(N4CreateBatchService.class),
                N4CandidateSearchCriteriaDTO.class);
        service = GWT.<N4CreateBatchService> create(N4CreateBatchService.class);
    }

    @Override
    public void acceptSelected() {
        IVisorEditor.Controller visorController = new IVisorEditor.Controller() {//@formatter:off
            private N4BatchSettingsVisor visor;

            { visor = new N4BatchSettingsVisor(this); }

            @Override public void show() {
                visor.populate(batchRequest);
                visor.setAgents(agents);
                getView().showVisor(visor);
            }

            @Override public void hide() { getView().hideVisor(); }

            @Override public void save() { apply(); hide(); }

            @Override
            public void apply() { N4CreateBatchActivity.super.acceptSelected(); }

        };//@formatter:on
        visorController.show();
    }

    @Override
    public void search() {
        getView().setLoading(true);
        service.searchForItems(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final DeferredProcessDialog dialog = new DeferredProcessDialog(i18n.tr("N4 Search"), i18n.tr("Searching for N4 Candidates"), false) {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        endSearch();
                        hide();
                    };
                };
                dialog.show();
                dialog.startProgress(result);
            }
        }, getView().getSettings().<N4CandidateSearchCriteriaDTO> duplicate());
    }

    @Override
    protected N4BatchRequestDTO makeProducedItems(List<LegalNoticeCandidateDTO> selectedItems) {
        N4BatchRequestDTO batchRequest = this.batchRequest.duplicate(N4BatchRequestDTO.class);
        for (LegalNoticeCandidateDTO noticeCandidate : selectedItems) {
            batchRequest.targetDelinquentLeases().add(noticeCandidate.leaseId().<Lease> duplicate());
        }
        return batchRequest;
    }

    @Override
    protected void onSelectedProccessSuccess(DeferredProcessProgressResponse result) {

    }

    @Override
    protected void initSettings(final AsyncCallback<N4CandidateSearchCriteriaDTO> callback) {
        (GWT.<N4CreateBatchService> create(N4CreateBatchService.class)).initSettings(new DefaultAsyncCallback<N4GenerationDefaultParamsDTO>() {
            @Override
            public void onSuccess(N4GenerationDefaultParamsDTO result) {
                N4CreateBatchActivity.this.batchRequest = result.batchRequest().duplicate();
                N4CreateBatchActivity.this.agents = result.availableAgents();
                callback.onSuccess(EntityFactory.create(N4CandidateSearchCriteriaDTO.class));
            }
        });
    }

    @Override
    protected void initView(N4CandidateSearchCriteriaDTO settings) {
        super.initView(settings);
        getView().setSearchEnabled(CommonsStringUtils.isEmpty(settings.n4PolicyErrors().getValue()));
    }

    private void endSearch() {
        service.getFoundItems(new DefaultAsyncCallback<Vector<LegalNoticeCandidateDTO>>() {
            @Override
            public void onSuccess(Vector<LegalNoticeCandidateDTO> result) {
                N4CreateBatchActivity.this.items = result;

                getView().resetVisibleRange();
                N4CreateBatchActivity.this.populateItems();
                N4CreateBatchActivity.this.getView().setLoading(false);
            }
        });
    }

}
