/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.DevSession;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.operations.domain.dev.DevelopmentUser;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.shared.config.VistaDemo;

public class DevelopmentSecurity {

    public static String OPENID_USER_EMAIL_ATTRIBUTE = "com.pyx4j.keep." + "openId.email";

    public static String OPENID_ACCESS_GRANTED_ATTRIBUTE = "access-granted";

    private static boolean hostQueryDone = false;

    private static DevelopmentUser developmentUserHostBased;

    public static boolean isDevelopmentAccessGranted() {
        DevSession devSession = DevSession.getSession();
        return (devSession.getAttribute(OPENID_ACCESS_GRANTED_ATTRIBUTE) == Boolean.TRUE);
    }

    public static String callNumberFilter(String number) {
        DevelopmentUser developmentUser = findDevelopmentUser();
        if (developmentUser == null) {
            return null;
        }
        if ((developmentUserHostBased != null) && (!developmentUser.testCallsOnHosts().isBooleanTrue())) {
            return null;
        }
        if ((number != null)
                && ((number.equals(developmentUser.homePhone().getValue())) || (number.equals(developmentUser.mobilePhone().getValue())) || (number
                        .equals(developmentUser.businessPhone().getValue())))) {
            return number;
        } else {
            // TODO remove Demo
            return developmentUser.mobilePhone().getValue();
        }
    }

    public static DevelopmentUser findDevelopmentUser() {
        final String requestNamespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            DevelopmentUser developmentUser = findByOpenId();
            if (developmentUser != null) {
                return developmentUser;
            }
            return findByHost();
        } finally {
            NamespaceManager.setNamespace(requestNamespace);
        }
    }

    private static DevelopmentUser findByHost() {
        if ((developmentUserHostBased != null) || (hostQueryDone)) {
            return developmentUserHostBased;
        }
        hostQueryDone = true;
        String host = SystemConfig.getLocalHostName();
        EntityQueryCriteria<DevelopmentUser> criteria = EntityQueryCriteria.create(DevelopmentUser.class);

        criteria.or(PropertyCriterion.eq(criteria.proto().host1(), host), new OrCriterion(PropertyCriterion.eq(criteria.proto().host2(), host),
                PropertyCriterion.eq(criteria.proto().host3(), host)));

        developmentUserHostBased = Persistence.service().retrieve(criteria);
        if (developmentUserHostBased != null) {
            return developmentUserHostBased;
        } else {
            return null;
        }
    }

    private static DevelopmentUser findByOpenId() {
        String email = (String) DevSession.getSession().getAttribute(OPENID_USER_EMAIL_ATTRIBUTE);
        if (email == null) {
            return null;
        }
        EntityQueryCriteria<DevelopmentUser> criteria = EntityQueryCriteria.create(DevelopmentUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        DevelopmentUser developmentUser = Persistence.service().retrieve(criteria);
        if (VistaDemo.isDemo() && (developmentUser == null)) {
            developmentUser = EntityFactory.create(DevelopmentUser.class);
            developmentUser.email().setValue(email);
            developmentUser.forwardAll().setValue(Boolean.TRUE);
        }
        return developmentUser;
    }
}
