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
package com.propertyvista.portal.server.portal.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class TenantPaymentMethodCrudServiceImpl extends AbstractCrudServiceImpl<PaymentMethod> implements TenantPaymentMethodCrudService {

    public TenantPaymentMethodCrudServiceImpl() {
        super(PaymentMethod.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<PaymentMethod> dbCriteria, EntityListCriteria<PaymentMethod> dtoCriteria) {
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().leaseParticipant(), TenantAppContext.getCurrentUserTenantInLease()));
    }

    @Override
    protected void persist(PaymentMethod entity, PaymentMethod dto) {
        Tenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();

        entity.leaseParticipant().set(tenantInLease);
        entity.isOneTimePayment().setValue(Boolean.FALSE);

        Persistence.service().retrieve(tenantInLease.leaseV());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit());
        Building building = tenantInLease.leaseV().holder().unit().belongsTo();
        ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(building, entity);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        Tenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseV());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit().belongsTo());
        AddressStructured address = tenantInLease.leaseV().holder().unit().belongsTo().info().address().duplicate();
        address.suiteNumber().set(tenantInLease.leaseV().holder().unit().info().number());
        callback.onSuccess(address);
    }
}
