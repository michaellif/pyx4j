/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 16, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseProducts;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsCommons {

    public static void savePreauthorizedPayments(List<PreauthorizedPaymentDTO> papsDto, Tenant tenantId) {
        List<AutopayAgreement> paps = new ArrayList<AutopayAgreement>();
        for (PreauthorizedPaymentDTO papDTO : papsDto) {
            updateCoveredItems(papDTO);
            paps.add(new PapConverter().createBO(papDTO));
        }

        // delete payment methods removed in UI:
        for (AutopayAgreement pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(tenantId)) {
            if (!paps.contains(pap)) {
                ServerSideFactory.create(PaymentMethodFacade.class).deleteAutopayAgreement(pap);
            }
        }

        // save new/edited ones:
        for (AutopayAgreement pap : paps) {
            // remove zero covered items:
            Iterator<AutopayAgreementCoveredItem> iterator = pap.coveredItems().iterator();
            while (iterator.hasNext()) {
                AutopayAgreementCoveredItem item = iterator.next();
                if (item.amount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    iterator.remove();
                    if (item.getPrimaryKey() != null) {
                        Persistence.service().delete(item);
                    }
                }
            }

            ServerSideFactory.create(PaymentMethodFacade.class).persistAutopayAgreement(pap, tenantId);
        }
    }

    public static List<PreauthorizedPaymentDTO> createPreauthorizedPayments(Tenant tenantId) {
        Tenant tenant = Persistence.secureRetrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);

        List<PreauthorizedPaymentDTO> paps = new ArrayList<PreauthorizedPaymentDTO>();
        for (AutopayAgreement pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(tenantId)) {
            paps.add(createPreauthorizedPaymentDto(pap));
        }

        return paps;
    }

    public static PreauthorizedPaymentDTO createPreauthorizedPaymentDto(AutopayAgreement pap) {
        PreauthorizedPaymentDTO papDto = new PapConverter().createTO(pap);

        updateCoveredItemsDto(papDto);
        fillCoveredItemsDto(papDto);

        return papDto;
    }

    public static void fillCoveredItemsDto(PreauthorizedPaymentDTO papDto) {
        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(papDto.tenant().lease(), AttachLevel.Attached);

        Lease lease = papDto.tenant().lease();
        LeaseProducts products = lease.currentTerm().version().leaseProducts();

        if (products.serviceItem().agreedPrice().getValue().compareTo(BigDecimal.ZERO) > 0 && !isCoveredItemExist(papDto, products.serviceItem())) {
            papDto.coveredItemsDTO().add(createCoveredItemDto(products.serviceItem(), lease, papDto.getPrimaryKey() == null));
        }

        for (BillableItem billableItem : products.featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
            if (billableItem.agreedPrice().getValue().compareTo(BigDecimal.ZERO) > 0                                                                            // non-free
                && !ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().type().getValue())                                       // recursive
                && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(new LogicalDate(SystemDateManager.getDate())))     // non-expired 
                && !isCoveredItemExist(papDto, billableItem)) {                                                                                                 // absent
            //@formatter:on
                papDto.coveredItemsDTO().add(createCoveredItemDto(billableItem, lease, papDto.getPrimaryKey() == null));
            }
        }
    }

    public static boolean isCoveredItemExist(PreauthorizedPaymentDTO papDto, BillableItem billableItem) {
        for (AutopayAgreementCoveredItem item : papDto.coveredItemsDTO()) {
            if (item.billableItem().id().equals(billableItem.id())) {
                return true;
            }
        }
        return false;
    }

    public static PreauthorizedPaymentCoveredItemDTO createCoveredItemDto(BillableItem billableItem, Lease lease, boolean isNewPap) {
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
            itemDto.amount().setValue(isNewPap ? itemPrice.subtract(itemDto.covered().getValue()) : BigDecimal.ZERO);
            itemDto.percent().setValue(itemDto.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            itemDto.amount().setValue(BigDecimal.ZERO);
            itemDto.percent().setValue(BigDecimal.ONE);
        }

        itemDto.billableItem().set(billableItem);

        return itemDto;
    }

    public static void updateCoveredItemsDto(PreauthorizedPaymentDTO papDto) {
        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(papDto.tenant().lease(), AttachLevel.Attached);

        papDto.coveredItemsDTO().clear();
        for (AutopayAgreementCoveredItem item : papDto.coveredItems()) {
            PreauthorizedPaymentCoveredItemDTO itemDto = item.duplicate(PreauthorizedPaymentCoveredItemDTO.class);
            papDto.coveredItemsDTO().add(updateCoveredItemDto(itemDto, papDto.tenant().lease()));
        }
    }

    public static PreauthorizedPaymentCoveredItemDTO updateCoveredItemDto(PreauthorizedPaymentCoveredItemDTO itemDto, Lease lease) {
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

    public static void updateCoveredItems(PreauthorizedPaymentDTO papDto) {
        papDto.coveredItems().clear();
        for (PreauthorizedPaymentCoveredItemDTO itemDto : papDto.coveredItemsDTO()) {
            if (itemDto.amount().getValue().compareTo(BigDecimal.ZERO) > 0) {
                papDto.coveredItems().add(itemDto.duplicate(AutopayAgreementCoveredItem.class));
            }
        }
    }

    static class PapConverter extends EntityBinder<AutopayAgreement, PreauthorizedPaymentDTO> {

        protected PapConverter() {
            super(AutopayAgreement.class, PreauthorizedPaymentDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
        }
    }

}
