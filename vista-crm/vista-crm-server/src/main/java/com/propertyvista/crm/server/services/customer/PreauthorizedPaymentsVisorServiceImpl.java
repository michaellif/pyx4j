/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsVisorServiceImpl implements PreauthorizedPaymentsVisorService {

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentsDTO dto = EntityFactory.create(PreauthorizedPaymentsDTO.class);

        dto.tenant().set(tenantId);

        fillTenantInfo(dto);
        fillPreauthorizedPayments(dto);
        fillAvailablePaymentMethods(dto);

        callback.onSuccess(dto);
    }

    @Override
    public void create(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentDTO papDto = EntityFactory.create(PreauthorizedPaymentDTO.class);

        papDto.tenant().set(tenantId);

        fillCoveredItemsDto(papDto);

        callback.onSuccess(papDto);
    }

    @Override
    public void delete(AsyncCallback<VoidSerializable> callback, PreauthorizedPayment pad) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pad);
        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO dto) {
        List<PreauthorizedPayment> paps = new ArrayList<PreauthorizedPayment>();
        for (PreauthorizedPaymentDTO papDTO : dto.preauthorizedPayments()) {
            updateCoveredItems(papDTO);
            paps.add(new PapConverter().createDBO(papDTO));
        }

        // delete payment methods removed in UI:
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(dto.tenant())) {
            if (!paps.contains(pap)) {
                ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pap);
            }
        }

        // save new/edited ones:
        for (PreauthorizedPayment pap : paps) {
            // remove zero covered items:
            Iterator<PreauthorizedPaymentCoveredItem> iterator = pap.coveredItems().iterator();
            while (iterator.hasNext()) {
                PreauthorizedPaymentCoveredItem item = iterator.next();
                if (item.amount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    iterator.remove();
                    if (item.getPrimaryKey() != null) {
                        Persistence.service().delete(item);
                    }
                }
            }

            ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(pap, dto.tenant());
        }

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void recollect(AsyncCallback<Vector<PreauthorizedPayment>> callback, Tenant tenantId) {
        callback.onSuccess(new Vector<PreauthorizedPayment>(ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(tenantId)));
    }

    private void fillTenantInfo(PreauthorizedPaymentsDTO dto) {
        Persistence.ensureRetrieve(dto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.tenant().lease(), AttachLevel.Attached);

        dto.tenantInfo().name().set(dto.tenant().customer().person().name());

        EntityListCriteria<LeaseTermParticipant> criteria = new EntityListCriteria<LeaseTermParticipant>(LeaseTermParticipant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), dto.tenant()));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder(), dto.tenant().lease().currentTerm()));

        LeaseTermParticipant<?> ltp = Persistence.service().retrieve(criteria);
        if (ltp != null) {
            dto.tenantInfo().role().setValue(ltp.role().getValue());
        }

        dto.nextScheduledPaymentDate().setValue(
                ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(dto.tenant().lease()));
        dto.paymentCutOffDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(dto.tenant().lease()));
    }

    private void fillAvailablePaymentMethods(PreauthorizedPaymentsDTO papDto) {
        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);

        EntityListCriteria<LeasePaymentMethod> criteria = new EntityListCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), papDto.tenant().customer()));
        criteria.add(PropertyCriterion.eq(criteria.proto().isProfiledMethod(), Boolean.TRUE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        papDto.availablePaymentMethods().addAll(Persistence.service().query(criteria));
    }

    private void fillPreauthorizedPayments(PreauthorizedPaymentsDTO dto) {
        Persistence.ensureRetrieve(dto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.tenant().lease(), AttachLevel.Attached);

        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(dto.tenant())) {
            dto.preauthorizedPayments().add(createPreauthorizedPaymentDto(pap));
        }
    }

    private PreauthorizedPaymentDTO createPreauthorizedPaymentDto(PreauthorizedPayment pap) {
        PreauthorizedPaymentDTO papDto = new PapConverter().createDTO(pap);

        updateCoveredItemsDto(papDto);
        fillCoveredItemsDto(papDto);

        return papDto;
    }

    private void fillCoveredItemsDto(PreauthorizedPaymentDTO papDto) {
        if (!papDto.expiring().isNull()) {
            return; // do not fill-up expired paps!.. 
        }

        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(papDto.tenant().lease(), AttachLevel.Attached);

        Lease lease = papDto.tenant().lease();

        if (!isCoveredItemExist(papDto, lease.currentTerm().version().leaseProducts().serviceItem())) {
            papDto.coveredItemsDTO().add(
                    createCoveredItemDto(lease.currentTerm().version().leaseProducts().serviceItem(), lease, papDto.getPrimaryKey() == null));
        }

        for (BillableItem billableItem : lease.currentTerm().version().leaseProducts().featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
                if (!ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().type().getValue())                                          // recursive
                    && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(new LogicalDate(SystemDateManager.getDate())))     // non-expired 
                  /*&& !isCoveredItemExist(papDto, billableItem)*/) {                                                                                                 // absent
            //@formatter:on
                papDto.coveredItemsDTO().add(createCoveredItemDto(billableItem, lease, papDto.getPrimaryKey() == null));
            }
        }
    }

    private boolean isCoveredItemExist(PreauthorizedPaymentDTO papDto, BillableItem billableItem) {
        for (PreauthorizedPaymentCoveredItem item : papDto.coveredItemsDTO()) {
            if (item.billableItem().uid().getValue().equals(billableItem.uid().getValue())) {
                return true;
            }
        }
        return false;
    }

    private PreauthorizedPaymentCoveredItemDTO createCoveredItemDto(BillableItem billableItem, Lease lease, boolean isNewPap) {
        PreauthorizedPaymentCoveredItemDTO item = EntityFactory.create(PreauthorizedPaymentCoveredItemDTO.class);

        // calculate already covered amount by other tenants/paps: 
        EntityQueryCriteria<PreauthorizedPaymentCoveredItem> criteria = new EntityQueryCriteria<PreauthorizedPaymentCoveredItem>(
                PreauthorizedPaymentCoveredItem.class);
        criteria.eq(criteria.proto().pap().tenant().lease(), lease);
        criteria.eq(criteria.proto().billableItem().uid(), billableItem.uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);
        criteria.isNull(criteria.proto().pap().expiring());

        item.covered().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem papci : Persistence.secureQuery(criteria)) {
            item.covered().setValue(item.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = billableItem.agreedPrice().getValue();
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            item.amount().setValue(isNewPap ? itemPrice.subtract(item.covered().getValue()) : BigDecimal.ZERO);
            item.percent().setValue(item.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            item.amount().setValue(BigDecimal.ZERO);
            item.percent().setValue(BigDecimal.ONE);
        }

        item.billableItem().set(billableItem);

        return item;
    }

    private void updateCoveredItemsDto(PreauthorizedPaymentDTO papDto) {
        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(papDto.tenant().lease(), AttachLevel.Attached);

        papDto.coveredItemsDTO().clear();
        for (PreauthorizedPaymentCoveredItem item : papDto.coveredItems()) {
            PreauthorizedPaymentCoveredItemDTO itemDto = item.duplicate(PreauthorizedPaymentCoveredItemDTO.class);
            papDto.coveredItemsDTO().add(updateCoveredItemDto(itemDto, papDto.tenant().lease()));
        }
    }

    private PreauthorizedPaymentCoveredItemDTO updateCoveredItemDto(PreauthorizedPaymentCoveredItemDTO item, Lease lease) {
        // calculate already covered amount by other tenants/paps: 
        EntityQueryCriteria<PreauthorizedPaymentCoveredItem> criteria = new EntityQueryCriteria<PreauthorizedPaymentCoveredItem>(
                PreauthorizedPaymentCoveredItem.class);
        criteria.ne(criteria.proto().pap(), item.pap());
        criteria.eq(criteria.proto().pap().tenant().lease(), lease);
        criteria.eq(criteria.proto().billableItem().uid(), item.billableItem().uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);
        criteria.isNull(criteria.proto().pap().expiring());

        item.covered().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem papci : Persistence.secureQuery(criteria)) {
            item.covered().setValue(item.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = item.billableItem().agreedPrice().getValue();
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            item.percent().setValue(item.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            item.percent().setValue(BigDecimal.ONE);
        }

        return item;
    }

    private void updateCoveredItems(PreauthorizedPaymentDTO papDto) {
        papDto.coveredItems().clear();
        for (PreauthorizedPaymentCoveredItemDTO item : papDto.coveredItemsDTO()) {
            if (item.amount().getValue().compareTo(BigDecimal.ZERO) > 0) {
                papDto.coveredItems().add(item.duplicate(PreauthorizedPaymentCoveredItem.class));
            }
        }
    }

    private class PapConverter extends EntityDtoBinder<PreauthorizedPayment, PreauthorizedPaymentDTO> {

        protected PapConverter() {
            super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteDBO();
        }
    }
}
