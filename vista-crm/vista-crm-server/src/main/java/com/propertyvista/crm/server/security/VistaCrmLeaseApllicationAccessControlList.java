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

import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationAdvance;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationVerifyDoc;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionApprove;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionDecline;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionMoreInfo;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationStartOnlineApplication;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;

public class VistaCrmLeaseApllicationAccessControlList extends UIAclBuilder {

    VistaCrmLeaseApllicationAccessControlList() {

        {
            List<Class<? extends IEntity>> entities = entities(LeaseApplicationDTO.class, Lead.class);
            grant(ApplicationBasic, entities, ALL);

            grant(ApplicationBasic, new ActionPermission(ApplicationStartOnlineApplication.class));
        }

        // Application: Advance
        {
            List<Class<? extends IEntity>> entities = entities(LeaseApplicationDTO.class, Lead.class);
            grant(ApplicationAdvance, entities, ALL);

            grant(ApplicationAdvance, new ActionPermission(ApplicationStartOnlineApplication.class));
        }

        // Application: Verify Doc
        {
            List<Class<? extends IEntity>> entities = entities(LeaseApplicationDTO.class, LeaseApplicationDocument.class);
            grant(ApplicationVerifyDoc, entities, ALL);
        }

        // Application: Full
        {
            List<Class<? extends IEntity>> entities = entities(LeaseApplicationDTO.class, Lead.class, TenantFinancialDTO.class, LeaseApplicationDocument.class);
            grant(ApplicationFull, entities, ALL);

            grant(ApplicationFull, new ActionPermission(ApplicationStartOnlineApplication.class));

            grant(ApplicationFull, VistaCrmBehavior.ApplicationDecisionAll);
        }

        //-- Application Decisions:

        grant(VistaCrmBehavior.ApplicationDecisionRecommendationApprove, new ActionPermission(ApplicationDecisionApprove.class));
        grant(VistaCrmBehavior.ApplicationDecisionRecommendationFurtherMoreInfo, new ActionPermission(ApplicationDecisionMoreInfo.class));

        grant(VistaCrmBehavior.ApplicationDecisionAll, new ActionPermission(ApplicationDecisionApprove.class));
        grant(VistaCrmBehavior.ApplicationDecisionAll, new ActionPermission(ApplicationDecisionMoreInfo.class));
        grant(VistaCrmBehavior.ApplicationDecisionAll, new ActionPermission(ApplicationDecisionDecline.class));

    }
}
