/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;

/**
 * This may be an optimization point for future.
 */
public class TenantAppContext extends VistaCustomerContext {

    private final static Logger log = LoggerFactory.getLogger(TenantAppContext.class);

    private static final I18n i18n = I18n.get(TenantAppContext.class);

    public static CustomerUser getCurrentUser() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn()) || (v.getUserVisit().getPrincipalPrimaryKey() == null)) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        }
        CustomerUser user = EntityFactory.create(CustomerUser.class);
        user.setPrimaryKey(v.getUserVisit().getPrincipalPrimaryKey());
        user.name().setValue(v.getUserVisit().getName());
        user.email().setValue(v.getUserVisit().getEmail());
        return user;
    }

    public static Customer getCurrentUserCustomer() {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), TenantAppContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }

    public static Tenant getCurrentUserTenant() {
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), getCurrentUserLeaseIdStub()));
        criteria.add(PropertyCriterion.eq(criteria.proto().customer().user(), TenantAppContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }

    public static LeaseTermTenant getCurrentUserTenantInLease() {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), TenantAppContext.getCurrentUser());
        criteria.eq(criteria.proto().leaseParticipant().lease(), getCurrentUserLeaseIdStub());
        criteria.isCurrent(criteria.proto().leaseTermV());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        return Persistence.service().retrieve(criteria);
    }

    public static AptUnit getCurrentCustomerUnit() {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto()._Leases(), getCurrentUserLeaseIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static Lease getCurrentUserLease() {
        return Persistence.service().retrieve(Lease.class, getCurrentUserLeaseIdStub().getPrimaryKey());
    }

}
