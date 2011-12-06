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
package com.propertyvista.crm.client.ui.gadgets.availabilityreport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.LineChart;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.client.ui.gadgets.util.TimeRange;
import com.propertyvista.crm.client.ui.gadgets.util.Tuple;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;

public class TurnoverAnalysisGraphGadget extends AbstractGadget<TurnoverAnalysisMetadata> {
    private static final I18n i18n = I18n.get(TurnoverAnalysisGraphGadget.class);

    private static class TurnoverAnalysisGraphGadgetInstance extends GadgetInstanceBase<TurnoverAnalysisMetadata> implements IBuildingGadget {
        private static final I18n i18n = I18n.get(TurnoverAnalysisGraphGadgetInstance.class);

        //@formatter:off
        /** Sets graph height in <i>EMs</i>. */
        private static double GRAPH_HEIGHT = 20.0;
        
        /** Sets toolbar height in <i>EMs</i>. */
        private static double CONTROLS_HEIGHT = 3.5;
               
    
        private static final String MEASURE_SELECTOR_RADIO_GROUP_ID = "measureSelector";
        private static final String RESOLUTION_SELECTOR_LABEL = "Resolution";
        private static final String SCALE_SELECTOR_LABEL = "Scale";
       
        private static final boolean DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT = false;
        public static AnalysisResolution DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX = AnalysisResolution.Year;
        
        @SuppressWarnings("unchecked")
        // used for graph scale auto selection and blacklisting of available scales upon filtering setup
        // the first one in the resolutions list is the default 
        private static final List<Tuple<Long, List<AnalysisResolution>>> RANGE_TO_ACCEPTED_RESOLUTIONS_MAP = Arrays.asList(
                    // keys must be in DESCENDING order, the list must end with a KEY = 0
                    // keys represent the LOWER BOUND of the range
                    new Tuple<Long, List<AnalysisResolution>>(21L * TimeRange.MONTH + 1l, Arrays.asList(AnalysisResolution.Year)),
                    new Tuple<Long, List<AnalysisResolution>>(20l * TimeRange.MONTH + 1l, Arrays.asList(AnalysisResolution.Year, AnalysisResolution.Month)),
                    new Tuple<Long, List<AnalysisResolution>>(TimeRange.YEAR + 1l, Arrays.asList(AnalysisResolution.Month)),
                    new Tuple<Long, List<AnalysisResolution>>(3L * TimeRange.MONTH +1l, Arrays.asList(AnalysisResolution.Month, AnalysisResolution.Week)),
                    new Tuple<Long, List<AnalysisResolution>>(31l * TimeRange.DAY + 1l, Arrays.asList(AnalysisResolution.Week)),                      
                    new Tuple<Long, List<AnalysisResolution>>(2L * TimeRange.DAY, Arrays.asList(AnalysisResolution.Day, AnalysisResolution.Week)),
                    new Tuple<Long, List<AnalysisResolution>>(0L, Arrays.asList(AnalysisResolution.Day)));
                
        List<UnitVacancyReportTurnoverAnalysisDTO> data;
    
        private SimplePanel graph;
    
        private HorizontalPanel controls;
    
        private LayoutPanel layoutPanel;
    
        private ListBox resolutionSelector;
        private RadioButton percent;
        private RadioButton number;
    
        private final AvailabilityReportService service;
    
        private AnalysisResolution currentDefaultResolution;
        //@formatter:on

        private FilterData filter;

        public TurnoverAnalysisGraphGadgetInstance(GadgetMetadata gmd) {
            super(gmd, TurnoverAnalysisMetadata.class);
            service = GWT.create(AvailabilityReportService.class);
            setDefaultPopulator(new Populator() {
                @Override
                public void populate() {
                    doPopulate();
                }
            });
            initView();
        }

