/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.property.asset.building.Building;

public interface ArrearsReportService extends IService {

    static int YOY_ANALYSIS_CHART_MAX_YEARS_AGO = 10;

    void leaseArrearsRoster(AsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>> callback, Vector<Building> selectedBuildingsStubs, LogicalDate asOf,
            ARCode.Type arrearsCategory, Vector<Sort> sortingCriteria, int pageNumber, int pageSize);

    void arrearsMonthlyComparison(AsyncCallback<ArrearsYOYComparisonDataDTO> callback, Vector<Building> buildings, int yearsAgo);
}
