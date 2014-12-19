/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 */
package com.propertyvista.portal.server.security.access.prospect;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class CustomerPreferencesDatasetAccessRule implements DatasetAccessRule<CustomerPreferences> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<CustomerPreferences> criteria) {
        criteria.eq(criteria.proto().customerUser(), PortalVistaContext.getCustomerUserIdStub());
    }

}
