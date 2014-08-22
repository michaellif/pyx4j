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
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionApprove;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionReserveUnit;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionStartOnline;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionSubmit;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationDecisionVerify;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.ApplicationVerifyDoc;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.security.LeaseTermEditOnApplicationInstanceAccess;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationCancel;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationApprove;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationMoreInfo;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDocumentSigning;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationOnlineApplication;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationReserveUnit;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationSubmit;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationVerify;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.dto.TenantFinancialDTO;

class VistaCrmLeaseApplicationAccessControlList extends UIAclBuilder {

    VistaCrmLeaseApplicationAccessControlList() {

        {// Lead:
            List<Class<? extends IEntity>> entities = entities(Lead.class);
            grant(ApplicationBasic, entities, READ | UPDATE);
            grant(ApplicationFull, entities, ALL);
        }

        {// Application(Term) itself:
            List<Class<? extends IEntity>> entities = entities(LeaseApplicationDTO.class);
            grant(ApplicationBasic, entities, READ);
            grant(ApplicationBasic, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), READ);

            grant(ApplicationFull, entities, ALL);
            grant(ApplicationFull, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), ALL);

            grant(ApplicationDecisionFull, entities, ALL);
            grant(ApplicationDecisionFull, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), ALL);

            grant(ApplicationVerifyDoc, entities, READ);
            grant(ApplicationVerifyDoc, LeaseTermDTO.class, new LeaseTermEditOnApplicationInstanceAccess(), READ);

        }

        {// Financial:
            List<Class<? extends IEntity>> entities = entities(TenantFinancialDTO.class);
            grant(ApplicationFull, entities, READ);
        }

        // Application Documents:
        grant(ApplicationBasic, LeaseApplicationDocument.class, READ);
        grant(ApplicationFull, LeaseApplicationDocument.class, READ);
        grant(ApplicationVerifyDoc, LeaseApplicationDocument.class, READ);
        // signing Action:
        grant(ApplicationBasic, ApplicationDocumentSigning.class);
        grant(ApplicationFull, ApplicationDocumentSigning.class);
        grant(ApplicationVerifyDoc, ApplicationDocumentSigning.class);

        // Application Decisions:
        grant(ApplicationDecisionSubmit, ApplicationSubmit.class);
        grant(ApplicationDecisionFull, ApplicationSubmit.class);

        grant(ApplicationDecisionVerify, ApplicationVerify.class);
        grant(ApplicationDecisionFull, ApplicationVerify.class);

        grant(ApplicationDecisionApprove, ApplicationApprove.class);
        grant(ApplicationDecisionFull, ApplicationApprove.class);

        grant(ApplicationDecisionVerify, ApplicationMoreInfo.class);
        grant(ApplicationDecisionApprove, ApplicationMoreInfo.class);
        grant(ApplicationDecisionFull, ApplicationMoreInfo.class);

        grant(ApplicationDecisionStartOnline, ApplicationCancel.class);
        grant(ApplicationDecisionReserveUnit, ApplicationCancel.class);
        grant(ApplicationDecisionSubmit, ApplicationCancel.class);
        grant(ApplicationDecisionVerify, ApplicationCancel.class);
        grant(ApplicationDecisionApprove, ApplicationCancel.class);
        grant(ApplicationDecisionFull, ApplicationCancel.class);

        // Reserve unit and start Online:        
        grant(ApplicationDecisionReserveUnit, ApplicationReserveUnit.class);
        grant(ApplicationDecisionFull, ApplicationReserveUnit.class);

        grant(ApplicationDecisionStartOnline, ApplicationOnlineApplication.class);
        grant(ApplicationDecisionFull, ApplicationOnlineApplication.class);
    }
}
