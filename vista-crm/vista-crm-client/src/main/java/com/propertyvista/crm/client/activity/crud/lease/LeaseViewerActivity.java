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
package com.propertyvista.crm.client.activity.crud.lease;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.client.backoffice.activity.SecureListerController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.lease.agreement.LeaseAgreementDocumentSigningController;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase;
import com.propertyvista.crm.client.activity.crud.lease.legal.LeaseLegalStateController;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseTermBlankAgreementDocumentDownloadService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class LeaseViewerActivity extends LeaseViewerActivityBase<LeaseDTO> implements LeaseViewerView.LeaseViewerPresenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivity.class);

    private final ILister.Presenter<DepositLifecycleDTO> depositLister;

    private final ILister.Presenter<LeaseAdjustment> leaseAdjustmentLister;

    private final ILister.Presenter<MaintenanceRequestDTO> maintenanceLister;

    private boolean isFormerLease = false;

    public LeaseViewerActivity(CrudAppPlace place) {
        super(LeaseDTO.class, place, CrmSite.getViewFactory().getView(LeaseViewerView.class), GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class));

        depositLister = new SecureListerController<DepositLifecycleDTO>(DepositLifecycleDTO.class, ((LeaseViewerView) getView()).getDepositListerView(),
                GWT.<DepositLifecycleCrudService> create(DepositLifecycleCrudService.class));

        leaseAdjustmentLister = new SecureListerController<LeaseAdjustment>(LeaseAdjustment.class,
                ((LeaseViewerView) getView()).getLeaseAdjustmentListerView(), GWT.<LeaseAdjustmentCrudService> create(LeaseAdjustmentCrudService.class)) {
            @Override
            public boolean canCreateNewItem() {
                return (super.canCreateNewItem() && !isFormerLease);
            }
        };

        maintenanceLister = new SecureListerController<MaintenanceRequestDTO>(MaintenanceRequestDTO.class,
                ((LeaseViewerView) getView()).getMaintenanceListerView(), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class)) {
            @Override
            public boolean canCreateNewItem() {
                return (super.canCreateNewItem() && !isFormerLease);
            }
        };
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);

        isFormerLease = result.status().getValue().isFormer();

        populateDeposits(result);
        populateLeaseAdjustments(result);
        populateMaintenance(result);
    }

    protected void populateDeposits(Lease result) {
        depositLister.setParent(result.billingAccount().getPrimaryKey());
        depositLister.populate();
    }

    protected void populateLeaseAdjustments(Lease result) {
        leaseAdjustmentLister.setParent(result.billingAccount().getPrimaryKey());
        leaseAdjustmentLister.populate();
    }

    protected void populateMaintenance(Lease result) {
        maintenanceLister.clearPreDefinedFilters();
        maintenanceLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class).unit(), result.unit()));
        maintenanceLister.populate();
    }

    // Actions:

    @Override
    public void startBilling() {
        GWT.<BillingExecutionService> create(BillingExecutionService.class).startBilling(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Billing"), i18n.tr("Running Billing.."), false) {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        // Navigate to created bill
                        super.onDeferredSuccess(result);
                        populate();
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, EntityFactory.createIdentityStub(Lease.class, getEntityId()));

    }

    @Override
    public void createCompletionEvent(Lease.CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDate, LogicalDate leseEndDate) {
        ((LeaseViewerCrudService) getService()).createCompletionEvent(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), completionType, eventDate, moveOutDate, leseEndDate);
    }

    @Override
    public void isCancelCompletionEventAvailable(final AsyncCallback<CancelMoveOutConstraintsDTO> callback) {
        ((LeaseViewerCrudService) getService()).isCancelCompletionEventAvailable(new DefaultAsyncCallback<CancelMoveOutConstraintsDTO>() {
            @Override
            public void onSuccess(CancelMoveOutConstraintsDTO result) {
                callback.onSuccess(result);
            }
        }, getEntityId());
    }

    @Override
    public void cancelCompletionEvent(String decisionReason) {
        ((LeaseViewerCrudService) getService()).cancelCompletionEvent(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    ((LeaseViewerView) getView()).reportCancelNoticeFailed((UserRuntimeException) caught);
                }
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void moveOut() {
        ((LeaseViewerCrudService) getService()).moveOut(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void sendMail(List<LeaseTermParticipant<?>> users, EmailTemplateType emailType) {
        ((LeaseViewerCrudService) getService()).sendMail(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseViewerView) getView()).reportSendMailActionResult(message);
            }
        }, getEntityId(), new Vector<LeaseTermParticipant<?>>(users), emailType);
    }

    @Override
    public void activate() {
        ((LeaseViewerCrudService) getService()).activate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                setEntityIdAsCurrentKey();
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void closeLease(String decisionReason) {
        ((LeaseViewerCrudService) getService()).closeLease(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void cancelLease(String decisionReason) {
        ((LeaseViewerCrudService) getService()).cancelLease(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void createOffer(final Type type) {
        LeaseTermCrudService.LeaseTermInitializationData id = EntityFactory.create(LeaseTermCrudService.LeaseTermInitializationData.class);
        id.isOffer().setValue(true);
        id.lease().set(EntityFactory.createIdentityStub(Lease.class, getEntityId()));
        id.termType().setValue(type);
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(id));
    }

    @Override
    public void simpleLeaseRenew(LogicalDate leaseEndDate) {
        ((LeaseViewerCrudService) getService()).simpleLeaseRenew(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), leaseEndDate);
    }

    @Override
    public void updateFromYardi() {
        ((LeaseViewerCrudService) getService()).updateFromYardiDeferred(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Lease Update"), i18n.tr("Updating Lease..."), false) {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        populate();
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, getEntityId());
    }

    @Override
    public void issueN4(N4BatchRequestDTO n4GenerationQuery) {
        n4GenerationQuery.targetDelinquentLeases().add(EntityFactory.createIdentityStub(Lease.class, getEntityId()));

        ((LeaseViewerCrudService) getService()).issueN4(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, n4GenerationQuery);
    }

    @Override
    public void viewDeletedPaps(final Tenant tenantId) {
        AutopayAgreement argProto = EntityFactory.getEntityPrototype(AutopayAgreement.class);
        CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AutopayAgreement.class);
        place.formListerPlace();

        place.queryArg(argProto.tenant().lease().leaseId().getPath().toString(), currentValue.leaseId().getValue().toString());

        if (tenantId != null) {
            place.queryArg(argProto.tenant().participantId().getPath().toString(), tenantId.participantId().getValue().toString());
        }

        place.queryArg(argProto.isDeleted().getPath().toString(), Boolean.TRUE.toString());

        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void viewApplication() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseApplication().formViewerPlace(currentValue.getPrimaryKey()));
    }

    @Override
    public void createMaintenanceRequest() {
        MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
        id.unit().set(EntityFactory.createIdentityStub(AptUnit.class, currentValue.unit().getPrimaryKey()));
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.MaintenanceRequest().formNewItemPlace(id));
    }

    @Override
    public void legalState() {
        new LeaseLegalStateController(this.getView(), EntityFactory.createIdentityStub(Lease.class, getEntityId())) {
            @Override
            public void hide() {
                super.hide();
                populate(); // refresh legal status (well, it's crude, but I'm lazy right now)
            };
        }.show();
    }

    @Override
    public void downloadAgreementForSigning() {
        ReportDialog reportDialog = new ReportDialog(i18n.tr("Creating Lease Agreement Document for Signing"), "");
        reportDialog.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);

        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(LeaseTermBlankAgreementDocumentDownloadService.LEASE_ID_PARAM_KEY, getEntityId());

        ReportRequest request = new ReportRequest();
        request.setParameters(params);
        reportDialog.start(GWT.<LeaseTermBlankAgreementDocumentDownloadService> create(LeaseTermBlankAgreementDocumentDownloadService.class), request);
    }

    @Override
    public void signingProgressOrUploadAgreement() {
        List<LeaseTermParticipant<?>> participants = new LinkedList<>();
        for (LeaseTermParticipant<?> p : currentValue.currentTerm().version().tenants()) {
            if (p.role().getValue() != Role.Dependent) {
                participants.add(p);
            }
        }
        for (LeaseTermParticipant<?> p : currentValue.currentTerm().version().guarantors()) {
            if (p.role().getValue() != Role.Dependent) {
                participants.add(p);
            }
        }
        new LeaseAgreementDocumentSigningController((LeaseViewerView) this.getView(), EntityFactory.createIdentityStub(Lease.class, getEntityId()),
                participants).show();
    }

    @Override
    public List<LeaseParticipant<?>> getAllLeaseParticipants() {
        ArrayList<LeaseParticipant<?>> allLeaseParticipants = new ArrayList<LeaseParticipant<?>>();
        if (currentValue == null) {
            return null;
        }
        if (currentValue.currentTerm().version().tenants().size() > 0) {
            for (LeaseTermTenant t : currentValue.currentTerm().version().tenants()) {
                allLeaseParticipants.add(t.leaseParticipant());
            }
        }
        if (currentValue.currentTerm().version().guarantors().size() > 0) {
            for (LeaseTermGuarantor g : currentValue.currentTerm().version().guarantors()) {
                allLeaseParticipants.add(g.leaseParticipant());
            }
        }
        return allLeaseParticipants;
    }

    @Override
    public void confirm(Collection<BillDataDTO> bills) {
        (GWT.<BillCrudService> create(BillCrudService.class)).confirm(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, new Vector<BillDataDTO>(bills));
    }
}
