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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsStatusGadgetMetadataForm;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsStatusGadget extends ListerGadgetInstanceBase<LeaseArrearsSnapshotDTO, ArrearsStatusGadgetMetadata> {

    private static final I18n i18n = I18n.get(ArrearsStatusGadget.class);

    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(CDatePicker.defaultDateFormat);

    private final ArrearsReportService service;

    private FormFlexPanel contentPanel;

    private HTML titleBannerLabel;

    public ArrearsStatusGadget(ArrearsStatusGadgetMetadata gmd) {
        super(gmd, ArrearsStatusGadgetMetadata.class, new ArrearsStatusGadgetMetadataForm(), LeaseArrearsSnapshotDTO.class, false);
        service = GWT.<ArrearsReportService> create(ArrearsReportService.class);
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
        contentPanel = new FormFlexPanel();
        contentPanel.setWidget(1, 0, initTitleBannerPanel());
        contentPanel.setWidget(2, 0, initListerWidget());
        return contentPanel;
    }

    private Widget initTitleBannerPanel() {
        HorizontalPanel titleBannerPanel = new HorizontalPanel();
        titleBannerPanel.setWidth("100%");
        titleBannerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

        titleBannerLabel = new HTML();
        titleBannerPanel.add(titleBannerLabel);
        return titleBannerPanel.asWidget();
    }

    @Override
    protected void populatePage(final int pageNumber) {
        if (containerBoard.getSelectedBuildingsStubs() == null) {
            refreshTitleBanner();
            setPageData(new Vector<LeaseArrearsSnapshotDTO>(), 0, 0, false);
            populateSucceded();
            return;
        } else {
            Vector<Building> buildings = new Vector<Building>(containerBoard.getSelectedBuildingsStubs());
            Vector<Sort> sortingCriteria = new Vector<Sort>(getListerSortingCriteria());

            service.leaseArrearsRoster(new DefaultAsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>>() {

                @Override
                public void onSuccess(EntitySearchResult<LeaseArrearsSnapshotDTO> result) {
                    refreshTitleBanner();
                    setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                    populateSucceded();
                }

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }

            }, buildings, getStatusDate(), getMetadata().category().getValue(), sortingCriteria, pageNumber, getMetadata().pageSize().getValue());
        }

    }

    private LogicalDate getStatusDate() {
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
    }

    private void refreshTitleBanner() {
        String unescaptedBanner = i18n.tr("{0} arrears as of {1}", getMetadata().category().getValue(), DATE_FORMAT.format(getStatusDate()));
        titleBannerLabel.setHTML(new SafeHtmlBuilder().appendEscaped(unescaptedBanner).toSafeHtml());
    }
}