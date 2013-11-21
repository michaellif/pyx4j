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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade.PaymentMethodUsage;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseProducts;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.AutoPayWizardService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressConverter;
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

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.resident));
        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(lease.billingAccount(), VistaApplication.resident));
        dto.convenienceFeeApplicableCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(lease.billingAccount(), VistaApplication.resident));

        // TODO: Currently allow just non-convenience fee cards (VISTA-3817, change 0):
        dto.allowedCardTypes().removeAll(dto.convenienceFeeApplicableCardTypes());

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), dto.address());

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
    protected void persist(AutopayAgreement bo, AutoPayDTO to) {
        Lease lease = ResidentPortalContext.getLease();

        if (bo.paymentMethod().getPrimaryKey() == null) {
            bo.paymentMethod().customer().set(ResidentPortalContext.getCustomer());
            bo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);

            ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), to.paymentMethod(), VistaApplication.resident);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(bo.paymentMethod(), lease.unit().building());
        }

        updateCoveredItems(bo, to);

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), to.paymentMethod(), VistaApplication.resident);
        bo.set(ServerSideFactory.create(PaymentMethodFacade.class).persistAutopayAgreement(bo,
                EntityFactory.createIdentityStub(Tenant.class, ResidentPortalContext.getTenant().getPrimaryKey())));
    }

    @Override
    protected void enhanceRetrieved(AutopayAgreement bo, AutoPayDTO to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        if (!to.tenant().equals(ResidentPortalContext.getTenant())) {
            to.paymentMethod().set(null);
        }

        Lease lease = ResidentPortalContext.getLease();
        Persistence.service().retrieve(lease.unit().building());

        to.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        to.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.resident));

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), to.address());

        to.propertyCode().set(lease.unit().building().propertyCode());
        to.unitNumber().set(lease.unit().info().number());

        to.leaseId().set(lease.leaseId());
        to.leaseStatus().set(lease.status());

        to.nextPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(lease));

        updateCoveredItemsDto(to, lease);
        fillCoveredItems(to, lease);
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                ResidentPortalContext.getTenantInLease(), PaymentMethodUsage.AutopayAgreementSetup, VistaApplication.resident);
        callback.onSuccess(new Vector<LeasePaymentMethod>(methods));
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(ResidentPortalContext.getTenant()));
    }

    @Override
    public void preview(AsyncCallback<AutopayAgreement> callback, AutoPayDTO currentValue) {
        AutopayAgreement entity = createBO(currentValue);

        updateCoveredItems(entity, currentValue);

        callback.onSuccess(entity);
    }

    private void fillCoveredItems(AutoPayDTO papDto, Lease lease) {
        LeaseProducts products = lease.currentTerm().version().leaseProducts();

        if (papDto.total().isNull()) {
            papDto.total().setValue(BigDecimal.ZERO);
        }

        PreauthorizedPaymentCoveredItemDTO itemDto;
        if (products.serviceItem().agreedPrice().getValue().compareTo(BigDecimal.ZERO) > 0 && !isCoveredItemExist(papDto, products.serviceItem())) {
            itemDto = createCoveredItemDTO(products.serviceItem(), lease);
            papDto.coveredItemsDTO().add(itemDto);
            papDto.total().setValue(papDto.total().getValue().add(itemDto.amount().getValue()));
        }

        for (BillableItem billableItem : products.featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
            if (billableItem.agreedPrice().getValue().compareTo(BigDecimal.ZERO) > 0                                                                            // non-free
                && !ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().type().getValue())                                       // recursive
                && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(new LogicalDate(SystemDateManager.getDate())))     // non-expired 
                && !isCoveredItemExist(papDto, billableItem)) {                                                                                                 // absent
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

        BigDecimal itemPrice = billableItem.agreedPrice().getValue();
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            itemDto.amount().setValue(itemPrice.subtract(itemDto.covered().getValue()));
            itemDto.percent().setValue(itemDto.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            itemDto.amount().setValue(BigDecimal.ZERO);
            itemDto.percent().setValue(BigDecimal.ONE);
        }

        itemDto.billableItem().set(billableItem);

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

        BigDecimal itemPrice = itemDto.billableItem().agreedPrice().getValue();
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
