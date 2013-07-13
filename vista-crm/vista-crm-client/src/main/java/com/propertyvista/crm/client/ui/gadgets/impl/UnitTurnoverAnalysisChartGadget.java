/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitTurnoverAnalysisGadgetService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.UnitTurnoverAnalysisGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.svg.gadgets.TurnoverAnalysisChartFactory;

public class UnitTurnoverAnalysisChartGadget extends GadgetInstanceBase<UnitTurnoverAnalysisGadgetMetadata> implements IBuildingBoardGadgetInstance,
        ProvidesResize {

    private static final I18n i18n = I18n.get(UnitTurnoverAnalysisChartGadget.class);

    /** Sets graph height in <i>EMs</i>. */
    private static double GRAPH_HEIGHT = 20.0;

    private static final String MEASURE_SELECTOR_RADIO_GROUP_ID = "measureSelector";

    private static final boolean DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT = false;

    public static AnalysisResolution DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX = AnalysisResolution.Year;

    List<UnitTurnoversPerIntervalDTO> data;

    private SimplePanel chartPanel;

    private HorizontalPanel controls;

    private RadioButton percent;

    private RadioButton number;

    private Label asOf;

    private final UnitTurnoverAnalysisGadgetService service;

    public UnitTurnoverAnalysisChartGadget(UnitTurnoverAnalysisGadgetMetadata gmd) {
        super(gmd, UnitTurnoverAnalysisGadgetMetadata.class);
        service = GWT.create(UnitTurnoverAnalysisGadgetService.class);
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                doPopulate();
            }
        });
        initView();
    }

    @Override
    public void setContainerBoard(IBuildingFilterContainer board) {
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
        asOf.setText(i18n.tr("As of Date: {0,date,short}", getStatusDate()));
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

                chartPanel.add((Widget) new TurnoverAnalysisChartFactory(new SvgFactoryForGwt()).createChart(data, getMetadata().isTurnoverMeasuredByPercent()
                        .getValue(), width, height));
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
        }, new Vector<Building>(containerBoard.getSelectedBuildingsStubs()), getStatusDate());
    }

    private LogicalDate getStatusDate() {
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
    }

    private Widget initAsOfBannerPanel() {
        FlowPanel asOfBannerPanel = new FlowPanel();
        asOfBannerPanel.setWidth("100%");
        asOf = new Label();
        asOf.getElement().getStyle().setProperty("marginLeft", "auto");
        asOf.getElement().getStyle().setProperty("marginRight", "auto");
        asOfBannerPanel.add(asOf);

        return asOfBannerPanel;
    }

    @Override
    public Widget initContentPanel() {
        chartPanel = new SimplePanel();
        chartPanel.setSize("100%", "" + GRAPH_HEIGHT + "em");

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

        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        panel.add(initAsOfBannerPanel());
        panel.add(chartPanel);
        panel.add(controls);

        return panel;
    }

    @Override
    public ISetup getSetup() {
        CEntityDecoratableForm<UnitTurnoverAnalysisGadgetMetadata> form = new CEntityDecoratableForm<UnitTurnoverAnalysisGadgetMetadata>(
                UnitTurnoverAnalysisGadgetMetadata.class) {
            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel p = new TwoColumnFlexFormPanel();
                int row = -1;
                p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().refreshInterval())).build());
                p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customizeDate())).build());
                get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        if (event.getValue() != null) {
                            get(proto().asOf()).setVisible(event.getValue());
                        }
                    }
                });
                p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().asOf())).build());
                get(proto().asOf()).setVisible(false);
                return p;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                get(proto().asOf()).setVisible(getValue().customizeDate().isBooleanTrue());
            }

        };
        form.initContent();
        return new SetupFormWrapper(form);
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