/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.server.security.VistaCustomerContext;

public class ProspectApplicationContext extends VistaCustomerContext {

    private final static Logger log = LoggerFactory.getLogger(ProspectApplicationContext.class);

    private static final I18n i18n = I18n.get(ProspectApplicationContext.class);

    public static ProspectApplicationAttributes getVisitAttributes() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        }
        ProspectApplicationAttributes attr = (ProspectApplicationAttributes) v.getAttribute("pt-visit");
        if (attr == null) {
            attr = new ProspectApplicationAttributes();
            v.setAttribute("pt-visit", attr);
        }
        return attr;
    }

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

    public static void setCurrentUserApplication(OnlineApplication application) {
        getVisitAttributes().setApplicationPrimaryKey(application.getPrimaryKey());
    }

    public static Key getCurrentUserApplicationPrimaryKey() {
        Key key = getVisitAttributes().getApplicationPrimaryKey();
        if (key == null) {
            log.trace("no application selected");
            throw new UserRuntimeException(i18n.tr("No Application Has Been Selected"));
        }
        return key;
    }

    public static OnlineApplication retrieveCurrentUserApplication() {
        return Persistence.service().retrieve(OnlineApplication.class, getCurrentUserApplicationPrimaryKey());
    }

    public static Lease retrieveCurrentUserLease() {
        return Persistence.service().retrieve(Lease.class, getCurrentUserLeaseIdStub().getPrimaryKey());
    }

    public static Customer retrieveCurrentUserCustomer() {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), ProspectApplicationContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }
}
