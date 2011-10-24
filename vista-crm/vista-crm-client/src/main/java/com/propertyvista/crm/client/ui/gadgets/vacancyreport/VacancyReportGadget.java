/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-22
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.client.ui.gadgets.GadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.SummaryView.SummaryFilteringCriteria;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.TurnoverAnalysisView.TurnoverAnalysisFilteringCriteria;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.UnitStatusListView.UnitStatusListViewFilteringCriteria;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportGadgetSettings;

public class VacancyReportGadget extends GadgetBase implements IBuildingGadget {
    private static final boolean DEFAULT_UNTIS_VISIBILITY = true;

    private static final boolean DEFAULT_SUMMARY_VISIBLITY = true;

    private static final boolean DEFAULT_ANALYSIS_VISIBILITY = true;

    private final SummaryViewImpl summary;

    private final TurnoverAnalysisViewImpl turnoverAnalysis;

    private final UnitStatusListViewImpl unitStatusList;

    private final VerticalPanel panel;

    private CombinedFilteringCriteria filteringCriteria;

    private final UnitVacancyReportGadgetSettings settings;

    public VacancyReportGadget(GadgetMetadata gmd) {
        super(gmd);
        settings = gadgetMetadata.settings().cast();

        unitStatusList = new UnitStatusListViewImpl();
        summary = new SummaryViewImpl();
        turnoverAnalysis = new TurnoverAnalysisViewImpl();

        panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.add(unitStatusList);
        panel.add(summary);
        panel.add(turnoverAnalysis);

        unitStatusList.attachSettings(settings);
        turnoverAnalysis.attachSettings(settings);

        new VacancyReportGadgetPresenter(unitStatusList, summary, turnoverAnalysis);

        setFiltering(new FilterData());
    }

    @Override
    protected AbstractGadgetSettings createSettings() {
        UnitVacancyReportGadgetSettings settings = EntityFactory.create(UnitVacancyReportGadgetSettings.class);

        settings.showSummary().setValue(DEFAULT_SUMMARY_VISIBLITY);
        settings.showTurnoverAnalysis().setValue(DEFAULT_ANALYSIS_VISIBILITY);
        settings.showUnits().setValue(DEFAULT_UNTIS_VISIBILITY);

        return settings;
    }

    @Override
    public ISetup getSetup() {
        return new SetupImpl(this.settings, this);
    }

    @Override
    public void setBuilding(Key id) {
        // Just fake function.
        List<Key> s = new ArrayList<Key>();
        s.add(id);
        setBuildings(s);
    }

    @Override
    public void setBuildings(List<Key> ids) {
        // Also fake function! not to be used
        FilterData filter = new FilterData();
        setFiltering(filter);
    }

    @Override
    public void setFiltering(FilterData filter) {
        LogicalDate toDate;
        LogicalDate fromDate;

        if (filter.toDate != null) {
            toDate = new LogicalDate(filter.toDate);
        } else {
            toDate = new LogicalDate();
        }
        // we get non inclusive range [,) from the UI, but we think it's more intuitive to make it inclusive
        // hence we fix the 'to date' so points to end of the next day        
        toDate = new LogicalDate(TimeUtils.dayEnd(toDate));

        if (filter.fromDate != null) {
            fromDate = new LogicalDate(filter.fromDate.getTime());
        } else {
            long interval = TurnoverAnalysisViewImpl.DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX.addTo(toDate.getTime()) - toDate.getTime();
            fromDate = new LogicalDate(toDate.getTime() - UnitVacancyReportService.MAX_SUPPORTED_INTERVALS * interval);
        }

        Set<String> buildings = fakeBuildings(filter.buildings);

        CombinedFilteringCriteria filteringCriteria = new CombinedFilteringCriteria(buildings, fromDate, toDate);

        this.filteringCriteria = filteringCriteria;
        applyFilteringAndSettings();
    }

    private HashSet<String> fakeBuildings(List<Key> buildings) {
        final HashSet<String> fakeBuildings = new HashSet<String>();
        if (buildings == null || buildings.size() == 0) {
            // empty list should denote all buildings
        }
        if (buildings.size() == 1) {
            fakeBuildings.add("jean0200");
        } else if (buildings.size() > 1) {
            fakeBuildings.add("bath1650");
            fakeBuildings.add("com0164");
            fakeBuildings.add("chel3126");
        }
        return fakeBuildings;
    };

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.VacancyReport);
        gmd.name().setValue(GadgetType.VacancyReport.toString());
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void start() {
        super.start();
        applyFilteringAndSettings();
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    private void applyFilteringAndSettings() {
        // WARNING: the order is important! (setFilteringCriteria triggers population)
        unitStatusList.asWidget().setVisible(settings.showUnits().getValue());
        unitStatusList.setFilteringCriteria(filteringCriteria);

        summary.asWidget().setVisible(settings.showSummary().getValue());
        summary.setSummaryFilteringCriteria(filteringCriteria);

        turnoverAnalysis.asWidget().setVisible(settings.showTurnoverAnalysis().getValue());
        turnoverAnalysis.setFilteringCriteria(filteringCriteria);
    }

    private static class CombinedFilteringCriteria implements TurnoverAnalysisFilteringCriteria, UnitStatusListViewFilteringCriteria, SummaryFilteringCriteria {

        private final Set<String> buildings;

        private final LogicalDate from;

        private final LogicalDate to;

        public CombinedFilteringCriteria(Set<String> buildings, LogicalDate from, LogicalDate to) {
            this.buildings = buildings;
            this.from = from;
            this.to = to;
        }

        @Override
        public Set<String> getBuildingsFilteringCriteria() {
            return buildings;
        }

        @Override
        public LogicalDate getFrom() {
            return from;
        }

        @Override
        public LogicalDate getTo() {
            return to;
        }
    }

}
