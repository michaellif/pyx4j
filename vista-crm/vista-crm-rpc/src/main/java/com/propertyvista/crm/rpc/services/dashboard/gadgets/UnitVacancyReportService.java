/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 5, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;

public interface UnitVacancyReportService extends AbstractCrudService<UnitVacancyReport> {
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, EntityQueryCriteria<UnitVacancyReport> criteria);

}
