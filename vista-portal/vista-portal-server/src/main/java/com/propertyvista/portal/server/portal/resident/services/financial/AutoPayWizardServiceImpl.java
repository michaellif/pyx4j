/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.financial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodTarget;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseProducts;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.AutoPayWizardService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class AutoPayWizardServiceImpl extends AbstractCrudServiceDtoImpl<AutopayAgreement, AutoPayDTO> implements AutoPayWizardService {

    public AutoPayWizardServiceImpl() {
        super(AutopayAgreement.class, AutoPayDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected AutoPayDTO init(InitializationData initializationData) {
        Lease lease = ResidentPortalContext.getLease();
        Persistence.service().retrieve(lease.unit().building());

        AutoPayDTO dto = EntityFactory.create(AutoPayDTO.class);

        dto.allowedPaymentsSetup().set(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentsSetup(lease.billingAccount(), PaymentMethodTarget.AutoPaySetup,
                        VistaApplication.resident));

        dto.address().set(AddressRetriever.getLeaseAddress(lease));

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.tenant().set(ResidentPortalContext.getTenant());

        dto.nextPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(lease));

        fillCoveredItems(dto, lease);

        return dto;
    }

    @Override
    protected boolean persist(AutopayAgreement bo, AutoPayDTO to) {
        Lease lease = ResidentPortalContext.getLease();

        if (bo.paymentMethod().getPrimaryKey() == null) {
            bo.paymentMethod().customer().set(ResidentPortalContext.getCustomer());
            bo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);

            ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), to.paymentMethod(), PaymentMethodTarget.AutoPaySetup,
                    VistaApplication.resident);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(bo.paymentMethod(), lease.unit().building());
        }

        updateCoveredItems(bo, to);

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), to.paymentMethod(), PaymentMethodTarget.AutoPaySetup,
                VistaApplication.resident);
        bo.set(ServerSideFactory.create(PaymentMethodFacade.class).persistAutopayAgreement(bo,
                EntityFactory.createIdentityStub(Tenant.class, ResidentPortalContext.getTenant().getPrimaryKey())));

        // He now have AutoPay
        if (!SecurityController.check(PortalResidentBehavior.AutopayAgreementPresent)) {
            ServerContext.getVisit().setAclRevalidationRequired();
        }

        return true;
    }

    @Override
    protected void enhanceRetrieved(AutopayAgreement bo, AutoPayDTO to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        if (!to.tenant().equals(ResidentPortalContext.getTenant())) {
            to.paymentMethod().set(null);
        }

        Lease lease = ResidentPortalContext.getLease();
        Persistence.service().retrieve(lease.unit().building());

        to.allowedPaymentsSetup().set(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentsSetup(lease.billingAccount(), PaymentMethodTarget.AutoPaySetup,
                        VistaApplication.resident));

        to.address().set(AddressRetriever.getLeaseAddress(lease));

        to.propertyCode().set(lease.unit().building().propertyCode());
        to.unitNumber().set(lease.unit().info().number());

        to.leaseId().set(lease.leaseId());
        to.leaseStatus().set(lease.status());

        to.nextPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(lease));

        updateCoveredItemsDto(to, lease);
        if (retrieveTarget == RetrieveTarget.Edit) {
            fillCoveredItems(to, lease);
        }
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                ResidentPortalContext.getLeaseTermTenant(), PaymentMethodTarget.AutoPaySetup, VistaApplication.resident);
        callback.onSuccess(new Vector<LeasePaymentMethod>(methods));
    }

    @Override
    public void getCurrentAddress(AsyncCallback<InternationalAddress> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddress(ResidentPortalContext.getTenant()));
    }

    @Override
    public void preview(AsyncCallback<AutopayAgreement> callback, AutoPayDTO currentValue) {
        AutopayAgreement entity = binder.createBO(currentValue);

        updateCoveredItems(entity, currentValue);

        callback.onSuccess(entity);
    }

    private void fillCoveredItems(AutoPayDTO papDto, Lease lease) {
        LeaseProducts products = lease.currentTerm().version().leaseProducts();

        if (papDto.total().isNull()) {
            papDto.total().setValue(BigDecimal.ZERO);
        }

        PreauthorizedPaymentCoveredItemDTO itemDto;
        if (ServerSideFactory.create(BillingFacade.class).getActualPrice(products.serviceItem()).compareTo(BigDecimal.ZERO) > 0
                && !isCoveredItemExist(papDto, products.serviceItem())) {
            itemDto = createCoveredItemDTO(products.serviceItem(), lease);
            papDto.coveredItemsDTO().add(itemDto);
            papDto.total().setValue(papDto.total().getValue().add(itemDto.amount().getValue()));
        }

        for (BillableItem billableItem : products.featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
            if (ServerSideFactory.create(BillingFacade.class).getActualPrice(billableItem).compareTo(BigDecimal.ZERO) > 0                                                                            // non-free
                && !ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().code().type().getValue())                    // recursive
                && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(SystemDateManager.getLogicalDate()))   // non-expired
                && !isCoveredItemExist(papDto, billableItem)) {                                                                                     // absent
            //@formatter:on
                itemDto = createCoveredItemDTO(billableItem, lease);
                papDto.coveredItemsDTO().add(itemDto);
                papDto.total().setValue(papDto.total().getValue().add(itemDto.amount().getValue()));
            }
        }
    }

    private boolean isCoveredItemExist(AutoPayDTO papDto, BillableItem billableItem) {
        for (AutopayAgreementCoveredItem item : papDto.coveredItemsDTO()) {
            if (item.billableItem().id().equals(billableItem.id())) {
                return true;
            }
        }
        return false;
    }

    private PreauthorizedPaymentCoveredItemDTO createCoveredItemDTO(BillableItem billableItem, Lease lease) {
        PreauthorizedPaymentCoveredItemDTO itemDto = EntityFactory.create(PreauthorizedPaymentCoveredItemDTO.class);

        // calculate already covered amount by other tenants/paps:
        EntityQueryCriteria<AutopayAgreementCoveredItem> criteria = new EntityQueryCriteria<AutopayAgreementCoveredItem>(AutopayAgreementCoveredItem.class);
        criteria.eq(criteria.proto().pap().tenant().lease(), lease);
        criteria.eq(criteria.proto().billableItem().uid(), billableItem.uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);

        itemDto.covered().setValue(BigDecimal.ZERO);
        for (AutopayAgreementCoveredItem papci : Persistence.secureQuery(criteria)) {
            itemDto.covered().setValue(itemDto.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = ServerSideFactory.create(BillingFacade.class).getActualPrice(billableItem);
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            itemDto.amount().setValue(itemPrice.subtract(itemDto.covered().getValue()));
            itemDto.percent().setValue(itemDto.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            itemDto.amount().setValue(BigDecimal.ZERO);
            itemDto.percent().setValue(BigDecimal.ONE);
        }

        itemDto.billableItem().set(billableItem.duplicate());
        itemDto.billableItem().item().product().setAttachLevel(AttachLevel.ToStringMembers);

        return itemDto;
    }

    private void updateCoveredItemsDto(AutoPayDTO papDto, Lease lease) {
        papDto.coveredItemsDTO().clear();

        if (papDto.total().isNull()) {
            papDto.total().setValue(BigDecimal.ZERO);
        }

        PreauthorizedPaymentCoveredItemDTO itemDto;
        for (AutopayAgreementCoveredItem item : papDto.coveredItems()) {
            itemDto = updateCoveredItemDto(item.duplicate(PreauthorizedPaymentCoveredItemDTO.class), lease);
            papDto.coveredItemsDTO().add(itemDto);
            papDto.total().setValue(papDto.total().getValue().add(itemDto.amount().getValue()));
        }
    }

    private PreauthorizedPaymentCoveredItemDTO updateCoveredItemDto(PreauthorizedPaymentCoveredItemDTO itemDto, Lease lease) {
        // calculate already covered amount by other tenants/paps:
        EntityQueryCriteria<AutopayAgreementCoveredItem> criteria = new EntityQueryCriteria<AutopayAgreementCoveredItem>(AutopayAgreementCoveredItem.class);
        criteria.ne(criteria.proto().pap(), itemDto.pap());
        criteria.eq(criteria.proto().pap().tenant().lease(), lease);
        criteria.eq(criteria.proto().billableItem().uid(), itemDto.billableItem().uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);

        itemDto.covered().setValue(BigDecimal.ZERO);
        for (AutopayAgreementCoveredItem papci : Persistence.secureQuery(criteria)) {
            itemDto.covered().setValue(itemDto.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = ServerSideFactory.create(BillingFacade.class).getActualPrice(itemDto.billableItem());
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            itemDto.percent().setValue(itemDto.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            itemDto.percent().setValue(BigDecimal.ONE);
        }

        return itemDto;
    }

    private void updateCoveredItems(AutopayAgreement entity, AutoPayDTO dto) {
        entity.coveredItems().clear();
        for (PreauthorizedPaymentCoveredItemDTO itemDto : dto.coveredItemsDTO()) {
            if (itemDto.amount().getValue().compareTo(BigDecimal.ZERO) > 0) {
                entity.coveredItems().add(itemDto.duplicate(AutopayAgreementCoveredItem.class));
            }
        }
    }
}
