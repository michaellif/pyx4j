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

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsYoyAnalysisGadgetMetadataForm;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.svg.gadgets.ArrearsYoyAnalysisChartFactory;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsYOYAnalysisChartGadget extends GadgetInstanceBase<ArrearsYOYAnalysisChartGadgetMetadata> implements IBuildingBoardGadgetInstance {

    private static final I18n i18n = I18n.get(ArrearsYOYAnalysisChartGadget.class);

    /** Sets graph height in <i>EMs</i>. */
    private static final double GRAPH_HEIGHT = 20.0;

    private LayoutPanel graphPanel;

    private final ArrearsReportService service;

    private ArrearsYOYComparisonDataDTO data;

    private final SvgFactoryForGwt factory;

    public ArrearsYOYAnalysisChartGadget(ArrearsYOYAnalysisChartGadgetMetadata gmd) {
        super(gmd, ArrearsYOYAnalysisChartGadgetMetadata.class, new ArrearsYoyAnalysisGadgetMetadataForm());
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
    public Widget initContentPanel() {
        graphPanel = new LayoutPanel() {
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

        graphPanel.setSize("100%", defineHeight());
        graphPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

        return graphPanel;
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
        graphPanel.clear();

        if (!(data == null || data.isEmpty())) {

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {

                    int width = graphPanel.getElement().getClientWidth();
                    int height = graphPanel.getElement().getClientHeight();

                    graphPanel.add((Widget) new ArrearsYoyAnalysisChartFactory(factory).createChart(data, width, height));
                }
            });
        } else {
            // TODO make it look better
            // TODO maybe this kind of functionality should be provided by the base class
            Label noData = new Label(i18n.tr("Data unavailable"));
            graphPanel.add(noData);
        }

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