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
package com.propertyvista.portal.server.portal.web.services;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.web.services.PaymentMethodCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class PaymentMethodCrudServiceImpl extends AbstractCrudServiceImpl<LeasePaymentMethod> implements PaymentMethodCrudService {

    public PaymentMethodCrudServiceImpl() {
        super(LeasePaymentMethod.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(LeasePaymentMethod entity, LeasePaymentMethod dto, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(entity, dto, retrieveTarget);

        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(TenantAppContext.getCurrentUserLease().billingAccount(),
                        VistaApplication.residentPortal));
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<LeasePaymentMethod> dbCriteria, EntityListCriteria<LeasePaymentMethod> dtoCriteria) {
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().customer().user(), TenantAppContext.getCurrentUser()));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isProfiledMethod(), Boolean.TRUE));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isDeleted(), Boolean.FALSE));

        // filter out not allowed payment types:
        Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(
                TenantAppContext.getCurrentUserLease().billingAccount(), VistaApplication.residentPortal);

        dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().type(), allowedPaymentTypes));
    }

    @Override
    protected void persist(LeasePaymentMethod entity, LeasePaymentMethod dto) {
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());

        entity.customer().set(TenantAppContext.getCurrentUserCustomer());
        entity.isProfiledMethod().setValue(Boolean.TRUE);

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), dto, VistaApplication.residentPortal);
        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(entity, lease.unit().building());
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        LeasePaymentMethod paymentMethod = Persistence.service().retrieve(entityClass, entityId);
        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);
        Persistence.service().commit();
        callback.onSuccess(Boolean.TRUE);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(TenantAppContext.getCurrentUserTenant()));
    }
}
