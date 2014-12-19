/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 2, 2013
 * @author vlads
 */
package com.propertyvista.site.server.security;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.site.rpc.services.PortalSiteServices;

public class VistaSiteAccessControlList extends ServletContainerAclBuilder {

    public VistaSiteAccessControlList() {
        grant(new IServiceExecutePermission(PortalSiteServices.class));

        freeze();
    }

}
