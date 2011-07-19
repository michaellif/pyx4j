/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.propertyvista.crm.rpc.services.ApplicationTenantCrudService;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;

public class ApplicationTenantCrudServiceImpl extends GenericCrudServiceImpl<PotentialTenantInfo> implements ApplicationTenantCrudService {

    public ApplicationTenantCrudServiceImpl() {
        super(PotentialTenantInfo.class);
    }

}
