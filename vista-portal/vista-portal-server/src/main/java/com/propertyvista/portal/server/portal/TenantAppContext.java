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
package com.propertyvista.portal.server.portal;

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

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.server.common.security.VistaContext;

/**
 * This may be an optimization point for future.
 */
public class TenantAppContext extends VistaContext {

    private final static Logger log = LoggerFactory.getLogger(TenantAppContext.class);

    private static final I18n i18n = I18n.get(TenantAppContext.class);

    public static TenantUser getCurrentUser() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn()) || (v.getUserVisit().getPrincipalPrimaryKey() == null)) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        }
        TenantUser user = EntityFactory.create(TenantUser.class);
        user.setPrimaryKey(v.getUserVisit().getPrincipalPrimaryKey());
        user.name().setValue(v.getUserVisit().getName());
        user.email().setValue(v.getUserVisit().getEmail());
        return user;
    }

    public static Tenant getCurrentUserTenant() {
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), TenantAppContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }

    public static TenantInLease getCurrentUserTenantInLease() {
        EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant().user(), TenantAppContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }
}
