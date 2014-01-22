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

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.billing.bill.BillListerController;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerActivity extends LeaseViewerActivityBase<LeaseDTO> implements LeaseViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivity.class);

    private final ILister.Presenter<DepositLifecycleDTO> depositLister;

    private final ILister.Presenter<BillDataDTO> billLister;

    private final ILister.Presenter<PaymentRecordDTO> paymentLister;

    private final ILister.Presenter<LeaseAdjustment> leaseAdjustmentLister;

    private final ILister.Presenter<MaintenanceRequestDTO> maintenanceLister;

    private LeaseDTO currentValue;

    public LeaseViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LeaseViewerView.class), GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class));

        depositLister = new ListerController<DepositLifecycleDTO>(((LeaseViewerView) getView()).getDepositListerView(),
                GWT.<DepositLifecycleCrudService> create(DepositLifecycleCrudService.class), DepositLifecycleDTO.class);

        billLister = new BillListerController(((LeaseViewerView) getView()).getBillListerView());

        paymentLister = new ListerController<PaymentRecordDTO>(((LeaseViewerView) getView()).getPaymentListerView(),
                GWT.<PaymentCrudService> create(PaymentCrudService.class), PaymentRecordDTO.class) {
            @Override
            public boolean canCreateNewItem() {
                return (currentValue.billingAccount().paymentAccepted().getValue() != BillingAccount.PaymentAccepted.DoNotAccept);
            }
        };

        leaseAdjustmentLister = new ListerController<LeaseAdjustment>(((LeaseViewerView) getView()).getLeaseAdjustmentListerView(),
                GWT.<LeaseAdjustmentCrudService> create(LeaseAdjustmentCrudService.class), LeaseAdjustment.class);

        maintenanceLister = new ListerController<MaintenanceRequestDTO>(((LeaseViewerView) getView()).getMaintenanceListerView(),
                GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class), MaintenanceRequestDTO.class);
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);

        currentValue = result;

        populateDeposits(result);
        populateBills(result);
        populatePayments(result);
        populateLeaseAdjustments(result);
        populateMaintenance(result);
    }

    protected void populateDeposits(Lease result) {
        depositLister.setParent(result.billingAccount().getPrimaryKey());
        depositLister.populate();
    }

    protected void populateBills(Lease result) {
        if (SecurityController.checkBehavior(VistaCrmBehavior.Billing)) {
            EntityFiltersBuilder<BillDataDTO> filters = EntityFiltersBuilder.create(BillDataDTO.class);
            filters.eq(filters.proto().bill().billingAccount().id(), result.billingAccount().getPrimaryKey());
            billLister.setPreDefinedFilters(filters.getFilters());
            billLister.populate();
        }
    }

    protected void populatePayments(Lease result) {
        paymentLister.setParent(result.billingAccount().getPrimaryKey());
        paymentLister.populate();
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
                        populateBills(currentValue);
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
    public void onInsuredTenantClicked(Tenant tenantId) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Tenant.class, tenantId.getPrimaryKey()));
    }

    @Override
    public void updateFromYardi() {
        ((LeaseViewerCrudService) getService()).updateFromYardi(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
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
        AutopayAgreement proto = EntityFactory.getEntityPrototype(AutopayAgreement.class);
        CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AutopayAgreement.class);
        place.formListerPlace();

        place.queryArg(proto.tenant().lease().leaseId().getPath().toString(), currentValue.leaseId().getValue().toString());

        if (tenantId != null) {
            place.queryArg(proto.tenant().participantId().getPath().toString(), tenantId.participantId().getValue().toString());
        }

        place.queryArg(proto.isDeleted().getPath().toString(), Boolean.TRUE.toString());

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
    public void setLegalStatus() {
        ((LeaseViewerView) getView()).requestNewLegalStatus(new DefaultAsyncCallback<LegalStatus>() {
            @Override
            public void onSuccess(LegalStatus result) {
                ((LeaseViewerCrudService) getService()).setLegalStatus(new DefaultAsyncCallback<VoidSerializable>() {
                    @Override
                    public void onSuccess(VoidSerializable result) {
                        refresh();
                    }
                }, EntityFactory.createIdentityStub(Lease.class, getEntityId()), result);
            }
        });
    }

    @Override
    public void clearLegalStatus() {
        final LegalStatus status = EntityFactory.create(LegalStatus.class);
        status.status().setValue(Status.None);
        ((LeaseViewerCrudService) getService()).setLegalStatus(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, EntityFactory.createIdentityStub(Lease.class, getEntityId()), status);
    }
}
