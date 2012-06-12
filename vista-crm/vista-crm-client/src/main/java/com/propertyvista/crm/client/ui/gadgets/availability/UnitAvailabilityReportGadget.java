/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 10, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.availability;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMeta.FilterPreset;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilityReportGadget extends AbstractGadget<UnitAvailabilityGadgetMeta> {

    private static final I18n i18n = I18n.get(UnitAvailabilityReportGadget.class);

    public static class UnitAvailabilityReportGadgetInstance extends ListerGadgetInstanceBase<UnitAvailabilityStatus, UnitAvailabilityGadgetMeta> implements
            IBuildingBoardGadgetInstance {

        private VerticalPanel gadgetPanel;

        private HTML filterDisplayPanel;

        private final AvailabilityReportService service;

        private CDatePicker asOf;

        public UnitAvailabilityReportGadgetInstance(GadgetMetadata gmd) {
            super(gmd, UnitAvailabilityGadgetMeta.class, new UnitAvailabilityGadgetMetatadaForm(), UnitAvailabilityStatus.class, false);
            service = GWT.create(AvailabilityReportService.class);
        }

        @Override
        public void setContainerBoard(final BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        protected UnitAvailabilityGadgetMeta createDefaultSettings(Class<UnitAvailabilityGadgetMeta> metadataClass) {
            UnitAvailabilityGadgetMeta settings = super.createDefaultSettings(metadataClass);
            settings.filterPreset().setValue(FilterPreset.VacantAndNotice);
            UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);
            settings.columnDescriptors().addAll(ColumnDescriptorConverter.asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    // references
                    new MemberColumnDescriptor.Builder(proto.building().propertyCode()).build(),
                    new MemberColumnDescriptor.Builder(proto.building().externalId()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto.building().info().name()).visible(false).title(i18n.tr("Building Name")).build(),
                    new MemberColumnDescriptor.Builder(proto.building().info().address()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto.building().propertyManager().name()).visible(false).title(i18n.tr("Property Manager")).build(),                    
                    new MemberColumnDescriptor.Builder(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                    new MemberColumnDescriptor.Builder(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                    new MemberColumnDescriptor.Builder(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                    new MemberColumnDescriptor.Builder(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                    
                    // status
                    new MemberColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                    new MemberColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
//                    new MemberColumnDescriptor.Builder(proto.unitRent()).build(),
//                    new MemberColumnDescriptor.Builder(proto.marketRent()).build(),
//                    new MemberColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
//                    new MemberColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto.daysVacant()).build()
//                    new MemberColumnDescriptor.Builder(proto.revenueLost()).build()
            )));//@formatter:on
            return settings;
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
        protected void populatePage(int pageNumber) {
            if (containerBoard.getSelectedBuildingsStubs() == null) {
                setAsOf(getStatusDate());
                setPageData(new Vector<UnitAvailabilityStatus>(), 0, 0, false);
                populateSucceded();
                return;
            }

            final int page = pageNumber;
            final Vector<Key> buildingPks = new Vector<Key>(containerBoard.getSelectedBuildingsStubs().size());
            for (Building b : containerBoard.getSelectedBuildingsStubs()) {
                buildingPks.add(b.getPrimaryKey());
            }

            service.unitStatusList(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>>() {
                @Override
                public void onSuccess(EntitySearchResult<UnitAvailabilityStatus> result) {
                    setPageData(result.getData(), page, result.getTotalRows(), result.hasMoreData());
                    redrawFilterDisplayPanel();
                    populateSucceded();
                }

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }
            }, buildingPks, getMetadata().filterPreset().getValue(), getStatusDate(), new Vector<Sort>(getListerSortingCriteria()), pageNumber, getPageSize());
        }

        private void setAsOf(LogicalDate statusDate) {
            asOf.setValue(statusDate);
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

    public UnitAvailabilityReportGadget() {
        super(UnitAvailabilityGadgetMeta.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Availability.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<UnitAvailabilityGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new UnitAvailabilityReportGadgetInstance(gadgetMetadata);
    }
}