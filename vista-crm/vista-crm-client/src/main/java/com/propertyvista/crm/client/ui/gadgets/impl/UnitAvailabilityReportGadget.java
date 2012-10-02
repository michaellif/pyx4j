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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.UnitAvailabilityGadgetMetatadaForm;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilityReportGadget extends ListerGadgetInstanceBase<UnitAvailabilityStatus, UnitAvailabilityGadgetMetadata> implements
        IBuildingBoardGadgetInstance {

    private VerticalPanel gadgetPanel;

    private HTML filterDisplayPanel;

    private final AvailabilityReportService service;

    private CDatePicker asOf;

    public UnitAvailabilityReportGadget(UnitAvailabilityGadgetMetadata gmd) {
        super(gmd, UnitAvailabilityGadgetMetadata.class, new UnitAvailabilityGadgetMetatadaForm(), UnitAvailabilityStatus.class, false);
        service = GWT.create(AvailabilityReportService.class);
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
    protected Widget initContentPanel() {

        gadgetPanel = new VerticalPanel();
        gadgetPanel.add(initAsOfBannerPanel());
        gadgetPanel.add(initFilterDisplayPanel());
        gadgetPanel.add(initListerWidget());
        gadgetPanel.setWidth("100%");

        return gadgetPanel;
    }

    @Override
    protected void populatePage(final int pageNumber) {
        if (containerBoard.getSelectedBuildingsStubs() == null) {
            setAsOf(getStatusDate());
            setPageData(new Vector<UnitAvailabilityStatus>(), 0, 0, false);
            populateSucceded();
            return;
        }

        service.unitStatusList(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>>() {
            @Override
            public void onSuccess(EntitySearchResult<UnitAvailabilityStatus> result) {
                setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                redrawFilterDisplayPanel();
                populateSucceded();
            }

            @Override
            public void onFailure(Throwable caught) {
                populateFailed(caught);
            }
        }, new Vector<Building>(containerBoard.getSelectedBuildingsStubs()), getMetadata().filterPreset().getValue(), getStatusDate(), new Vector<Sort>(
                getListerSortingCriteria()), pageNumber, getPageSize());
    }

    private void setAsOf(LogicalDate statusDate) {
        asOf.setValue(statusDate);
    }

    private LogicalDate getStatusDate() {
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
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

    private Widget initFilterDisplayPanel() {
        filterDisplayPanel = new HTML();
        filterDisplayPanel.getElement().getStyle().setProperty("width", "100%");
        filterDisplayPanel.getElement().getStyle().setProperty("textAlign", "center");
        return filterDisplayPanel;
    }

    private void redrawFilterDisplayPanel() {
        filterDisplayPanel.setHTML(new SafeHtmlBuilder().appendEscaped(getMetadata().filterPreset().getValue().toString()).toSafeHtml());
    }
}