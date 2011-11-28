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

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class TenantPaymentMethodCrudServiceImpl extends AbstractCrudServiceImpl<PaymentMethod> implements TenantPaymentMethodCrudService {

    public TenantPaymentMethodCrudServiceImpl() {
        super(PaymentMethod.class);
    }

    @Override
    protected void enhanceListRetrieved(PaymentMethod entity) {
        entity.creditCard().number().setValue("XXXX XXX XXXX " + entity.creditCard().numberRefference().getValue());

    }

    @Override
    protected void enhanceRetrieved(PaymentMethod entity) {
        entity.creditCard().number().setValue("XXXX XXX XXXX " + entity.creditCard().numberRefference().getValue());
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<PaymentMethod>> callback, EntityListCriteria<PaymentMethod> criteria) {
        criteria = EntityListCriteria.create(PaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), TenantAppContext.getCurrentUserTenant()));
        EntitySearchResult<PaymentMethod> result = new EntitySearchResult<PaymentMethod>();
        for (PaymentMethod entity : Persistence.service().query(criteria)) {
            enhanceListRetrieved(entity);
            result.add(entity);
        }
        callback.onSuccess(result);
    }

    @Override
    protected void persist(PaymentMethod entity) {
        entity.tenant().set(TenantAppContext.getCurrentUserTenant());
        String ccn = entity.creditCard().number().getValue().trim();
        entity.creditCard().numberRefference().setValue(ccn.substring(ccn.length() - 4));
        Persistence.service().persist(entity);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        TenantInLease tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.lease());
        Persistence.service().retrieve(tenantInLease.lease().unit());
        Persistence.service().retrieve(tenantInLease.lease().unit().belongsTo());
        AddressStructured address = tenantInLease.lease().unit().belongsTo().info().address().cloneEntity();
        address.suiteNumber().set(tenantInLease.lease().unit().info().number());
        callback.onSuccess(address);
    }
}
