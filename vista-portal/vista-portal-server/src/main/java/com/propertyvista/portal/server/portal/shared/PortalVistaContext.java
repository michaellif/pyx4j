/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;
import com.propertyvista.server.common.security.VistaContext;

public class PortalVistaContext extends VistaContext {

    public static CustomerUser getCustomerUserIdStub() {
        return Context.getUserVisit(CustomerUserVisit.class).getCurrentUser();
    }

    public static Customer getCustomer() {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), PortalVistaContext.getCustomerUserIdStub()));
        return Persistence.service().retrieve(criteria);
    }
}
