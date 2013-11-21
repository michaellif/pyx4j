/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.portal.rpc.portal.prospect.dto.RentalSummaryDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;

public class ApplicationStatusServiceImpl implements ApplicationStatusService {

    private static final I18n i18n = I18n.get(ApplicationStatusServiceImpl.class);

    @Override
    public void retrieveMasterApplicationStatus(AsyncCallback<MasterOnlineApplicationStatus> callback) {
//        MasterOnlineApplicationStatus account = ServerSideFactory.create(OnlineApplicationFacade.class).calculateOnlineApplicationStatus(
//                to.leaseApplication().onlineApplication());

//        MasterOnlineApplication masterOnlineApplication = Persistence.service().retrieve(MasterOnlineApplication.class,
//                selectedApplication.masterOnlineApplication().getPrimaryKey());

        MasterOnlineApplicationStatus account = EntityFactory.create(MasterOnlineApplicationStatus.class);

        callback.onSuccess(account);
    }

    @Override
    public void retrieveRentalSummary(AsyncCallback<RentalSummaryDTO> callback) {
        // TODO Auto-generated method stub

    }

}
