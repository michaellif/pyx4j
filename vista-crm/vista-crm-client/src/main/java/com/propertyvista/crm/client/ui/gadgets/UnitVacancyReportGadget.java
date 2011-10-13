/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 5, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;

public class UnitVacancyReportGadget extends ListerGadgetBase<UnitVacancyReport> implements IBuildingGadget {
    private final VerticalPanel gadgetPanel;

    private final UnitVacancyReportSummaryForm unitVacancyReportSummaryForm;

    private final UnitVacancyReportGadgetActivity activity;

    private List<Key> buildings;

    public UnitVacancyReportGadget(GadgetMetadata gmd) {
        super(gmd, (UnitVacancyReportService) GWT.create(UnitVacancyReportService.class), UnitVacancyReport.class);

        unitVacancyReportSummaryForm = new UnitVacancyReportSummaryForm();
        unitVacancyReportSummaryForm.initContent();

        gadgetPanel = new VerticalPanel();

        gadgetPanel.add(getListerBase());
        gadgetPanel.add(unitVacancyReportSummaryForm.asWidget());

        activity = new UnitVacancyReportGadgetActivity(this, (UnitVacancyReportService) service);

        getListerView().setPresenter(activity);
        getRefreshTimer().registerEventHandler(new RefreshTimerEventHandler() {

            @Override
            public void onTime() {
                activity.populateSummary();
            }
        });
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        super.selfInit(gmd);

        ListerGadgetBaseSettings settings = gmd.settings().cast();
        settings.itemsPerPage().setValue(5);

        gmd.type().setValue(GadgetType.UnitVacancyReport);
        gmd.name().setValue(i18n.tr("Unit Vacancy Report"));

    }

    public IListerView<UnitVacancyReport> getListerView() {
        return getListerBase();
    }

    public UnitVacancyReportSummaryForm getUnitVacancyReportSummaryForm() {
        return unitVacancyReportSummaryForm;
    }

    @Override
    public Widget asWidget() {

        return gadgetPanel;
    }

    @Override
    public void start() {
        super.start();
        activity.populateSummary();
    }

    public class UnitVacancyReportSummaryForm extends CrmEntityForm<UnitVacancyReportSummaryDTO> {

        public UnitVacancyReportSummaryForm() {
            super(UnitVacancyReportSummaryDTO.class, new CrmViewersComponentFactory());
        }

        @Override
        public IsWidget createContent() {
            final int WIDTH = 10;
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true);

            main.add(inject(proto().total()), WIDTH);
            main.add(inject(proto().netExposure()), WIDTH);
            main.add(new VistaLineSeparator());

            VistaDecoratorsSplitFlowPanel sp = new VistaDecoratorsSplitFlowPanel(true);

            sp.getLeftPanel().add(inject(proto().vacancyAbsolute()), WIDTH);
            sp.getLeftPanel().add(inject(proto().vacancyRelative()), WIDTH);
            sp.getLeftPanel().add(inject(proto().vacantRented()), WIDTH);
            sp.getLeftPanel().add(new VistaLineSeparator());
            sp.getLeftPanel().add(inject(proto().occupancyAbsolute()), WIDTH);
            sp.getLeftPanel().add(inject(proto().occupancyRelative()), WIDTH);

            sp.getRightPanel().add(inject(proto().noticeAbsolute()), WIDTH);
            sp.getRightPanel().add(inject(proto().noticeRelative()), WIDTH);
            sp.getRightPanel().add(inject(proto().noticeRented()), WIDTH);

            main.add(sp);

            return new CrmScrollPanel(main);
        }
    }

    public class UnitVacancyReportGadgetActivity extends ListerActivityBase<UnitVacancyReport> {

        private final UnitVacancyReportService serivce;

        private final UnitVacancyReportGadget gadget;

        // TODO create interface for the gadget and pass interface to the constructor?
        public UnitVacancyReportGadgetActivity(UnitVacancyReportGadget gadget, UnitVacancyReportService service) {
            super(gadget.getListerView(), service, UnitVacancyReport.class);
            this.serivce = service;
            this.gadget = gadget;
        }

        @Override
        protected EntityListCriteria<UnitVacancyReport> constructSearchCriteria() {
            EntityListCriteria<UnitVacancyReport> criteria = super.constructSearchCriteria();

            if (buildings != null && !buildings.isEmpty()) {
                // FIXME this is the the part that should construct search criteria based on user selection in dashboard, but it's currently disabled because this gadget has only demo functionality 
                //Building building = EntityFactory.create(Building.class);
                //criteria.add(new PropertyCriterion(building.id().getPath().toString(), Restriction.IN, (Serializable) buildings));

                // TODO these are fake buildings for use with fake unit property report table, for demonstration purposes only
                final List<String> fakeBuildings = new ArrayList<String>();
                if (buildings.size() == 1) {
                    fakeBuildings.add("bath1650");
                } else {
                    fakeBuildings.add("jean0200");
                    fakeBuildings.add("com0164");
                    fakeBuildings.add("chel3126");
                }

                criteria.add(new PropertyCriterion(getListerBase().proto().propertyCode().getPath().toString(), Restriction.IN, (Serializable) fakeBuildings));
            }

            return criteria;

        }

        public void populateSummary() {
            serivce.summary(new AsyncCallback<UnitVacancyReportSummaryDTO>() {
                @Override
                public void onSuccess(UnitVacancyReportSummaryDTO result) {
                    gadget.getUnitVacancyReportSummaryForm().populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            }, constructSearchCriteria());
        }
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<UnitVacancyReport>> columnDescriptors, UnitVacancyReport proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost()));
    }

    @Override
    protected void fillAvailableColumnDescripors(List<ColumnDescriptor<UnitVacancyReport>> columnDescriptors, UnitVacancyReport proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanMarketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.isScoped()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentReady()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaAbsolute()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaRelative()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveOutDay()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveInDay()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedFromDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost()));
    }

    @Override
    public void setBuilding(Key id) {
        List<Key> ids = new ArrayList<Key>(1);
        ids.add(id);
        setBuildings(ids);
    }

    @Override
    public void setBuildings(List<Key> ids) {
        List<Key> my = new ArrayList<Key>(1);
        for (Key id : ids) {
            my.add(id);
        }
        buildings = my;

        // TODO maybe the following should be removed, depends on the dashboard logic
        stop();
        start();
    }

}
