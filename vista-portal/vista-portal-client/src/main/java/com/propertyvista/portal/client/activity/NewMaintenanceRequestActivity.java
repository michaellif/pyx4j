/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.portal.client.ui.residents.NewMaintenanceRequestView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class NewMaintenanceRequestActivity extends SecurityAwareActivity implements NewMaintenanceRequestView.Presenter {

    private final NewMaintenanceRequestView view;

    private final TenantMaintenanceService srv;

    private MaintenanceRequestDTO request;

    public NewMaintenanceRequestActivity(Place place) {
        this.view = PortalViewFactory.instance(NewMaintenanceRequestView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantMaintenanceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        request = EntityFactory.create(MaintenanceRequestDTO.class);
        view.populate(request);

        srv.listIssueElements(new DefaultAsyncCallback<Vector<IssueElement>>() {

            @Override
            public void onSuccess(Vector<IssueElement> result) {
                view.updateIssueElementSelector(result);
            }
        });
    }

    @Override
    public void submit() {
        srv.createNewTicket(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance());
            }

        }, request);
    }

    @Override
    public void onIssueElementSelection(IssueElement selectedItem) {
        srv.getIssueRepairSubject(new DefaultAsyncCallback<IssueElement>() {

            @Override
            public void onSuccess(IssueElement result) {
                view.updateIssueRepairSubjectSelector(result.subjects());
            }
        }, selectedItem);
    }

    @Override
    public void onIssueRepairSubjectSelection(IssueRepairSubject selectedItem) {
        srv.getIssueSubjectDetails(new DefaultAsyncCallback<IssueRepairSubject>() {

            @Override
            public void onSuccess(IssueRepairSubject result) {
                view.updateIssueSubjectDetailsSelector(result.details());
            }
        }, selectedItem);
    }

    @Override
    public void onSubjectDetailsSelection(IssueSubjectDetails selectedItem) {
        srv.getIssueClassification(new DefaultAsyncCallback<IssueSubjectDetails>() {

            @Override
            public void onSuccess(IssueSubjectDetails result) {
                view.updateIssueClassificationSelector(result.classifications());
            }
        }, selectedItem);
    }
}
