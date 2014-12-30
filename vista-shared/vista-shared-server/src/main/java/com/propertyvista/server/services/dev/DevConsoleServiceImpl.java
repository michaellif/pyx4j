/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author vlads
 */
package com.propertyvista.server.services.dev;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.rpc.DevConsoleDataTO;
import com.propertyvista.shared.services.dev.DevConsoleService;

public class DevConsoleServiceImpl implements DevConsoleService {

    @Override
    public void obtainData(AsyncCallback<DevConsoleDataTO> callback) {
        DevConsoleDataTO to = EntityFactory.create(DevConsoleDataTO.class);
        to.crmUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.crm, true));
        to.residentUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true));
        to.prospectUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true));
        to.siteUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.site, true));
        callback.onSuccess(to);
    }

}
