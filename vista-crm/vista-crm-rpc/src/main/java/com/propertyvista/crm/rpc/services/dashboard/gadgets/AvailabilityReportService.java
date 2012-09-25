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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public interface AvailabilityReportService extends IService {

    void turnoverAnalysis(AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>> callback, Vector<Key> buidlings, LogicalDate reportDate);

    void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>> callback, Vector<Key> buildings,
            UnitAvailabilityGadgetMetadata.FilterPreset filterPreset, LogicalDate when, Vector<Sort> sortingCriteria, int pageNumber, int pageSize);

    void summary(AsyncCallback<UnitAvailabilityReportSummaryDTO> callback, Vector<Key> buildings, LogicalDate toDate);

    void unitStatusSummary(AsyncCallback<Vector<UnitAvailabilityStatusSummaryLineDTO>> callback, Vector<Building> buildings, LogicalDate asOf);

}
