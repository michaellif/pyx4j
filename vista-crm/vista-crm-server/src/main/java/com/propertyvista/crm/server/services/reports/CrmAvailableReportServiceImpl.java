/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.reports.CrmAvailableReportService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.reports.AvailableCrmReport;
import com.propertyvista.domain.reports.AvailableCrmReport.CrmReportType;

public class CrmAvailableReportServiceImpl implements CrmAvailableReportService {

    @Override
    public void obtainAvailableReportTypes(AsyncCallback<Vector<CrmReportType>> callback) {
        EntityQueryCriteria<AvailableCrmReport> criteria = EntityQueryCriteria.create(AvailableCrmReport.class);
        criteria.in(criteria.proto().roles().$().users(), CrmAppContext.getCurrentUser());
        Vector<CrmReportType> result = new Vector<>();

        for (AvailableCrmReport r : Persistence.service().query(criteria)) {
            result.add(r.reportType().getValue());
        }
        callback.onSuccess(result);
    }
}
