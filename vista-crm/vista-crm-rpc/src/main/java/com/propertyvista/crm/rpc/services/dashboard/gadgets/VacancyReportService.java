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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus;

public interface VacancyReportService extends IService {

    public void turnoverAnalysis(AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>> callback, Vector<String> buidlings, LogicalDate fromDate,
            LogicalDate toDate, AnalysisResolution resolution);

    /**
     * 
     * @param callback
     * @param buildings
     * @param displayOccupied
     * @param displayVacant
     * @param displayNotice
     * @param displayRented
     * @param displayNotRented
     * @param from
     * @param to
     * @param sortingCriteria
     * @param pageNumber
     * @param pageSize
     */
    void unitStatusList(AsyncCallback<EntitySearchResult<UnitVacancyStatus>> callback, Vector<String> buildings, boolean displayOccupied,
            boolean displayVacant, boolean displayNotice, boolean displayRented, boolean displayNotRented, LogicalDate from, LogicalDate to,
            Vector<Sort> sortingCriteria, int pageNumber, int pageSize);

    void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<String> buildings, LogicalDate fromDate, LogicalDate toDate);

}
