/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionAll;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionRecommendationApprove;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionRecommendationFurtherMoreInfo;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationVerifyDoc;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.security.LeaseTermEditOnApplicationInstanceAccess;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionADC;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionMoreInfo;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDocumentSigning;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationOnlineApplication;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationReserveUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.dto.TenantFinancialDTO;

public class VistaCrmLeaseApplicationAccessControlList extends UIAclBuilder {

    VistaCrmLeaseApplicationAccessControlList() {

        {// Lead:
            List<Class<? extends IEntity>> entities = entities(Lead.class);
            grant(ApplicationBasic, entities, READ | UPDATE);
            grant(ApplicationFull, entities, ALL);
        }

        {// Application(Term) itself:
            List<Class<? extends IEntity>> entities = entities(LeaseApplicationDTO.class);
            grant(ApplicationBasic, entities, ALL);
            grant(ApplicationFull, entities, ALL);
            grant(ApplicationVerifyDoc, entities, READ | UPDATE);

            grant(ApplicationBasic, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), ALL);
            grant(ApplicationFull, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), ALL);
            grant(ApplicationVerifyDoc, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), READ | UPDATE);
        }

        {// Financial:
            List<Class<? extends IEntity>> entities = entities(TenantFinancialDTO.class);
            grant(ApplicationFull, entities, READ);
        }

        // Application Documents:
        grant(ApplicationFull, LeaseApplicationDocument.class, READ | UPDATE);
        grant(ApplicationVerifyDoc, LeaseApplicationDocument.class, READ | UPDATE);
        // signing Action:
        grant(ApplicationBasic, new ActionPermission(ApplicationDocumentSigning.class));
        grant(ApplicationFull, new ActionPermission(ApplicationDocumentSigning.class));
        grant(ApplicationVerifyDoc, new ActionPermission(ApplicationDocumentSigning.class));

        // Application Decisions:
        grant(ApplicationFull, new ActionPermission(ApplicationDecisionADC.class));
        grant(ApplicationFull, new ActionPermission(ApplicationDecisionMoreInfo.class));

        grant(ApplicationDecisionRecommendationApprove, new ActionPermission(ApplicationDecisionADC.class));
        grant(ApplicationDecisionRecommendationApprove, new ActionPermission(ApplicationDecisionMoreInfo.class));

        grant(ApplicationDecisionRecommendationFurtherMoreInfo, new ActionPermission(ApplicationDecisionADC.class));
        grant(ApplicationDecisionRecommendationFurtherMoreInfo, new ActionPermission(ApplicationDecisionMoreInfo.class));

        grant(ApplicationDecisionAll, new ActionPermission(ApplicationDecisionADC.class));
        grant(ApplicationDecisionAll, new ActionPermission(ApplicationDecisionMoreInfo.class));

        grant(ApplicationBasic, new ActionPermission(ApplicationOnlineApplication.class));
        grant(ApplicationFull, new ActionPermission(ApplicationOnlineApplication.class));

        grant(ApplicationFull, new ActionPermission(ApplicationReserveUnit.class));
    }
}
