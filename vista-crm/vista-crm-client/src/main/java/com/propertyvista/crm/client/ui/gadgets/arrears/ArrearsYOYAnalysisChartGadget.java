/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.BarChart2D;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChart;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsComparisonDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsValueDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsYOYAnalysisChartGadget extends AbstractGadget<ArrearsYOYAnalysisChartMetadata> {

    public static class ArrearsYOYAnalysisChartGadgetInstance extends GadgetInstanceBase<ArrearsYOYAnalysisChartMetadata> implements
            IBuildingBoardGadgetInstance {

        private static final I18n i18n = I18n.get(ArrearsYOYAnalysisChartGadgetInstance.class);

        /** Sets graph height in <i>EMs</i>. */
        private static final double GRAPH_HEIGHT = 20.0;

        private LayoutPanel graph;

        private final ArrearsReportService service;

        private ArrearsYOYComparisonDataDTO data;

        private final SvgFactoryForGwt factory;

        public ArrearsYOYAnalysisChartGadgetInstance(GadgetMetadata gmd) {
            super(gmd, ArrearsYOYAnalysisChartMetadata.class, new ArrearsYoyAnalysisGadgetMetadataForm());
            service = GWT.create(ArrearsReportService.class);
            data = null;
            factory = new SvgFactoryForGwt();
            setDefaultPopulator(new Populator() {
                @Override
                public void populate() {
                    doPopulate();
                }
            });
        }

        @Override
        protected ArrearsYOYAnalysisChartMetadata createDefaultSettings(Class<ArrearsYOYAnalysisChartMetadata> metadataClass) {
            ArrearsYOYAnalysisChartMetadata settings = super.createDefaultSettings(metadataClass);
            settings.yearsToCompare().setValue(3);
            return settings;
        }

        @Override
        public Widget initContentPanel() {
            graph = new LayoutPanel() {
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

            graph.setSize("100%", defineHeight());
            graph.getElement().getStyle().setOverflow(Overflow.HIDDEN);

            return graph;
        }

        @Override
        public void setContainerBoard(BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        private void doPopulate() {
            if (containerBoard.getSelectedBuildingsStubs() != null) {
                service.arrearsMonthlyComparison(new AsyncCallback<ArrearsYOYComparisonDataDTO>() {

                    @Override
                    public void onSuccess(ArrearsYOYComparisonDataDTO result) {
                        setYoyAnalysisData(result);
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }

                }, new Vector<Building>(containerBoard.getSelectedBuildingsStubs()), getMetadata().yearsToCompare().getValue());
                return;
            } else {
                setYoyAnalysisData(null);
                populateSucceded();
            }
        }

        private void setYoyAnalysisData(ArrearsYOYComparisonDataDTO data) {
            this.data = data;
            redraw();
        }

        private void redraw() {
            graph.clear();

            if (!(data == null || data.isEmpty())) {

                final DataSource ds = new DataSource();
                ds.setSeriesDescription(createSeriesDescription());

                for (ArrearsComparisonDTO comparison : data.comparisonsByMonth()) {
                    ds.addDataSet(asMetric(ds, comparison), asValues(comparison));
                }

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        SvgRoot svgroot = factory.getSvgRoot();
                        svgroot.add(createChart(ds));
                        graph.add((Widget) svgroot);
                    }
                });
            } else {
                // TODO make it look better
                // TODO maybe this kind of functionality should be provided by the base class
                Label noData = new Label(i18n.tr("Data unavailable"));
                graph.add(noData);
            }

        }

        private DataSource.Metric asMetric(DataSource ds, ArrearsComparisonDTO comparison) {
            Date month = new Date(0, comparison.month().getValue() + 1, 0);
            String monthLabel = TimeUtils.simpleFormat(month, "MMM");
            return ds.new Metric(monthLabel);
        }

        private List<Double> asValues(ArrearsComparisonDTO comparison) {
            List<Double> values = new ArrayList<Double>();
            for (ArrearsValueDTO value : comparison.values()) {
                values.add(value.totalArrears().getValue().doubleValue());
            }
            return values;
        }

        private List<String> createSeriesDescription() {
            ArrearsComparisonDTO comparison = data.comparisonsByMonth().get(0);
            List<String> description = new ArrayList<String>();
            for (ArrearsValueDTO value : comparison.values()) {
                description.add(Integer.toString(value.year().getValue()));
            }
            return description;
        }

        private GridBasedChartConfigurator createConfig(DataSource ds) {
            GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, graph.getElement().getClientWidth(), graph.getElement()
                    .getClientHeight());
            config.setGridType(GridType.Both);
            config.setTheme(ChartTheme.Bright);
            config.setShowValueLabels(false);
            config.setLegend(true);
            config.setZeroBased(false);
            return config;
        }

        private GridBasedChart createChart(DataSource ds) {
            return new BarChart2D(createConfig(ds));
        }

        @Override
        protected String defineHeight() {
            return "" + GRAPH_HEIGHT + "em";
        }

        @Override
        public void onResize() {
            super.onResize();
            redraw();
        }

    }

    public ArrearsYOYAnalysisChartGadget() {
        super(ArrearsYOYAnalysisChartMetadata.class);
    }

    @Override
    public java.util.List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Arrears.toString(), Directory.Categories.Chart.toString());
    };

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<ArrearsYOYAnalysisChartMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsYOYAnalysisChartGadgetInstance(gadgetMetadata);
    }
}