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

import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.gadgets.GadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportSummaryDTO;

public class AvailabiltySummaryGadget extends GadgetBase implements IBuildingGadget {

    public final CrmEntityForm<UnitVacancyReportSummaryDTO> form;

    public final Panel panel;

    private AvailabilityReportService service;

    private FilterData filter;

    public AvailabiltySummaryGadget(GadgetMetadata gmd) {
        super(gmd);
        form = new CrmEntityForm<UnitVacancyReportSummaryDTO>(UnitVacancyReportSummaryDTO.class, new CrmViewersComponentFactory()) {

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

                main.setWidget(++row, 0, new VistaLineSeparator());
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
        service = GWT.create(AvailabilityReportService.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.AvailabilitySummaryMk2);
        gmd.name().setValue(GadgetType.AvailabilitySummaryMk2.toString());
    }

    @Override
    public void start() {
        super.start();
        populateSummary();
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public boolean isSetupable() {
        return false;
    }

    @Override
    public void setFiltering(FilterData filterData) {
        this.filter = filterData;
        populateSummary();
    }

    private void populate(UnitVacancyReportSummaryDTO summary) {
        form.populate(summary);
    }

    private boolean isEnabled() {
        return form.isVisible();
    }

    private void reportError(Throwable error) {
        // TODO implement this in GadgetBase
    }

    // PRESENTER
    private void populateSummary() {
        if (isEnabled() & isRunning()) {
            if (filter == null) {
                populate(EntityFactory.create(UnitVacancyReportSummaryDTO.class));
            } else {
                service.summary(new AsyncCallback<UnitVacancyReportSummaryDTO>() {
                    @Override
                    public void onSuccess(UnitVacancyReportSummaryDTO result) {
                        populate(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        reportError(caught);
                    }
                }, new Vector<Key>(filter.buildings), filter.toDate == null ? null : new LogicalDate(filter.toDate));
            }
        }
    }
}