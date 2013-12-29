/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 30, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.security.access.prospect;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class IdentificationDocumentFileProspectDatasetAccessRule implements DatasetAccessRule<IdentificationDocumentFile> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<IdentificationDocumentFile> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().owner().owner().holder().screene().user(), PortalVistaContext.getCustomerUserIdStub()));
    }

}
