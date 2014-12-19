/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 */
package com.propertyvista.crm.server.services.selections.version;

import com.propertyvista.crm.rpc.services.selections.version.ServiceVersionService;
import com.propertyvista.domain.financial.offering.Service.ServiceV;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.versioning.AbstractVistaVersionDataListServiceImpl;

public class ServiceVersionServiceImpl extends AbstractVistaVersionDataListServiceImpl<ServiceV, ServiceV> implements ServiceVersionService {

    public ServiceVersionServiceImpl() {
        super(ServiceV.class, ServiceV.class, CrmUser.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }
}
