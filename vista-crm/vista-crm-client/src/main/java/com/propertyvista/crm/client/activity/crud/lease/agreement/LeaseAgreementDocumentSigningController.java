/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.agreement;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.lease.agreement.LeaseAgreementDocumentSigningVisor;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;

public class LeaseAgreementDocumentSigningController implements IVisorEditor.Controller {

    private final LeaseAgreementDocumentSigningVisor visor;

    private final LeaseViewerView view;

    private final List<LeaseTermParticipant<?>> leaseTermParticipantOptions;

    private final Lease leaseId;

    public LeaseAgreementDocumentSigningController(LeaseViewerView view, Lease leaseId, List<LeaseTermParticipant<?>> leaseTermParticipantOptions) {
        this.visor = new LeaseAgreementDocumentSigningVisor(this);
        this.view = view;
        this.leaseId = leaseId;
        this.leaseTermParticipantOptions = leaseTermParticipantOptions;
    }

    @Override
    public void show() {
        populate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                LeaseAgreementDocumentSigningController.this.view.showVisor(LeaseAgreementDocumentSigningController.this.visor);
            }
        });
    }

    @Override
    public void hide() {
        this.view.hideVisor();
    }

    @Override
    public void apply() {
        GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class).updateLeaseAgreementDocuments(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // do nothing
            }
        }, this.leaseId, this.visor.getValue());
    }

    @Override
    public void save() {
        GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class).updateLeaseAgreementDocuments(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate(new DefaultAsyncCallback<VoidSerializable>() {
                    @Override
                    public void onSuccess(VoidSerializable result) {
                        LeaseAgreementDocumentSigningController.this.view.hideVisor();
                    }
                });
            }
        }, this.leaseId, this.visor.getValue());
    }

    private void populate(final AsyncCallback<VoidSerializable> callback) {
        GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class).getLeaseAgreementDocuments(new DefaultAsyncCallback<LeaseAgreementDocumentsDTO>() {
            @Override
            public void onSuccess(LeaseAgreementDocumentsDTO leaseAgreementDocuments) {
                LeaseAgreementDocumentSigningController.this.visor.setParticipantsOptions(leaseTermParticipantOptions);
                LeaseAgreementDocumentSigningController.this.visor.populate(leaseAgreementDocuments);
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        }, this.leaseId);
    }

}
