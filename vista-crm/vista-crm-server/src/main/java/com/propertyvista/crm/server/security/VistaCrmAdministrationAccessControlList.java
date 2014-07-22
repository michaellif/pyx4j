/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.AdminContent;
import static com.propertyvista.domain.security.VistaCrmBehavior.AdminFinancial;
import static com.propertyvista.domain.security.VistaCrmBehavior.AdminGeneral;

import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationAccess;

class VistaCrmAdministrationAccessControlList extends UIAclBuilder {

    VistaCrmAdministrationAccessControlList() {
        grant(AdminGeneral, CrmAdministrationAccess.class);
        grant(AdminFinancial, CrmAdministrationAccess.class);
        grant(AdminContent, CrmAdministrationAccess.class);
    }

}