        @Override
        protected TurnoverAnalysisMetadata createDefaultSettings(Class<TurnoverAnalysisMetadata> metadataClass) {
            TurnoverAnalysisMetadata settings = super.createDefaultSettings(metadataClass);
            settings.isTurnoverMeasuredByPercent().setValue(DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT);
            settings.turnoverAnalysisResolution().setValue(DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX);
            return settings;
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public boolean isSetupable() {
            return false;
        }

        @Override
        public void setFiltering(FilterData filterData) {
            this.filter = filterData;

            if (filter != null) {
                fillResolutionSelector();
                AnalysisResolution selected = getSelectedResolution();
                AnalysisResolution defaultResolution = getDefaultResolution(filter.fromDate, filter.toDate);
                if (selected == null || currentDefaultResolution != defaultResolution) {
                    setResolution(defaultResolution);
                    currentDefaultResolution = defaultResolution;
                }
            }
            populate();
        }

        /**
         * This method is supposed to be used to adjust the available scale (resolution) combo box for the selected filtering criteria.
         */
        private void fillResolutionSelector() {
            AnalysisResolution currentResolution = getSelectedResolution();

            resolutionSelector.clear();
            AnalysisResolution[] analysisValues = AnalysisResolution.values();
            int selectedResolutionIndex = -1;
            int added = -1;
            for (int i = 0; i < analysisValues.length; ++i) {
                AnalysisResolution resolution = analysisValues[i];
                if (isResolutionAcceptable(resolution)) {
                    resolutionSelector.addItem(resolution.toString(), resolution.toString());
                    ++added;
                }
                if (resolution.equals(currentResolution)) {
                    selectedResolutionIndex = added;
                }
            }

            if (selectedResolutionIndex >= 0) {
                resolutionSelector.setSelectedIndex(selectedResolutionIndex);
            }
        }

        private boolean isResolutionAcceptable(AnalysisResolution resolution) {
            if (filter.toDate == null | filter.fromDate == null) {
                return resolution == DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX;
            } else {
                long requestedTimerange = filter.toDate.getTime() - filter.fromDate.getTime();
                for (Tuple<Long, List<AnalysisResolution>> rangeToResolutions : RANGE_TO_ACCEPTED_RESOLUTIONS_MAP) {
                    if (requestedTimerange > rangeToResolutions.car()) {
                        return rangeToResolutions.cdr().contains(resolution);
                    }
                }
                return false;
            }
        }

        private static AnalysisResolution getDefaultResolution(Date from, Date to) {
            AnalysisResolution defaultScale = DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX;
            if (from == null | to == null) {
                return defaultScale;
            }

            long range = to.getTime() - from.getTime();
            for (Tuple<Long, List<AnalysisResolution>> defaultSetting : RANGE_TO_ACCEPTED_RESOLUTIONS_MAP) {
                if (range <= defaultSetting.car()) {
                    defaultScale = defaultSetting.cdr().get(0);
                    break;
                }
            }

            return defaultScale;
        }

        private void setResolution(AnalysisResolution resolution) {
            if (resolutionSelector.getItemCount() == 0) {
                resolutionSelector.addItem(resolution.toString(), resolution.toString());
                resolutionSelector.setSelectedIndex(0);
            } else if (resolution != null) {
                int resolutionsCount = resolutionSelector.getItemCount();
                for (int i = 0; i < resolutionsCount; ++i) {
                    if (resolution.equals(AnalysisResolution.representationToValue(resolutionSelector.getValue(i)))) {
                        resolutionSelector.setSelectedIndex(0);
                        break;
                    }
                }
            }

        }

        public AnalysisResolution getSelectedResolution() {
            int selected = resolutionSelector.getSelectedIndex();
            if (selected != -1) {
                return AnalysisResolution.representationToValue(resolutionSelector.getValue(selected));
            } else {
                return null;
            }
        }

        public boolean isTunoverMeasuredByPercent() {
            return percent.getValue();
        }

        public void setTurnoverAnalysisData(List<UnitVacancyReportTurnoverAnalysisDTO> data) {
            this.data = data;
            redraw();
        }

        private void redraw() {
            graph.clear();

            if ((data == null || data.size() == 0) | (filter == null)) {
                // TODO show something like "no data provided"/"no search criteria"?)
                return;
            }

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    DataSource ds = new DataSource();
                    for (UnitVacancyReportTurnoverAnalysisDTO intervalData : data) {
                        ArrayList<Double> values = new ArrayList<Double>();
                        if (!isTunoverMeasuredByPercent()) {
                            values.add((double) intervalData.unitsTurnedOverAbs().getValue().intValue());
                        } else {
                            values.add(intervalData.unitsTurnedOverPct().getValue());
                        }

                        ds.addDataSet(
                                ds.new Metric(getSelectedResolution().intervalLabelFormat(intervalData.fromDate().getValue(), intervalData.toDate().getValue())),
                                values);
                    }
                    SvgFactory factory = new SvgFactoryForGwt();
                    GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, graph.getElement().getClientWidth(), graph.getElement()
                            .getClientHeight());
                    config.setGridType(GridType.Both);
                    config.setTheme(ChartTheme.Bright);
                    config.setShowValueLabels(true);
                    config.setZeroBased(false);

                    SvgRoot svgroot = factory.getSvgRoot();
                    svgroot.add(new LineChart(config));

                    graph.add((Widget) svgroot);
                    graph.getElement().getStyle().setOverflow(Overflow.HIDDEN);
                }
            });
        }

        private void doPopulate() {
            AnalysisResolution scale = getSelectedResolution();
            if (filter == null | scale == null) {
                setTurnoverAnalysisData(null);
                populateSucceded();
                return;
            }
            service.turnoverAnalysis(new AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }

                @Override
                public void onSuccess(Vector<UnitVacancyReportTurnoverAnalysisDTO> result) {
                    setTurnoverAnalysisData(result);
                    populateSucceded();
                }
            }, new Vector<Key>(filter.buildings), filter.fromDate == null ? null : new LogicalDate(filter.fromDate), filter.toDate == null ? null
                    : new LogicalDate(filter.toDate), scale);
        }

        @Override
        public Widget initContentPanel() {
            graph = new SimplePanel();
            graph.setSize("100%", "100%");

            controls = new HorizontalPanel();
            controls.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
            controls.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
            controls.getElement().getStyle().setPaddingLeft(2, Unit.EM);

            resolutionSelector = new ListBox(false);
            resolutionSelector.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    populate();
                }
            });
            resolutionSelector.getElement().getStyle().setMarginLeft(0.5, Unit.EM);
            resolutionSelector.setWidth("5em");

            controls.add(new Label(i18n.tr(RESOLUTION_SELECTOR_LABEL) + ":"));
            controls.add(resolutionSelector);

            ValueChangeHandler<Boolean> measureChangeHandler = new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    getMetadata().isTurnoverMeasuredByPercent().setValue(percent.getValue());
                    redraw();
                }
            };

            percent = new RadioButton(MEASURE_SELECTOR_RADIO_GROUP_ID, i18n.tr("%"));
            percent.addValueChangeHandler(measureChangeHandler);
            percent.setValue(DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT, false);

            number = new RadioButton(MEASURE_SELECTOR_RADIO_GROUP_ID, i18n.tr("#"));
            number.addValueChangeHandler(measureChangeHandler);
            number.setValue(!percent.getValue(), false);

            FlowPanel measureSelection = new FlowPanel();
            measureSelection.getElement().getStyle().setMarginLeft(0.4, Unit.EM);
            measureSelection.add(percent);
            measureSelection.add(number);

            Widget w;
            controls.add(w = new Label(i18n.tr(SCALE_SELECTOR_LABEL) + ":"));
            w.getElement().getStyle().setMarginLeft(2, Unit.EM);
            controls.add(measureSelection);

            layoutPanel = new LayoutPanel() {
                // FIXME this supposed to cause automatic resizing of the graph, but unfortunately it doesn't: fix auto-resizing 
                @Override
                public void onResize() {
                    super.onResize();
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            redraw();
                        }
                    });
                }
            };

            layoutPanel.setSize("100%", defineHeight());

            layoutPanel.add(graph);
            layoutPanel.setWidgetTopHeight(graph, 0, Unit.EM, GRAPH_HEIGHT, Unit.EM);
            layoutPanel.setWidgetLeftWidth(graph, 0, Unit.PCT, 100, Unit.PCT);
            layoutPanel.add(controls);
            layoutPanel.setWidgetTopHeight(controls, GRAPH_HEIGHT, Unit.EM, CONTROLS_HEIGHT, Unit.EM);
            layoutPanel.setWidgetLeftWidth(controls, 0, Unit.PCT, 100, Unit.PCT);
            return layoutPanel;
        }

        @Override
        protected String defineHeight() {
            return "" + (GRAPH_HEIGHT + CONTROLS_HEIGHT) + "em";
        }

        @Override
        public void onResize() {
            super.onResize();
            redraw();
        }

        @Override
        public void onMaximize(boolean maximized_restored) {
            super.onMaximize(maximized_restored);
            redraw();
        }

        @Override
        public void onMinimize(boolean minimized_restored) {
            super.onMinimize(minimized_restored);
            redraw();
        }
    }

    public TurnoverAnalysisGraphGadget() {
        super(TurnoverAnalysisMetadata.class);
    }

    @Override
    public String getDescription() {
        return i18n.tr("A graph that visually demonstrates the turnover rate in either percentage or quantity over the course of multiple years");
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(i18n.tr("Availability"), i18n.tr("Chart"));
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<TurnoverAnalysisMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new TurnoverAnalysisGraphGadgetInstance(gadgetMetadata);
    }

}