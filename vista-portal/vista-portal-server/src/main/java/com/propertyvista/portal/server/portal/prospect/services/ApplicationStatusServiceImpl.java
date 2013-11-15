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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicationStatusDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.RentalSummaryDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;

public class ApplicationStatusServiceImpl implements ApplicationStatusService {

    private static final I18n i18n = I18n.get(ApplicationStatusServiceImpl.class);

    @Override
    public void retrieveApplicationStatus(AsyncCallback<ApplicationStatusDTO> callback) {
        ApplicationStatusDTO account = EntityFactory.create(ApplicationStatusDTO.class);
        callback.onSuccess(account);
    }

    @Override
    public void retrieveRentalSummary(AsyncCallback<RentalSummaryDTO> callback) {
        // TODO Auto-generated method stub

    }

}
