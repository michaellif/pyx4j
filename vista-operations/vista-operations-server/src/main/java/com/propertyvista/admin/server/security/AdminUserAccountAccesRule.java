/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.server.security;

import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.server.common.security.VistaContext;

public class AdminUserAccountAccesRule implements DatasetAccessRule<AdminUserCredential> {

    @Override
    public void applyRule(EntityQueryCriteria<AdminUserCredential> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), VistaContext.getCurrentUserPrimaryKey()));
    }

}
