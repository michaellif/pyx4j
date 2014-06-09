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

import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionApprove;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionDecline;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionMoreInfo;
import com.propertyvista.crm.rpc.services.lease.ac.UpdateFromYardi;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class VistaCrmLeaseApllicationAccessControlList extends UIAclBuilder {

    VistaCrmLeaseApllicationAccessControlList() {

        grant(VistaCrmBehavior.ApllicationBasic, new ActionPermission(UpdateFromYardi.class));

        //--

        grant(VistaCrmBehavior.ApplicationDecisionRecommendationApprove, new ActionPermission(ApplicationDecisionApprove.class));
        grant(VistaCrmBehavior.ApplicationDecisionRecommendationFurtherMoreInfo, new ActionPermission(ApplicationDecisionMoreInfo.class));

        grant(VistaCrmBehavior.ApplicationDecisionAll, new ActionPermission(ApplicationDecisionApprove.class));
        grant(VistaCrmBehavior.ApplicationDecisionAll, new ActionPermission(ApplicationDecisionMoreInfo.class));
        grant(VistaCrmBehavior.ApplicationDecisionAll, new ActionPermission(ApplicationDecisionDecline.class));

    }
}
