/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.util.Collection;

import org.apache.commons.lang.Validate;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PaymentMethodCrudServiceImpl extends AbstractCrudServiceImpl<LeasePaymentMethod> implements PaymentMethodCrudService {

    public PaymentMethodCrudServiceImpl() {
        super(LeasePaymentMethod.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<LeasePaymentMethod> dbCriteria, EntityListCriteria<LeasePaymentMethod> dtoCriteria) {
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().customer().user(), TenantAppContext.getCurrentUser()));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isProfiledMethod(), Boolean.TRUE));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isDeleted(), Boolean.FALSE));

        // filter out not allowed payment types:
        LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());

        Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(
                tenantInLease.leaseTermV().holder().lease().billingAccount(), VistaApplication.resident);

        dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().type(), allowedPaymentTypes));
    }

    @Override
    protected void enhanceListRetrieved(LeasePaymentMethod entity, LeasePaymentMethod dto) {
        dto.isPreauthorized().setValue(entity.equals(TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().preauthorizedPayment()));
        super.enhanceListRetrieved(entity, dto);
    }

    @Override
    protected void enhanceRetrieved(LeasePaymentMethod entity, LeasePaymentMethod dto, RetrieveTraget retrieveTraget) {
        dto.isPreauthorized().setValue(entity.equals(TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().preauthorizedPayment()));
        super.enhanceRetrieved(entity, dto, retrieveTraget);
    }

    @Override
    protected void persist(LeasePaymentMethod entity, LeasePaymentMethod dto) {
        LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());

        entity.customer().set(tenantInLease.leaseParticipant().customer());

        Validate.isTrue(PaymentType.avalableInPortal().contains(entity.type().getValue()));
        Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(
                tenantInLease.leaseTermV().holder().lease().billingAccount(), VistaApplication.resident);

        // save just allowed methods here:
        if (allowedPaymentTypes.contains(entity.type().getValue())) {
            entity.isProfiledMethod().setValue(Boolean.TRUE);

            ServerSideFactory.create(PaymentMethodFacade.class)
                    .persistLeasePaymentMethod(entity, tenantInLease.leaseTermV().holder().lease().unit().building());

            if (dto.isPreauthorized().isBooleanTrue() || tenantInLease.leaseParticipant().preauthorizedPayment().isNull()) {
                if (!tenantInLease.leaseParticipant().preauthorizedPayment().equals(entity)) {
                    tenantInLease.leaseParticipant().preauthorizedPayment().set(entity);
                    Persistence.service().merge(tenantInLease.leaseParticipant());
                }
            }
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        LeasePaymentMethod paymentMethod = Persistence.service().retrieve(entityClass, entityId);
        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);
        Persistence.service().commit();
        callback.onSuccess(Boolean.TRUE);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().building());

        AddressStructured address = tenantInLease.leaseTermV().holder().lease().unit().building().info().address().duplicate();
        address.suiteNumber().set(tenantInLease.leaseTermV().holder().lease().unit().info().number());
        callback.onSuccess(address);
    }
}
