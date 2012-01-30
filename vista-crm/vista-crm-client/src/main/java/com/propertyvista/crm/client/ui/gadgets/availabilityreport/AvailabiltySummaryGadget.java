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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class AvailabiltySummaryGadget extends AbstractGadget<AvailabilitySummary> {
    private static final I18n i18n = I18n.get(AvailabiltySummaryGadget.class);

    public static class AvailabiltySummaryGadgetInstance extends GadgetInstanceBase<AvailabilitySummary> implements IBuildingGadget {
        //private static final I18n i18n = I18n.get(AvailabiltySummaryGadgetInstance.class);

        public CrmEntityForm<UnitVacancyReportSummaryDTO> form;

        public Panel panel;

        private final AvailabilityReportService service;

        private FilterData filter;

        public AvailabiltySummaryGadgetInstance(GadgetMetadata gmd) {
            super(gmd, AvailabilitySummary.class);
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
        public Widget initContentPanel() {
            form = new CrmEntityForm<UnitVacancyReportSummaryDTO>(UnitVacancyReportSummaryDTO.class, true) {

                @Override
                public IsWidget createContent() {
                    final double COMPONENT_WIDTH = 5d;
                    final double LABEL_WIDTH = 10d;
                    int row = 0;
                    int col = -1;
                    FormFlexPanel main = new FormFlexPanel();

                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().occupancyAbsolute()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().vacancyAbsolute()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().noticeAbsolute()), COMPONENT_WIDTH, LABEL_WIDTH).build());

                    ++row;
                    col = -1;
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().occupancyRelative()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().vacancyRelative()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().noticeRelative()), COMPONENT_WIDTH, LABEL_WIDTH).build());

                    ++row;
                    col = 0;
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().vacantRented()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(row, ++col, new DecoratorBuilder(inject(proto().noticeRented()), COMPONENT_WIDTH, LABEL_WIDTH).build());

                    main.setHR(++row, 0, 2);
                    main.getFlexCellFormatter().setColSpan(row, 0, 3);

                    main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().total()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().netExposureAbsolute()), COMPONENT_WIDTH, LABEL_WIDTH).build());
                    main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().netExposureRelative()), COMPONENT_WIDTH, LABEL_WIDTH).build());

                    main.getColumnFormatter().setWidth(0, "33%");
                    main.getColumnFormatter().setWidth(1, "34%");
                    main.getColumnFormatter().setWidth(2, "33%");

                    main.setWidth("100%");

                    return main;
                }
            };
            form.initContent();
            form.setWidth("100%");
            panel = new VerticalPanel();
            panel.add(form);

            return panel;
        }

        @Override
        public boolean isSetupable() {
            return false;
        }

        @Override
        public void setFiltering(FilterData filterData) {
            this.filter = filterData;
            populate();
        }

        private void setData(UnitVacancyReportSummaryDTO summary) {
            form.populate(summary);
        }

        private void doPopulate() {
            if (filter == null) {
                setData(EntityFactory.create(UnitVacancyReportSummaryDTO.class));
                populateSucceded();
            } else {
                service.summary(new AsyncCallback<UnitVacancyReportSummaryDTO>() {
                    @Override
                    public void onSuccess(UnitVacancyReportSummaryDTO result) {
                        setData(result);
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                }, new Vector<Key>(filter.buildings), filter.toDate == null ? null : new LogicalDate(filter.toDate));
            }
        }

    }

    public AvailabiltySummaryGadget() {
        super(AvailabilitySummary.class);
    }

    @Override
    public String getDescription() {
        return i18n
                .tr("Shows a summary of information about all units, including the total number of units, vacancy, notice and net exposure information in both percentages and quantity");
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
    protected GadgetInstanceBase<AvailabilitySummary> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new AvailabiltySummaryGadgetInstance(gadgetMetadata);
    }
}