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
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.BarChart2D;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;
import com.pyx4j.widgets.client.TabLayoutPanel;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportGadgetSettings;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class UnitVacancyReportGadget extends ListerGadgetBase<UnitVacancyReport> implements IBuildingGadget {
    public static final String TURNOVER_ANALYSIS_CAPTION = "Turnover Analysis";

    public static final String RESOLUTION_SELECTOR_LABEL = "Resolution";

    public static final String SUMMARY_CAPTION = "Summary";

    private static final RefreshInterval DEFAULT_REFRESH_INTERVAL = RefreshInterval.Never;

    private static final int DEFAULT_ITEMS_PER_PAGE = 5;

    private static final boolean DEFAULT_UNTIS_VISIBILITY = true;

    private static final boolean DEFAULT_SUMMARY_VISIBLITY = true;

    private static final boolean DEFAULT_ANALYSIS_VISIBILITY = true;

    private static final boolean DEFAULT_ANALYSIS_SHOW_PERCENT = false;

    public static final AnalysisResolution DEFAULT_ANALYSIS_RESOLUTION = AnalysisResolution.Year;

    private final VerticalPanel gadgetPanel;

    private final AnalysisView turnoverAnalysisView;

    private final SummaryView summaryView;

    private final UnitVacancyReportGadgetActivity activity;

    private List<Key> buildings;

    private LogicalDate fromDate;

    private LogicalDate toDate;

    private final UnitVacancyReportGadgetSettings settings;

    public UnitVacancyReportGadget(GadgetMetadata gmd) {
        super(gmd, (UnitVacancyReportService) GWT.create(UnitVacancyReportService.class), UnitVacancyReport.class);

        settings = gmd.settings().cast();
        summaryView = new SummaryView();
        summaryView.initContent();

        gadgetPanel = new VerticalPanel();

        gadgetPanel.add(getListerBase());
        gadgetPanel.add(summaryView.asWidget());

        turnoverAnalysisView = new AnalysisView(settings);
        gadgetPanel.add(turnoverAnalysisView.asWidget());
        activity = new UnitVacancyReportGadgetActivity(this, (UnitVacancyReportService) service);

        toDate = new LogicalDate(TimeUtils.dayEnd(TimeUtils.today()));
        fromDate = new LogicalDate(toDate.getTime() - UnitVacancyReportService.MAX_DATE_RANGE);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.UnitVacancyReport);
        gmd.name().setValue(GadgetType.UnitVacancyReport.toString());
    }

    @Override
    protected ListerGadgetBaseSettings createSettings() {
        UnitVacancyReportGadgetSettings settings = EntityFactory.create(UnitVacancyReportGadgetSettings.class);
        settings.refreshInterval().setValue(DEFAULT_REFRESH_INTERVAL);
        settings.itemsPerPage().setValue(DEFAULT_ITEMS_PER_PAGE);
        settings.currentPage().setValue(0);
        settings.columnPaths().clear();

        settings.showUnits().setValue(DEFAULT_UNTIS_VISIBILITY);
        settings.showSummary().setValue(DEFAULT_SUMMARY_VISIBLITY);
        settings.showTurnoverAnalysis().setValue(DEFAULT_ANALYSIS_VISIBILITY);

        settings.isTurnoverMeasuredByPercent().setValue(DEFAULT_ANALYSIS_SHOW_PERCENT);
        settings.turnoverAnalysisResolution().setValue(DEFAULT_ANALYSIS_RESOLUTION);
        return settings;
    }

    public UnitVacancyReportGadgetSettings getSettings() {
        return settings;
    }

    public IListerView<UnitVacancyReport> getListerView() {
        return getListerBase();
    }

    public SummaryView getUnitVacancyReportSummaryForm() {
        return summaryView;
    }

    public AnalysisView getAnalysisView() {
        return turnoverAnalysisView;
    }

    @Override
    public Widget asWidget() {

        return gadgetPanel;
    }

    public void populate() {
        activity.populateSummary();
        activity.populateTurnoverAnalysis();
    }

    @Override
    public void start() {
        getListerBase().asWidget().setVisible(getSettings().showUnits().getValue());
        summaryView.asWidget().setVisible(getSettings().showSummary().getValue());
        turnoverAnalysisView.asWidget().setVisible(getSettings().showTurnoverAnalysis().getValue());
        super.start();
        populate();
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<UnitVacancyReport>> columnDescriptors, UnitVacancyReport proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost()));
    }

    @Override
    protected void fillAvailableColumnDescripors(List<ColumnDescriptor<UnitVacancyReport>> columnDescriptors, UnitVacancyReport proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanMarketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.isScoped()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentReady()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaAbsolute()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaRelative()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveOutDay()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveInDay()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedFromDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost()));
    }

    @Override
    public ISetup getSetup() {
        return new SetupView();
    }

    @Override
    public void setBuilding(Key id) {
        List<Key> ids = new ArrayList<Key>(1);
        if (id != null) {
            ids.add(id);
        }
        setBuildings(ids);
    }

    @Override
    public void setBuildings(List<Key> ids) {
        List<Key> my = new ArrayList<Key>();
        if (ids != null) {
            for (Key id : ids) {
                my.add(id);
            }
        }
        buildings = my;
    }

    @Override
    public void setFiltering(FilterData filter) {
        fromDate = new LogicalDate(filter.fromDate.getTime());
        // we are making non inclusive range [,), but we think it's more intuitive to make it inclusive
        // hence we fix the to date to point to the beggining of the next day
        toDate = new LogicalDate(TimeUtils.dayEnd(filter.toDate));
        setBuildings(filter.buildings);
        stop();
        start();
    }

    private LogicalDate getToDate() {
        return toDate;
    }

    private LogicalDate getFromDate() {
        return fromDate;
    }

    @Override
    protected void onRefreshTimer() {
        super.onRefreshTimer();
        populate();
    }

    private static String toMonth(Date date) {
        // Since it seems that GWT doesn't include this awsome calendar classes
        // we are going to have to improvise (I hope I remember all the months names)
        // GregorianCalendar c = new GregorianCalendar();
        // return i18n.tr(c.getDisplayName(Calendar.MONTH, c.get(Calendar.MONTH), Locale.CANADA));

        // TODO use GWT GregorianCalendar if and when possible

        // TODO use lazy initalization to create the translated list of the months only once

        // TODO the someone to add MONTH_NAMES_LONG to the Time Utils
        String monthRepr = i18n.tr(TimeUtils.MONTH_NAMES_SHORT[date.getDay()]);

        return monthRepr.substring(0, Math.min(3, monthRepr.length()));
    }

    public class UnitVacancyReportGadgetActivity extends ListerActivityBase<UnitVacancyReport> {

        private final UnitVacancyReportService serivce;

        private final UnitVacancyReportGadget gadget;

        // TODO create interface for the gadget and pass interface to the constructor?
        public UnitVacancyReportGadgetActivity(UnitVacancyReportGadget gadget, UnitVacancyReportService service) {
            super(gadget.getListerView(), service, UnitVacancyReport.class);
            this.serivce = service;
            this.gadget = gadget;
            this.gadget.getAnalysisView().setPresenter(this);
        }

        @Override
        protected EntityListCriteria<UnitVacancyReport> constructSearchCriteria() {
            EntityListCriteria<UnitVacancyReport> criteria = super.constructSearchCriteria();

            if (buildings != null && !buildings.isEmpty()) {
                // FIXME this is the the part that should construct search criteria based on user selection in dashboard, but it's currently disabled because this gadget has only demo functionality 
                //Building building = EntityFactory.create(Building.class);
                //criteria.add(new PropertyCriterion(building.id().getPath().toString(), Restriction.IN, (Serializable) buildings));

                // TODO these are fake buildings for use with fake unit property report table, for demonstration purposes only
                final Collection<String> fakeBuildings = new HashSet<String>();
                if (buildings.size() == 1) {
                    fakeBuildings.add("bath1650");
                } else {
                    fakeBuildings.add("jean0200");
                    fakeBuildings.add("com0164");
                    fakeBuildings.add("chel3126");
                }

                criteria.add(new PropertyCriterion(getListerBase().proto().propertyCode().getPath().toString(), Restriction.IN, (Serializable) fakeBuildings));

            }
            criteria.add(new PropertyCriterion(getListerBase().proto().fromDate().getPath().toString(), Restriction.GREATER_THAN_OR_EQUAL, getFromDate()));
            criteria.add(new PropertyCriterion(getListerBase().proto().toDate().getPath().toString(), Restriction.LESS_THAN_OR_EQUAL, getToDate()));

            return criteria;

        }

        @Override
        public void populate(int pageNumber) {
            if (gadget != null && gadget.getSettings().showUnits().getValue()) {
                super.populate(pageNumber);
            }
        }

        public void populateSummary() {
            if (gadget == null || (gadget.getFromDate() == null | gadget.getToDate() == null)) {
                return;
            }
            if (!gadget.getSettings().showSummary().getValue()) {
                return;
            }

            serivce.summary(new AsyncCallback<UnitVacancyReportSummaryDTO>() {
                @Override
                public void onSuccess(UnitVacancyReportSummaryDTO result) {
                    gadget.getUnitVacancyReportSummaryForm().populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            }, constructSearchCriteria(), gadget.getFromDate(), gadget.getToDate());
        }

        public void populateTurnoverAnalysis() {
            if (gadget.getFromDate() == null | gadget.getToDate() == null) {
                return;
            }
            if (!gadget.getSettings().showTurnoverAnalysis().getValue()) {
                return;
            }

            serivce.turnoverAnalysis(new AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }

                @Override
                public void onSuccess(Vector<UnitVacancyReportTurnoverAnalysisDTO> result) {
                    gadget.getAnalysisView().setData(result);

                }
            }, gadget.getFromDate(), gadget.getToDate(), gadget.getAnalysisView().getResolution());
        }
    }

    public static class AnalysisView implements IsWidget {
        List<UnitVacancyReportTurnoverAnalysisDTO> data;

        private final SimplePanel graph;

        private final FlexTable controls;

        private final VerticalPanel layoutPanel;

        ListBox resolutionSelector;

        RadioButton percent;

        RadioButton number;

        FlowPanel measureSelection;

        UnitVacancyReportGadgetActivity presenter;

        private final UnitVacancyReportGadgetSettings settings;

        public AnalysisView(UnitVacancyReportGadgetSettings settings) {
            this.settings = settings;
            validateSettings();
            layoutPanel = new VerticalPanel();
            layoutPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
            layoutPanel.setWidth("100%");

            graph = new SimplePanel();

            controls = new FlexTable();

            resolutionSelector = new ListBox(false);

            AnalysisResolution[] analysisValues = AnalysisResolution.values();
            int selectedResolutionIndex = 0;
            for (int i = 0; i < analysisValues.length; ++i) {
                AnalysisResolution resolution = analysisValues[i];
                resolutionSelector.addItem(resolution.toString(), resolution.toString());
                if (resolution.equals(settings.turnoverAnalysisResolution().getValue())) {
                    selectedResolutionIndex = i;
                }
            }
            resolutionSelector.setItemSelected(selectedResolutionIndex, true);
            resolutionSelector.addChangeHandler(new ChangeHandler() {

                @Override
                public void onChange(ChangeEvent event) {
                    if (presenter != null) {
                        AnalysisResolution r = AnalysisResolution.representationToValue(resolutionSelector.getValue(resolutionSelector.getSelectedIndex()));
                        AnalysisView.this.settings.turnoverAnalysisResolution().setValue(r);
                        presenter.populateTurnoverAnalysis();
                    }
                }

            });
            controls.setWidget(0, 0, new Label(i18n.tr(RESOLUTION_SELECTOR_LABEL)));
            controls.setWidget(0, 1, resolutionSelector);

            ValueChangeHandler<Boolean> measureChangeHandler = new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    AnalysisView.this.settings.isTurnoverMeasuredByPercent().setValue(percent.getValue());
                    redraw();
                }
            };
            percent = new RadioButton("measureSelector", i18n.tr("%"));
            percent.addValueChangeHandler(measureChangeHandler);
            percent.setValue(getShowPercents(), false);
            number = new RadioButton("measureSelector", i18n.tr("#"));
            number.addValueChangeHandler(measureChangeHandler);
            number.setValue(!getShowPercents(), false);

            measureSelection = new FlowPanel();
            measureSelection.add(percent);
            measureSelection.add(number);

            Label caption = new Label(i18n.tr(TURNOVER_ANALYSIS_CAPTION));
            caption.setWidth("100%");

            layoutPanel.add(caption);
            layoutPanel.add(graph);
            layoutPanel.add(controls);
            layoutPanel.add(measureSelection);
        }

        public void validateSettings() {
            if (settings.isTurnoverMeasuredByPercent().isNull()) {
                settings.isTurnoverMeasuredByPercent().setValue(DEFAULT_ANALYSIS_SHOW_PERCENT);
            }
            if (settings.turnoverAnalysisResolution().isNull()) {
                settings.turnoverAnalysisResolution().setValue(DEFAULT_ANALYSIS_RESOLUTION);
            }
        }

        @Override
        public Widget asWidget() {
            return layoutPanel;
        }

        public AnalysisResolution getResolution() {
            return settings.turnoverAnalysisResolution().getValue();
        }

        public boolean getShowPercents() {
            return settings.isTurnoverMeasuredByPercent().getValue();
        }

        public void setPresenter(UnitVacancyReportGadgetActivity presenter) {
            this.presenter = presenter;
        }

        public void setData(List<UnitVacancyReportTurnoverAnalysisDTO> data) {
            this.data = data;
            redraw();
        }

        private void redraw() {
            if (data == null || data.size() == 0) {
                graph.clear();
                return;
            }
            DataSource ds = new DataSource();

            for (UnitVacancyReportTurnoverAnalysisDTO intervalData : data) {
                ArrayList<Double> values = new ArrayList<Double>();
                if (!getShowPercents()) {
                    values.add((double) intervalData.unitsTurnedOverAbs().getValue().intValue());
                } else {
                    values.add(intervalData.unitsTurnedOverPct().getValue());
                }
                if (getResolution() == AnalysisResolution.Month) {
                    // FIXME normal month format
                    // TODO localized month format?
                    // TODO month converter should be an external utility
                    String label = toMonth(intervalData.fromDate().getValue()) + "-" + Integer.toString(1900 + intervalData.fromDate().getValue().getYear());
                    ds.addDataSet(ds.new Metric(label), values);
                } else if (getResolution() == AnalysisResolution.Year) {
                    ds.addDataSet(ds.new Metric(Integer.toString(1900 + intervalData.toDate().getValue().getYear())), values);
                }
            }

            SvgFactory factory = new SvgFactoryForGwt();

            GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, 700, 200);
            config.setGridType(GridType.Both);
            config.setTheme(ChartTheme.Bright);
            config.setShowValueLabels(true);
            config.setZeroBased(false);

            SvgRoot svgroot = factory.getSvgRoot();
            svgroot.add(new BarChart2D(config));

            graph.clear();
            graph.add((Widget) svgroot);
            graph.setSize("700px", "200px");
            graph.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        }
    }

    public class SummaryView extends CrmEntityForm<UnitVacancyReportSummaryDTO> {

        public SummaryView() {
            super(UnitVacancyReportSummaryDTO.class, new CrmViewersComponentFactory());
        }

        @Override
        public IsWidget createContent() {
            final int WIDTH = 10;
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true);

            VerticalPanel p = new VerticalPanel();
            p.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
            p.setWidth("100%");
            Label caption = new Label(i18n.tr(SUMMARY_CAPTION));
            caption.setWidth("100%");
            p.add(caption);
            main.add(p);
            main.add(inject(proto().total()), WIDTH);
            main.add(inject(proto().netExposure()), WIDTH);
            main.add(new VistaLineSeparator());

            VistaDecoratorsSplitFlowPanel sp = new VistaDecoratorsSplitFlowPanel(true);

            sp.getLeftPanel().add(inject(proto().vacancyAbsolute()), WIDTH);
            sp.getLeftPanel().add(inject(proto().vacancyRelative()), WIDTH);
            sp.getLeftPanel().add(inject(proto().vacantRented()), WIDTH);
            sp.getLeftPanel().add(new VistaLineSeparator());
            sp.getLeftPanel().add(inject(proto().occupancyAbsolute()), WIDTH);
            sp.getLeftPanel().add(inject(proto().occupancyRelative()), WIDTH);

            sp.getRightPanel().add(inject(proto().noticeAbsolute()), WIDTH);
            sp.getRightPanel().add(inject(proto().noticeRelative()), WIDTH);
            sp.getRightPanel().add(inject(proto().noticeRented()), WIDTH);

            main.add(sp);

            return new CrmScrollPanel(main);
        }
    }

    private class SetupView implements ISetup {
        private static final double HEADER_HEIGHT = 3d;

        private final ISetup parentSetup;

        private final VerticalPanel mySetup;

        private final TabLayoutPanel combinedSetup;

        private final CheckBox showUnits;

        private final CheckBox showSummary;

        private final CheckBox showTurnoverAnalysis;

        public SetupView() {

            combinedSetup = new TabLayoutPanel(HEADER_HEIGHT, Unit.EM);

            parentSetup = UnitVacancyReportGadget.super.getSetup();
            combinedSetup.add(parentSetup.asWidget(), i18n.tr("General Settings"));

            showUnits = new CheckBox(i18n.tr("units"));
            showUnits.setValue(UnitVacancyReportGadget.this.getSettings().showUnits().getValue());

            showSummary = new CheckBox(i18n.tr("summary"));
            showSummary.setValue(UnitVacancyReportGadget.this.getSettings().showSummary().getValue());

            showTurnoverAnalysis = new CheckBox(i18n.tr("turnover analysis"));
            showTurnoverAnalysis.setValue(UnitVacancyReportGadget.this.getSettings().showTurnoverAnalysis().getValue());

            mySetup = new VerticalPanel();
            mySetup.setWidth("100%");
            mySetup.setHeight("100%");
            mySetup.add(showUnits);
            mySetup.add(showSummary);
            mySetup.add(showTurnoverAnalysis);
            mySetup.getElement().getStyle().setPaddingLeft(2, Unit.EM);
            mySetup.getElement().getStyle().setPaddingTop(1, Unit.EM);

            combinedSetup.add(mySetup, i18n.tr("Show/Hide"));
            combinedSetup.setWidth("100%");
            combinedSetup.setHeight("10em");
        }

        @Override
        public Widget asWidget() {
            return combinedSetup;
        }

        @Override
        public boolean onStart() {
            // parent restarts the Gadget (calls stop() and start())
            return parentSetup.onStart();
        }

        @Override
        public boolean onOk() {
            getSettings().showUnits().setValue(showUnits.getValue());
            getSettings().showSummary().setValue(showSummary.getValue());
            getSettings().showTurnoverAnalysis().setValue(showTurnoverAnalysis.getValue());
            return parentSetup.onOk();
        }

        @Override
        public void onCancel() {
            parentSetup.onCancel();
        }

    }
}
