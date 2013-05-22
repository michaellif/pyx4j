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
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PreauthorizedPaymentsVisorServiceImpl implements PreauthorizedPaymentsVisorService {

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentsDTO dto = EntityFactory.create(PreauthorizedPaymentsDTO.class);

        dto.tenant().set(tenantId);
        dto.preauthorizedPayments().addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(tenantId));

        Tenant tenant = Persistence.secureRetrieve(Tenant.class, tenantId.getPrimaryKey());

        fillTenantInfo(dto, tenant);
        fillPreauthorizedPaymentItems(dto, tenant);
        fillAvailablePaymentMethods(dto, tenant);

        callback.onSuccess(dto);
    }

    private void fillPreauthorizedPaymentItems(PreauthorizedPaymentsDTO dto, Tenant tenant) {
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);

        for (PreauthorizedPayment pap : dto.preauthorizedPayments()) {
            fillPreauthorizedPaymentItem(pap, tenant);
        }
    }

    private void fillPreauthorizedPaymentItem(PreauthorizedPayment pap, Tenant tenant) {
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);

        if (!isCoveredItemExist(pap, tenant.lease().currentTerm().version().leaseProducts().serviceItem())) {
            pap.coveredItems().add(createCoveredItem(tenant.lease().currentTerm().version().leaseProducts().serviceItem()));
        }

        for (BillableItem billableItem : tenant.lease().currentTerm().version().leaseProducts().featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
                if (!ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().type().getValue())                                          // recursive
                    && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(new LogicalDate(SystemDateManager.getDate())))     // non-expired 
                    && !isCoveredItemExist(pap, billableItem)) {                                                                                                    // absent
                //@formatter:on
                pap.coveredItems().add(createCoveredItem(billableItem));
            }
        }
    }

    private boolean isCoveredItemExist(PreauthorizedPayment pap, BillableItem billableItem) {
        for (PreauthorizedPaymentCoveredItem item : pap.coveredItems()) {
            if (item.billableItem().uid().getValue().equals(billableItem.uid().getValue())) {
                return true;
            }
        }

        return false;
    }

    private PreauthorizedPaymentCoveredItem createCoveredItem(BillableItem billableItem) {
        PreauthorizedPaymentCoveredItem item = EntityFactory.create(PreauthorizedPaymentCoveredItem.class);

        item.billableItem().set(billableItem);
//        item.amount().setValue(billableItem.agreedPrice().getValue());

        return item;
    }

    private void fillTenantInfo(PreauthorizedPaymentsDTO pads, Tenant tenant) {
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);

        pads.tenantInfo().name().set(tenant.customer().person().name());

        EntityListCriteria<LeaseTermParticipant> criteria = new EntityListCriteria<LeaseTermParticipant>(LeaseTermParticipant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), tenant));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder(), tenant.lease().currentTerm()));

        LeaseTermParticipant<?> ltp = Persistence.service().retrieve(criteria);
        if (ltp != null) {
            pads.tenantInfo().role().setValue(ltp.role().getValue());
        }
    }

    private void fillAvailablePaymentMethods(PreauthorizedPaymentsDTO pads, Tenant tenant) {
        EntityListCriteria<LeasePaymentMethod> criteria = new EntityListCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), tenant.customer()));
        criteria.add(PropertyCriterion.eq(criteria.proto().isProfiledMethod(), Boolean.TRUE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        pads.availablePaymentMethods().addAll(Persistence.service().query(criteria));
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO pads) {
        // delete payment methods removed in UI:
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(pads.tenant())) {
            if (!pads.preauthorizedPayments().contains(pap)) {
                ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pap);
            }
        }

        // save new/edited ones:
        for (PreauthorizedPayment pap : pads.preauthorizedPayments()) {
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

            ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(pap, pads.tenant());
        }

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void create(AsyncCallback<PreauthorizedPayment> callback, Tenant tenantId) {
        PreauthorizedPayment pap = EntityFactory.create(PreauthorizedPayment.class);

        pap.tenant().set(tenantId);

        Tenant tenant = Persistence.secureRetrieve(Tenant.class, tenantId.getPrimaryKey());

        fillPreauthorizedPaymentItem(pap, tenant);

        callback.onSuccess(pap);
    }

    @Override
    public void delete(AsyncCallback<VoidSerializable> callback, PreauthorizedPayment pad) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pad);
        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void recollect(AsyncCallback<Vector<PreauthorizedPayment>> callback, Tenant tenantId) {
        callback.onSuccess(new Vector<PreauthorizedPayment>(ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(tenantId)));
    }
}
