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
package com.propertyvista.portal.server.access;

import com.propertyvista.config.SystemConfig;
import com.propertyvista.portal.server.access.openId.OpenIdServlet;
import com.propertyvista.server.domain.dev.DevelopmentUser;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

public class DevelopmentSecurity {

    private static boolean hostQueryDone = false;

    private static DevelopmentUser developmentUserHostBased;

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

    private static DevelopmentUser findDevelopmentUser() {
        DevelopmentUser developmentUser = findByHost();
        if (developmentUser != null) {
            return developmentUser;
        }
        return findByOpenId();
    }

    private static DevelopmentUser findByHost() {
        if ((developmentUserHostBased != null) || (hostQueryDone)) {
            return developmentUserHostBased;
        }
        hostQueryDone = true;
        String host = SystemConfig.instance().getLocalHostName();
        EntityQueryCriteria<DevelopmentUser> criteria = EntityQueryCriteria.create(DevelopmentUser.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().host1(), host));
        developmentUserHostBased = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (developmentUserHostBased != null) {
            return developmentUserHostBased;
        }

        // TODO add OR to MySQL criteria
        criteria.getFilters().clear();
        criteria.add(PropertyCriterion.eq(criteria.proto().host2(), host));
        developmentUserHostBased = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (developmentUserHostBased != null) {
            return developmentUserHostBased;
        }

        criteria.getFilters().clear();
        criteria.add(PropertyCriterion.eq(criteria.proto().host3(), host));
        developmentUserHostBased = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (developmentUserHostBased != null) {
            return developmentUserHostBased;
        }

        return null;
    }

    private static DevelopmentUser findByOpenId() {
        String email = (String) Context.getVisit().getAttribute(OpenIdServlet.USER_EMAIL_ATTRIBUTE);
        if (email == null) {
            return null;
        }
        EntityQueryCriteria<DevelopmentUser> criteria = EntityQueryCriteria.create(DevelopmentUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        return PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
    }
}
