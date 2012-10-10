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

import org.apache.commons.lang.Validate;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PaymentMethodCrudServiceImpl extends AbstractCrudServiceImpl<PaymentMethod> implements PaymentMethodCrudService {

    public PaymentMethodCrudServiceImpl() {
        super(PaymentMethod.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<PaymentMethod> dbCriteria, EntityListCriteria<PaymentMethod> dtoCriteria) {
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().customer().user(), TenantAppContext.getCurrentUser()));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isOneTimePayment(), Boolean.FALSE));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isDeleted(), Boolean.FALSE));
    }

    @Override
    protected void enhanceListRetrieved(PaymentMethod entity, PaymentMethod dto) {
        dto.isPreauthorized().setValue(entity.equals(TenantAppContext.getCurrentUserTenantInLease().leaseCustomer().preauthorizedPayment()));
        super.enhanceListRetrieved(entity, dto);
    }

    @Override
    protected void enhanceRetrieved(PaymentMethod entity, PaymentMethod dto, RetrieveTraget retrieveTraget) {
        dto.isPreauthorized().setValue(entity.equals(TenantAppContext.getCurrentUserTenantInLease().leaseCustomer().preauthorizedPayment()));
        super.enhanceRetrieved(entity, dto, retrieveTraget);
    }

    @Override
    protected void persist(PaymentMethod entity, PaymentMethod dto) {
        Tenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());

        entity.customer().set(tenantInLease.leaseCustomer().customer());

        Validate.isTrue(PaymentType.avalableInPortal().contains(entity.type().getValue()));
        entity.isOneTimePayment().setValue(Boolean.FALSE);

        ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(tenantInLease.leaseTermV().holder().lease().unit().building(), entity);

        if (dto.isPreauthorized().isBooleanTrue() || tenantInLease.leaseCustomer().preauthorizedPayment().isNull()) {
            if (!tenantInLease.leaseCustomer().preauthorizedPayment().equals(entity)) {
                tenantInLease.leaseCustomer().preauthorizedPayment().set(entity);
                Persistence.service().merge(tenantInLease.leaseCustomer());
            }
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        PaymentMethod paymentMethod = Persistence.service().retrieve(entityClass, entityId);
        ServerSideFactory.create(PaymentFacade.class).deletePaymentMethod(paymentMethod);
        Persistence.service().commit();
        callback.onSuccess(Boolean.TRUE);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        Tenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().building());

        AddressStructured address = tenantInLease.leaseTermV().holder().lease().unit().building().info().address().duplicate();
        address.suiteNumber().set(tenantInLease.leaseTermV().holder().lease().unit().info().number());
        callback.onSuccess(address);
    }
}
