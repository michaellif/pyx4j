/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationStatusSummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationStatusService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.ptapp.ApplicationManager;

public class ApplicationStatusServiceImpl extends ApplicationEntityServiceImpl implements ApplicationStatusService {

    private final static Logger log = LoggerFactory.getLogger(ApplicationStatusServiceImpl.class);

    @Override
    public void retrieveStatus(AsyncCallback<ApplicationStatusSummaryDTO> callback) {
        log.info("Retrieving Status and Summary for tenant {}", PtAppContext.getCurrentUserLeasePrimaryKey());

        // find master application:
        EntityQueryCriteria<MasterApplication> criteria = new EntityQueryCriteria<MasterApplication>(MasterApplication.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), PtAppContext.getCurrentUserLeasePrimaryKey()));
        MasterApplication ma = Persistence.service().retrieve(criteria);
        if ((ma == null) || (ma.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(MasterApplication.class).getCaption() + "' for lease "
                    + PtAppContext.getCurrentUserLeasePrimaryKey() + " NotFound");
        }

        // fill status data:
        ApplicationStatusSummaryDTO applicationStatusSummary = EntityFactory.create(ApplicationStatusSummaryDTO.class);
        applicationStatusSummary.status().set(ApplicationManager.calculateStatus(ma));

        // fill summary data:
        applicationStatusSummary.summary().set(new SummaryServiceImpl().retrieveData());
        callback.onSuccess(applicationStatusSummary);

        // filter out data for current tenant according it's role in application:
        // TODO !!!
    }
}
