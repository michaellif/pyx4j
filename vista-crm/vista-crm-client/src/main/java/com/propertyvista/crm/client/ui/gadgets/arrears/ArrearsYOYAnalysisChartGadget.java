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

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
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

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChart;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ArrearsYOYAnalysisChartGadget extends AbstractGadget<ArrearsYOYAnalysisChart> {
    private static final I18n i18n = I18n.get(ArrearsYOYAnalysisChartGadget.class);

    public static class ArrearsYOYAnalysisChartGadgetImpl extends GadgetInstanceBase<ArrearsYOYAnalysisChart> implements IBuildingGadget {
        private static final I18n i18n = I18n.get(ArrearsYOYAnalysisChartGadgetImpl.class);

        /** Sets graph height in <i>EMs</i>. */
        private static final double GRAPH_HEIGHT = 20.0;

        // TODO make this constant setupable
        private static final int YEARS_TO_COMPARE = 3;

        FilterData filterData = null;

        LayoutPanel graph;

        ArrearsReportService service;

        Vector<Vector<Double>> data;

        private final SvgFactoryForGwt factory;

        public ArrearsYOYAnalysisChartGadgetImpl(GadgetMetadata gmd) {
            super(gmd, ArrearsYOYAnalysisChart.class);
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
        public void setFiltering(FilterData filterData) {
            this.filterData = filterData;
            populate();
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

        private void doPopulate() {
            if (filterData != null) {
                service.arrearsMonthlyComparison(new AsyncCallback<Vector<Vector<Double>>>() {
                    @Override
                    public void onSuccess(Vector<Vector<Double>> result) {
                        setData(result);
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                }, new Vector<Key>(filterData.buildings), YEARS_TO_COMPARE);
            } else {
                setData(null);
                populateSucceded();
            }
        }

        void setData(Vector<Vector<Double>> data) {
            this.data = data;
            redraw();
        }

        @SuppressWarnings("deprecation")
        private void redraw() {
            graph.clear();

            if (!(data == null || data.isEmpty())) {
                final DataSource ds = new DataSource();
                ds.setSeriesDescription(createSeriesDescription());

                Date monthConverted = new Date(1);
                for (int month = 0; month < 12; ++month) {
                    monthConverted.setMonth(month);
                    ds.addDataSet(ds.new Metric(TimeUtils.simpleFormat(monthConverted, "MMM")), data.get(month));
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

        private List<String> createSeriesDescription() {
            int lastYear = ((filterData != null) && (filterData.toDate != null) ? new LogicalDate(filterData.toDate) : new LogicalDate()).getYear() + 1900;
            int firstYear = lastYear - YEARS_TO_COMPARE + 1;
            List<String> description = new ArrayList<String>(lastYear - firstYear);
            for (int year = firstYear; year <= lastYear; ++year) {
                description.add(Integer.toString(year));
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
        super(ArrearsYOYAnalysisChart.class);
    }

    @Override
    public String getDescription() {
        return i18n.tr("A graph that visually demonstrates the arrear balance each month over the course of multiple years");
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<ArrearsYOYAnalysisChart> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsYOYAnalysisChartGadgetImpl(gadgetMetadata);
    }
}