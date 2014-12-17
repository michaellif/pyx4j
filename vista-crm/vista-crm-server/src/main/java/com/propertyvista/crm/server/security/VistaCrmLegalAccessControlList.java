/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.LegalCollectionsBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.LegalCollectionsFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.CREATE;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseCompletion;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseNotice;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.dto.LeaseLegalStateDTO;

class VistaCrmLegalAccessControlList extends UIAclBuilder {

    VistaCrmLegalAccessControlList() {

        // Actions:
        grant(LegalCollectionsBasic, LeaseNotice.class);

        grant(LegalCollectionsFull, LeaseNotice.class);
        grant(LegalCollectionsFull, LeaseCompletion.class);

        // Legal documents:
        grant(LegalCollectionsBasic, LegalLetter.class, READ);
        grant(LegalCollectionsBasic, LeaseLegalStateDTO.class, READ);
        grant(LegalCollectionsBasic, LegalNoticeCandidateDTO.class, READ);

        grant(LegalCollectionsFull, LegalLetter.class, READ | CREATE);
        grant(LegalCollectionsFull, LeaseLegalStateDTO.class, READ | UPDATE);
        grant(LegalCollectionsFull, LegalNoticeCandidateDTO.class, ALL);
    }
}
