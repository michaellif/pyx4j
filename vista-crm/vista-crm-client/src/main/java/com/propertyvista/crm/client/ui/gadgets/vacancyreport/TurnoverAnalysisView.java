/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-23
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public interface TurnoverAnalysisView extends IsWidget {
    public static final AnalysisResolution DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX = AnalysisResolution.Year;

    interface TurnoverAnalysisFilteringCriteria {
        /**
         * @return property codes of the building
         */
        Set<String> getBuildingsFilteringCriteria();

        LogicalDate getFrom();

        LogicalDate getTo();
    }

    interface Presenter {
        void populateTurnoverAnalysis();
    }

    void setPresenter(Presenter presenter);

    /**
     * @return resolution that was chosen or <code>null</code> if nothing is selected.
     */
    AnalysisResolution getSelectedResolution();

    boolean isTunoverMeasuredByPercent();

    void setTurnoverAnalysisData(List<UnitVacancyReportTurnoverAnalysisDTO> data);

    boolean isEnabled();

    void reportError(Throwable error);

    TurnoverAnalysisFilteringCriteria getTurnoverAnalysisFilteringCriteria();

}
