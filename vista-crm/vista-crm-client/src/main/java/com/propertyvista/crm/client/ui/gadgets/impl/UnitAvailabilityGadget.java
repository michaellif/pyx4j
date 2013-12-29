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

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.UnitAvailabilityGadgetMetatadaForm;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils.ItemSelectCommand;
import com.propertyvista.crm.client.ui.gadgets.util.Provider;
import com.propertyvista.crm.rpc.dto.gadgets.UnitAvailabilityStatusDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilityStatusListService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.common.AsOfDateCriterion;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class UnitAvailabilityGadget extends GadgetInstanceBase<UnitAvailabilityGadgetMetadata> implements IBuildingBoardGadgetInstance {

    private static final I18n i18n = I18n.get(UnitAvailabilityGadget.class);

    private static final List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        UnitAvailabilityStatusDTO proto = EntityFactory.create(UnitAvailabilityStatusDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.<ColumnDescriptor> asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.propertyCode()).build(),
                new MemberColumnDescriptor.Builder(proto.externalId()).visible(false).build(),                
                new MemberColumnDescriptor.Builder(proto.buildingName()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.address()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.propertyManager()).build(),                    
                new MemberColumnDescriptor.Builder(proto.complex()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.unit()).build(),
                new MemberColumnDescriptor.Builder(proto.floorplanName()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.floorplanMarketingName()).visible(false).build(),
                
                // status
                new MemberColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                new MemberColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.unitRent()).build(),
                new MemberColumnDescriptor.Builder(proto.marketRent()).build(),
                new MemberColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.vacantSince()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.daysVacant()).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto.revenueLost()).sortable(false).build()
        );//@formatter:on
    }

    private VerticalPanel gadgetPanel;

    private HTML filterDisplayPanel;

    private Label asOf;

    private EntityDataTablePanel<UnitAvailabilityStatusDTO> lister;

    public UnitAvailabilityGadget(UnitAvailabilityGadgetMetadata gmd) {
        super(gmd, UnitAvailabilityGadgetMetadata.class, new UnitAvailabilityGadgetMetatadaForm());
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                lister.getDataTablePanel().setPageSize(getMetadata().unitStatusListerSettings().pageSize().getValue());

                lister.getDataSource().clearPreDefinedFilters();

                lister.getDataSource().addPreDefinedFilter(new AsOfDateCriterion(getStatusDate()));

                if (!containerBoard.getSelectedBuildingsStubs().isEmpty()) {
                    lister.getDataSource().addPreDefinedFilter(
                            PropertyCriterion.in(lister.proto().buildingsFilterAnchor(), containerBoard.getSelectedBuildingsStubs()));
                }

                switch (getMetadata().filterPreset().getValue()) {
                case Vacant:
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(lister.proto().vacancyStatus(), Vacancy.Vacant));
                    break;
                case Notice:
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(lister.proto().vacancyStatus(), Vacancy.Notice));
                    break;
                case VacantAndNotice:
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.in(lister.proto().vacancyStatus(), Vacancy.Vacant, Vacancy.Notice));
                    break;
                case NetExposure:
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.in(lister.proto().vacancyStatus(), Vacancy.Vacant, Vacancy.Notice));
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(lister.proto().rentedStatus(), RentedStatus.Unrented));
                    break;
                case Rented:
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.in(lister.proto().vacancyStatus(), Vacancy.Vacant, Vacancy.Notice));
                    lister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(lister.proto().rentedStatus(), RentedStatus.Rented));
                    break;
                }

                lister.obtain(0);

                redrawFilterDisplayPanel();
                redrawAsOfBannerPanel();
                populateSucceded();
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

        gadgetPanel = new VerticalPanel();
        gadgetPanel.add(initAsOfBannerPanel());
        gadgetPanel.add(initFilterDisplayPanel());

        lister = new EntityDataTablePanel<UnitAvailabilityStatusDTO>(UnitAvailabilityStatusDTO.class);
        lister.setDataSource(new ListerDataSource<UnitAvailabilityStatusDTO>(UnitAvailabilityStatusDTO.class, GWT
                .<UnitAvailabilityStatusListService> create(UnitAvailabilityStatusListService.class)));
        ListerUtils.bind(lister.getDataTablePanel())//@formatter:off
            .columnDescriptors(DEFAULT_COLUMN_DESCRIPTORS)
            .setupable(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey()))
            .userSettingsProvider(new Provider<ListerUserSettings>() {
                @Override
                public ListerUserSettings get() {
                    return getMetadata().unitStatusListerSettings();
                }
             })
            .onColumnSelectionChanged(new Command() {
                @Override
                public void execute() {
                    saveMetadata();
                }
            })
            .onItemSelectedCommand(new ItemSelectCommand<UnitAvailabilityStatusDTO>() {                
                @Override
                public void execute(UnitAvailabilityStatusDTO item) {
                    AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(AptUnit.class).formViewerPlace(item.unitId().getValue()));
                }
            })
            .init();
        ///@formatter:on

        gadgetPanel.add(lister);
        gadgetPanel.setWidth("100%");

        return gadgetPanel;
    }

    private LogicalDate getStatusDate() {
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
    }

    private Widget initAsOfBannerPanel() {
        HorizontalPanel asForBannerPanel = new HorizontalPanel();
        asForBannerPanel.setWidth("100%");
        asForBannerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

        asOf = new Label();

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

    private void redrawAsOfBannerPanel() {
        asOf.setText(i18n.tr("As of Date: {0,date,short}", getStatusDate()));
    }
}