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

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.client.ui.gadgets.vacancyreport.SummaryView.SummaryFilteringCriteria;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.TurnoverAnalysisView.TurnoverAnalysisFilteringCriteria;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.UnitStatusListView.UnitStatusListViewFilteringCriteria;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.VacancyReportService;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class VacancyReportGadgetPresenter implements UnitStatusListView.Presenter, SummaryView.Presenter, TurnoverAnalysisView.Presenter {
    private final VacancyReportService service;

    private final UnitStatusListView unitStatusListView;

    private final SummaryView summaryView;

    private final TurnoverAnalysisView turnoverAnalysisView;

    public VacancyReportGadgetPresenter(UnitStatusListView unitStatusListView, SummaryView summaryView, TurnoverAnalysisView turnoverAnalysisView,
            VacancyReportService service) {
        this.service = service;
        this.unitStatusListView = unitStatusListView;
        this.summaryView = summaryView;
        this.turnoverAnalysisView = turnoverAnalysisView;
        unitStatusListView.setPresenter(this);
        summaryView.setPresenter(this);
        turnoverAnalysisView.setPresenter(this);
    }

    public VacancyReportGadgetPresenter(UnitStatusListView unitStatusListView, SummaryView summaryView, TurnoverAnalysisView turnoverAnalysisView) {
        this(unitStatusListView, summaryView, turnoverAnalysisView, (VacancyReportService) GWT.create(VacancyReportService.class));
    }

    @Override
    public void populateUnitStatusList() {
        populateUnitStatusListPage(unitStatusListView.getPageNumber());
    }

    @Override
    public void nextUnitStatusListPage() {
        populateUnitStatusListPage(unitStatusListView.getPageNumber() + 1);
    }

    @Override
    public void prevUnitStatusListPage() {
        if (unitStatusListView.getPageNumber() > 0) {
            populateUnitStatusListPage(unitStatusListView.getPageNumber() - 1);
        }
    }

    private void populateUnitStatusListPage(final int pageNumber) {
        if (unitStatusListView.isEnabled()) {
            UnitStatusListViewFilteringCriteria filter = unitStatusListView.getUnitStatusListFilterCriteria();
            if (filter == null) {
                return;
            }
            service.unitStatusList(new AsyncCallback<EntitySearchResult<UnitVacancyStatus>>() {

                @Override
                public void onSuccess(EntitySearchResult<UnitVacancyStatus> result) {
                    if (pageNumber == 0 | !result.getData().isEmpty()) {
                        unitStatusListView.setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                    } else {
                        populateUnitStatusListPage(pageNumber - 1);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    unitStatusListView.reportError(caught);
                }
            }, new Vector<String>(filter.getBuildingsFilteringCriteria()), filter.getFrom(), filter.getTo(),
                    new Vector<Sort>(unitStatusListView.getUnitStatusListSortingCriteria()), pageNumber, unitStatusListView.getPageSize());
        }
    }

    @Override
    public void populateSummary() {
        if (summaryView.isEnabled()) {
            SummaryFilteringCriteria filter = summaryView.getSummaryFilteringCriteria();
            if (filter == null) {
                return;
            }
            service.summary(new AsyncCallback<UnitVacancyReportSummaryDTO>() {

                @Override
                public void onFailure(Throwable caught) {
                    summaryView.reportError(caught);
                }

                @Override
                public void onSuccess(UnitVacancyReportSummaryDTO result) {
                    summaryView.populateSummary(result);
                }
            }, new Vector<String>(filter.getBuildingsFilteringCriteria()), filter.getFrom(), filter.getTo());
        }
    }

    @Override
    public void populateTurnoverAnalysis() {
        if (turnoverAnalysisView.isEnabled()) {
            TurnoverAnalysisFilteringCriteria filter = turnoverAnalysisView.getTurnoverAnalysisFilteringCriteria();
            AnalysisResolution scale = turnoverAnalysisView.getSelectedResolution();
            if (filter == null | scale == null) {
                return;
            }
            service.turnoverAnalysis(new AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    turnoverAnalysisView.reportError(caught);
                }

                @Override
                public void onSuccess(Vector<UnitVacancyReportTurnoverAnalysisDTO> result) {
                    turnoverAnalysisView.setTurnoverAnalysisData(result);
                }
            }, new Vector<String>(filter.getBuildingsFilteringCriteria()), filter.getFrom(), filter.getTo(), scale);
        }
    }
}
