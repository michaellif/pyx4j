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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsStatusGadgetMetadataForm;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.Provider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsStatusGadget extends GadgetInstanceBase<ArrearsStatusGadgetMetadata> {

    private static final I18n i18n = I18n.get(ArrearsStatusGadget.class);

    private static final List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        LeaseArrearsSnapshotDTO proto = EntityFactory.getEntityPrototype(LeaseArrearsSnapshotDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().propertyCode()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().name()).title(i18n.ntr("Building")).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().streetNumber()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().streetName()).visible(false).build(),                    
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().province().name()).visible(false).title(i18n.ntr("Province")).build(),                    
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().country().name()).visible(false).title(i18n.ntr("Country")).build(),                    
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().complex().name()).visible(false).title(i18n.ntr("Complex")).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().info().number()).title(i18n.ntr("Unit")).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseId()).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseFrom()).build(),
                new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseTo()).build(),
                
                // arrears
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucketCurrent()).build(),
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucketThisMonth()).build(),
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucket30()).build(),
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucket60()).build(),
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucket90()).build(),
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucketOver90()).build(),
                
                new MemberColumnDescriptor.Builder(proto.selectedBuckets().arrearsAmount()).build()
        //TODO calculate CREDIT AMOUNT                    
        //        column(proto.selectedBuckets().creditAmount()).build(),
        //        column(proto.selectedBuckets().totalBalance()).build(),
        //TODO calculate LMR                    
        //        column(proto.lmrToUnitRentDifference()).build()                
        );//@formatter:on
    }

    private final ArrearsReportService service;

    private TwoColumnFlexFormPanel contentPanel;

    private HTML titleBannerLabel;

    private DataTablePanel<LeaseArrearsSnapshotDTO> dataTablePanel;

    private int pageNumber;

    public ArrearsStatusGadget(ArrearsStatusGadgetMetadata gmd) {
        super(gmd, ArrearsStatusGadgetMetadata.class, new ArrearsStatusGadgetMetadataForm());
        service = GWT.<ArrearsReportService> create(ArrearsReportService.class);
        setDefaultPopulator(new Populator() {

            @Override
            public void populate() {
                ArrearsStatusGadget.this.populatePage(pageNumber);
            }

        });
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
        contentPanel = new TwoColumnFlexFormPanel();
        contentPanel.setWidget(1, 0, initTitleBannerPanel());
        contentPanel.setWidget(2, 0, initDataTablePanel());
        return contentPanel;
    }

    private Widget initDataTablePanel() {
        dataTablePanel = new DataTablePanel<LeaseArrearsSnapshotDTO>(LeaseArrearsSnapshotDTO.class);

        ListerUtils.<LeaseArrearsSnapshotDTO>bind(dataTablePanel)//@formatter:off
        .columnDescriptors(DEFAULT_COLUMN_DESCRIPTORS)
        .setupable(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey()))
        .userSettingsProvider(new Provider<ListerUserSettings>() {
            @Override
            public ListerUserSettings get() {
                return getMetadata().arrearsStatusListerSettings();
            }
         })
        .onColumnSelectionChanged(new Command() {
            @Override
            public void execute() {
                saveMetadata();
            }
        })
        .init();
        //@formatter:on

        dataTablePanel.setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                populate(false);
            }
        });
        dataTablePanel.setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber = 0;
                populate(false);
            }
        });
        dataTablePanel.setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                if (pageNumber != 0) {
                    --pageNumber;
                    populate(false);
                }
            }
        });
        dataTablePanel.setNextActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber += 1;
                populate(false);
            }
        });
        dataTablePanel.setLastActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber = (dataTablePanel.getDataTableModel().getTotalRows() - 1) / getMetadata().arrearsStatusListerSettings().pageSize().getValue();
                populate(false);
            }
        });
        dataTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<LeaseArrearsSnapshotDTO>() {
            @Override
            public void onChange(ColumnDescriptor column) {
                populate(false);
            }
        });

        return dataTablePanel;
    }

    private Widget initTitleBannerPanel() {
        HorizontalPanel titleBannerPanel = new HorizontalPanel();
        titleBannerPanel.setWidth("100%");
        titleBannerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

        titleBannerLabel = new HTML();
        titleBannerPanel.add(titleBannerLabel);
        return titleBannerPanel.asWidget();
    }

    private void populatePage(final int pageNumber) {
        Vector<Building> buildings = new Vector<Building>(containerBoard.getSelectedBuildingsStubs());
        Vector<Sort> sortingCriteria = new Vector<Sort>(dataTablePanel.getDataTable().getDataTableModel().getSortCriteria());

        service.leaseArrearsRoster(new DefaultAsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>>() {

            @Override
            public void onSuccess(EntitySearchResult<LeaseArrearsSnapshotDTO> result) {
                redrawTitleBanner();
                dataTablePanel.setPageSize(getMetadata().arrearsStatusListerSettings().pageSize().getValue());
                dataTablePanel.populateData(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
                populateSucceded();
            }

            @Override
            public void onFailure(Throwable caught) {
                populateFailed(caught);
            }

        }, buildings, getStatusDate(), getMetadata().category().getValue(), sortingCriteria, pageNumber, getMetadata().arrearsStatusListerSettings().pageSize()
                .getValue());

    }

    private LogicalDate getStatusDate() {
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
    }

    private void redrawTitleBanner() {
        String arrearsCategory = getMetadata().filterByCategory().isBooleanTrue() ? getMetadata().category().getValue().toString() : i18n.tr("Total");
        String unescaptedBanner = i18n.tr("{0} arrears as of {1,date,short}", arrearsCategory, getStatusDate());
        titleBannerLabel.setHTML(new SafeHtmlBuilder().appendEscaped(unescaptedBanner).toSafeHtml());
    }
}