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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.rpc.services.AbstractCrudService;
import com.pyx4j.widgets.client.TabLayoutPanel;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;

public class UnitVacancyReportGadget extends ListerGadgetBase<UnitVacancyReport> {

    @SuppressWarnings("unchecked")
    public UnitVacancyReportGadget(GadgetMetadata gmd) {
        super(gmd, (AbstractCrudService<UnitVacancyReport>) GWT.create(UnitVacancyReportService.class), UnitVacancyReport.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        super.selfInit(gmd);

        gmd.type().setValue(GadgetType.UnitVacancyReport);
        gmd.name().setValue(i18n.tr("Unit Vacancy Report"));
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<UnitVacancyReport>> columnDescriptors, UnitVacancyReport proto) {
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
    public ISetup getSetup() {
        return new SetupUnitVacancyReport(super.getSetup());
    }

    class SetupUnitVacancyReport implements ISetup {
        private static final String PARENT_SETUP_TAB_NAME = "General";

        private static final String SETUP_TAB_NAME = "Report Settings";

        private final ISetup parentSetup;

        private final TabLayoutPanel tabPanel;

        private final VerticalPanel setupPanel;

        public SetupUnitVacancyReport(ISetup parentSetup) {
            this.parentSetup = parentSetup;

            // TODO add some controls
            setupPanel = new VerticalPanel();
            setupPanel.add(new Label("FOO"));
            // TODO create style?
            tabPanel = new TabLayoutPanel(1.5, Unit.EM);
            tabPanel.add(setupPanel.asWidget(), new Label(SETUP_TAB_NAME));
            tabPanel.add(parentSetup.asWidget(), new Label(PARENT_SETUP_TAB_NAME));
        }

        @Override
        public Widget asWidget() {
            return tabPanel;
        }

        @Override
        public boolean onStart() {
            return parentSetup.onStart();
        }

        @Override
        public boolean onOk() {
            return parentSetup.onOk();
        }

        @Override
        public void onCancel() {
            parentSetup.onCancel();
        }

    }
}
