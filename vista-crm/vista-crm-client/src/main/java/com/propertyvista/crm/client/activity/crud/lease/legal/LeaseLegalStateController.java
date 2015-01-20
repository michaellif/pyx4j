/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-03
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.crud.lease.legal;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.visor.IVisor;

import com.propertyvista.crm.client.ui.crud.lease.legal.LeaseLegalStateVisor;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseLegalStateDTO;
import com.propertyvista.dto.LegalStatusDTO;

@Deprecated
public class LeaseLegalStateController implements IVisor.Controller {

    private final LeaseLegalStateVisor legalStateVisor = new LeaseLegalStateVisor(this);

    private final LeaseViewerCrudService service = GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class);

    private final IPrimePaneView<?> parentView;

    private final Lease leaseId;

    public LeaseLegalStateController(IPrimePaneView<?> parentView, Lease leaseId) {
        this.parentView = parentView;
        this.leaseId = leaseId;
    }

    @Override
    public void show() {
        populateAndShow();
    }

    @Override
    public void hide() {
        this.parentView.hideVisor();
    }

    private void populateAndShow() {
        service.getLegalState(new DefaultAsyncCallback<LeaseLegalStateDTO>() {
            @Override
            public void onSuccess(LeaseLegalStateDTO result) {
                legalStateVisor.populate(result);
                parentView.showVisor(legalStateVisor);
            }
        }, leaseId);
    }

    public void updateStatus() {
        legalStateVisor.requestNewLegalStatus(new DefaultAsyncCallback<LegalStatusDTO>() {
            @Override
            public void onSuccess(LegalStatusDTO result) {
                service.setLegalStatus(new DefaultAsyncCallback<VoidSerializable>() {
                    @Override
                    public void onSuccess(VoidSerializable result) {
                        LeaseLegalStateController.this.populateAndShow(); // refresh
                    }
                }, LeaseLegalStateController.this.leaseId, result);
            }
        });
    }

    public void clearStatus() {
        final LegalStatusDTO status = EntityFactory.create(LegalStatusDTO.class);
        status.status().setValue(Status.None);
        service.setLegalStatus(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                LeaseLegalStateController.this.populateAndShow(); // refresh
            }
        }, leaseId, status);
    }

    public void deleteStatus(LegalStatus statusId) {
        service.deleteLegalStatus(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                LeaseLegalStateController.this.populateAndShow(); // refresh
            }
        }, leaseId, statusId);
    }

}
