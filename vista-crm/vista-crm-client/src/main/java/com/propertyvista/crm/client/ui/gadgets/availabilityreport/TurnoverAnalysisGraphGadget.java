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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.svg.gadgets.TurnoverAnalysisChartFactory;

public class TurnoverAnalysisGraphGadget extends AbstractGadget<TurnoverAnalysisMetadata> {

    private static final I18n i18n = I18n.get(TurnoverAnalysisGraphGadget.class);

    private static class TurnoverAnalysisGraphGadgetInstance extends GadgetInstanceBase<TurnoverAnalysisMetadata> implements IBuildingBoardGadgetInstance {

        private static final I18n i18n = I18n.get(TurnoverAnalysisGraphGadgetInstance.class);

        /** Sets graph height in <i>EMs</i>. */
        private static double GRAPH_HEIGHT = 20.0;

        /** Sets toolbar height in <i>EMs</i>. */
        private static double CONTROLS_HEIGHT = 3.5;

        private static final String MEASURE_SELECTOR_RADIO_GROUP_ID = "measureSelector";

        private static final boolean DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT = false;

        public static AnalysisResolution DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX = AnalysisResolution.Year;

        List<UnitTurnoversPerIntervalDTO> data;

        private SimplePanel chartPanel;

        private HorizontalPanel controls;

        private LayoutPanel layoutPanel;

        private RadioButton percent;

        private RadioButton number;

        private final AvailabilityReportService service;

        private CDatePicker asOf;

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
        public void setContainerBoard(BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public boolean isSetupable() {
            return true;
        }

        public void setTurnoverAnalysisData(List<UnitTurnoversPerIntervalDTO> data) {
            this.data = data;
            redraw();
        }

        private void redraw() {
            chartPanel.clear();

            if ((data == null || data.size() == 0)) {
                // TODO show something like "no data provided"/"no search criteria"?)
                return;
            }

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    int width = chartPanel.getElement().getClientWidth();
                    int height = chartPanel.getElement().getClientHeight();

                    chartPanel.add((Widget) new TurnoverAnalysisChartFactory(new SvgFactoryForGwt()).createChart(data, getMetadata()
                            .isTurnoverMeasuredByPercent().getValue(), width, height));
                    chartPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
                }
            });
        }

        private void doPopulate() {

            if (containerBoard.getSelectedBuildingsStubs() == null) {
                setTurnoverAnalysisData(null);
                populateSucceded();
                return;
            }

            final Vector<Key> buildingPks = new Vector<Key>(containerBoard.getSelectedBuildingsStubs().size());
            for (Building b : containerBoard.getSelectedBuildingsStubs()) {
                buildingPks.add(b.getPrimaryKey());
            }

            service.turnoverAnalysis(new AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }

                @Override
                public void onSuccess(Vector<UnitTurnoversPerIntervalDTO> result) {
                    setTurnoverAnalysisData(result);
                    populateSucceded();
                }
            }, buildingPks, getStatusDate());
        }

        private LogicalDate getStatusDate() {
            return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate();
        }

        private Widget initAsOfBannerPanel() {
            HorizontalPanel asForBannerPanel = new HorizontalPanel();
            asForBannerPanel.setWidth("100%");
            asForBannerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

            asOf = new CDatePicker();
            asOf.setValue(getStatusDate());
            asOf.setViewable(true);

            asForBannerPanel.add(asOf);
            return asForBannerPanel.asWidget();
        }

        @Override
        public Widget initContentPanel() {
            chartPanel = new SimplePanel();
            chartPanel.setSize("100%", "100%");

            controls = new HorizontalPanel();
            controls.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
            controls.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
            controls.getElement().getStyle().setPaddingLeft(2, Unit.EM);

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
            controls.add(w = new Label(i18n.tr("Scale") + ":"));
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

            layoutPanel.add(initAsOfBannerPanel());
            layoutPanel.add(chartPanel);
            layoutPanel.setWidgetTopHeight(chartPanel, 0, Unit.EM, GRAPH_HEIGHT, Unit.EM);
            layoutPanel.setWidgetLeftWidth(chartPanel, 0, Unit.PCT, 100, Unit.PCT);
            layoutPanel.add(controls);
            layoutPanel.setWidgetTopHeight(controls, GRAPH_HEIGHT, Unit.EM, CONTROLS_HEIGHT, Unit.EM);
            layoutPanel.setWidgetLeftWidth(controls, 0, Unit.PCT, 100, Unit.PCT);
            return layoutPanel;
        }

        @Override
        public ISetup getSetup() {
            return new SetupFormWrapper(new CEntityDecoratableForm<TurnoverAnalysisMetadata>(TurnoverAnalysisMetadata.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel p = new FormFlexPanel();
                    int row = -1;
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customizeDate())).build());
                    get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Boolean> event) {
                            if (event.getValue() != null) {
                                get(proto().asOf()).setVisible(event.getValue());
                            }
                        }
                    });
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).build());
                    get(proto().asOf()).setVisible(false);
                    return p;
                }

                @Override
                protected void onPopulate() {
                    super.onPopulate();
                    get(proto().asOf()).setVisible(getValue().customizeDate().isBooleanTrue());
                }

            });

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