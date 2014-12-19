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
 */
package com.propertyvista.operations.server.security;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.domain.security.OperationsUserCredential;

public class OperationsUserAccountAccesRule implements DatasetAccessRule<OperationsUserCredential> {

    private static final long serialVersionUID = -8708570145392919006L;

    @Override
    public void applyRule(EntityQueryCriteria<OperationsUserCredential> criteria) {
        if (!SecurityController.check(VistaOperationsBehavior.SystemAdmin)) {
            criteria.eq(criteria.proto().user(), VistaContext.getCurrentUserPrimaryKey());
        }
    }

}
