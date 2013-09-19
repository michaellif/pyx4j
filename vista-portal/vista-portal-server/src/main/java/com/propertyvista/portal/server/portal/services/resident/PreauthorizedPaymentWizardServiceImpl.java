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
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
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
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseProducts;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentWizardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class PreauthorizedPaymentWizardServiceImpl extends AbstractCrudServiceDtoImpl<PreauthorizedPayment, PreauthorizedPaymentDTO> implements
        PreauthorizedPaymentWizardService {

    public PreauthorizedPaymentWizardServiceImpl() {
        super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected PreauthorizedPaymentDTO init(InitializationData initializationData) {
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit().building());

        PreauthorizedPaymentDTO dto = EntityFactory.create(PreauthorizedPaymentDTO.class);

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.residentPortal));
        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(lease.billingAccount(), VistaApplication.residentPortal));

        new AddressConverter.StructuredToSimpleAddressConverter().copyDBOtoDTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.tenant().set(TenantAppContext.getCurrentUserTenant());

        dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(lease));
        dto.paymentCutOffDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(dto.tenant().lease()));

        fillCoveredItems(dto, lease.currentTerm().version().leaseProducts());

        return dto;
    }

    @Override
    public void save(AsyncCallback<Key> callback, PreauthorizedPaymentDTO dto) {
        PreauthorizedPayment entity = createDBO(dto);

        Lease lease = TenantAppContext.getCurrentUserLease();
        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), dto.paymentMethod(), VistaApplication.residentPortal);

        if (entity.paymentMethod().getPrimaryKey() == null) {
            entity.paymentMethod().customer().set(TenantAppContext.getCurrentUserCustomer());
            entity.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(entity.paymentMethod(), lease.unit().building());
        }

        updateCoveredItems(entity, dto);

        ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(entity,
                EntityFactory.createIdentityStub(Tenant.class, TenantAppContext.getCurrentUserTenant().getPrimaryKey()));
        Persistence.service().commit();

        callback.onSuccess(entity.getPrimaryKey());
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        callback.onSuccess(new Vector<LeasePaymentMethod>(LeaseParticipantUtils.getProfiledPaymentMethods(TenantAppContext.getCurrentUserTenantInLease())));
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(TenantAppContext.getCurrentUserTenant()));
    }

    @Override
    public void preview(AsyncCallback<PreauthorizedPayment> callback, PreauthorizedPaymentDTO currentValue) {
        PreauthorizedPayment entity = createDBO(currentValue);

        updateCoveredItems(entity, currentValue);

        callback.onSuccess(entity);
    }

    private void fillCoveredItems(PreauthorizedPaymentDTO papDto, LeaseProducts products) {
        papDto.total().setValue(BigDecimal.ZERO);

        PreauthorizedPaymentCoveredItemDTO item = createCoveredItemDTO(products.serviceItem());
        papDto.total().setValue(papDto.total().getValue().add(item.amount().getValue()));
        papDto.coveredItemsDTO().add(item);

        for (BillableItem billableItem : products.featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
            if (!ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().type().getValue())                                          // recursive
                && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(new LogicalDate(SystemDateManager.getDate())))     // non-expired 
                && !isCoveredItemExist(papDto, billableItem)) {                                                                                                 // absent
            //@formatter:on
                item = createCoveredItemDTO(billableItem);
                papDto.total().setValue(papDto.total().getValue().add(item.amount().getValue()));
                papDto.coveredItemsDTO().add(item);
            }
        }
    }

    private boolean isCoveredItemExist(PreauthorizedPaymentDTO papDto, BillableItem billableItem) {
        for (PreauthorizedPaymentCoveredItem item : papDto.coveredItemsDTO()) {
            if (item.billableItem().id().equals(billableItem.id())) {
                return true;
            }
        }
        return false;
    }

    private PreauthorizedPaymentCoveredItemDTO createCoveredItemDTO(BillableItem billableItem) {
        PreauthorizedPaymentCoveredItemDTO item = EntityFactory.create(PreauthorizedPaymentCoveredItemDTO.class);

        // calculate already covered amount by other tenants/paps: 
        EntityQueryCriteria<PreauthorizedPaymentCoveredItem> criteria = new EntityQueryCriteria<PreauthorizedPaymentCoveredItem>(
                PreauthorizedPaymentCoveredItem.class);
        criteria.eq(criteria.proto().pap().tenant().lease(), TenantAppContext.getCurrentUserLeaseIdStub());
        criteria.eq(criteria.proto().billableItem().uid(), billableItem.uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);
        criteria.isNull(criteria.proto().pap().expiring());

        item.covered().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem papci : Persistence.secureQuery(criteria)) {
            item.covered().setValue(item.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = billableItem.agreedPrice().getValue();
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            item.amount().setValue(itemPrice.subtract(item.covered().getValue()));
            item.percent().setValue(item.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            item.amount().setValue(BigDecimal.ZERO);
            item.percent().setValue(BigDecimal.ONE);
        }

        item.billableItem().set(billableItem);

        return item;
    }

    private void updateCoveredItems(PreauthorizedPayment entity, PreauthorizedPaymentDTO dto) {
        entity.coveredItems().clear();
        for (PreauthorizedPaymentCoveredItemDTO item : dto.coveredItemsDTO()) {
            if (item.amount().getValue().compareTo(BigDecimal.ZERO) > 0) {
                entity.coveredItems().add(item.duplicate(PreauthorizedPaymentCoveredItem.class));
            }
        }
    }
}
