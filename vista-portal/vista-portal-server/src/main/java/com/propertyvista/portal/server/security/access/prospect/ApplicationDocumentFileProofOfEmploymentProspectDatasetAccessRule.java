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

import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFolder;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class ApplicationDocumentFileProofOfEmploymentProspectDatasetAccessRule implements
        DatasetAccessRule<ApplicationDocumentFile<ProofOfEmploymentDocumentFolder>> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<ApplicationDocumentFile<ProofOfEmploymentDocumentFolder>> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().owner().owner().owner().holder().screene().user(), PortalVistaContext.getCustomerUserIdStub()));
    }

}
