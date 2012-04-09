/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.Arrears;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatus;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ArrearsStatusGadget extends AbstractGadget<ArrearsStatus> {

    private static final I18n i18n = I18n.get(ArrearsStatusGadget.class);

    private static class ArrearsStatusGadgetInstance extends ListerGadgetInstanceBase<MockupArrearsState, ArrearsStatus> implements

    IBuildingBoardGadgetInstance {

        private static final I18n i18n = I18n.get(ArrearsStatusGadgetInstance.class);

        private final ArrearsReportService service;

        private FormFlexPanel panel;

        private List<Key> buildings;

        public ArrearsStatusGadgetInstance(GadgetMetadata gmd) {
            super(gmd, MockupArrearsState.class, ArrearsStatus.class);
            service = GWT.create(ArrearsReportService.class);
        }

        @Override
        protected ArrearsStatus createDefaultSettings(Class<ArrearsStatus> metadataClass) {
            ArrearsStatus metadata = super.createDefaultSettings(metadataClass);
            metadata.category().setValue(ArrearsStatus.Category.Total);
            return metadata;
        }

        @Override
        protected boolean isFilterRequired() {
            return true;
        }

        private Arrears arrearsProto() {
            if (getMetadata().category().isNull()) {
                return proto().totalArrears();
            }
            switch (getMetadata().category().getValue()) {
            case Total:
                return proto().totalArrears();
            case Rent:
                return proto().rentArrears();
            case Parking:
                return proto().parkingArrears();
            case Other:
                return proto().otherArrears();
            default:
                return proto().totalArrears();
            }
        }

        //@formatter:off    
        @Override
        public List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(
                    new MemberColumnDescriptor.Builder(proto().propertyCode()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().buildingName()).build(),
                    new MemberColumnDescriptor.Builder(proto().complexName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().unitNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().firstName()).build(),
                    new MemberColumnDescriptor.Builder(proto().lastName()).build(),
                    
                    // arrears subclass specific stuff goes here here
                    new MemberColumnDescriptor.Builder(arrearsProto().thisMonth()).build(),
                    new MemberColumnDescriptor.Builder(arrearsProto().monthAgo()).build(),
                    new MemberColumnDescriptor.Builder(arrearsProto().twoMonthsAgo()).build(),
                    new MemberColumnDescriptor.Builder(arrearsProto().threeMonthsAgo()).build(),
                    new MemberColumnDescriptor.Builder(arrearsProto().overFourMonthsAgo()).build(),
                    new MemberColumnDescriptor.Builder(arrearsProto().totalBalance()).build(),
                    new MemberColumnDescriptor.Builder(arrearsProto().prepayments()).build(),
                                    
                    new MemberColumnDescriptor.Builder(proto().legalStatus()).build(),
                    new MemberColumnDescriptor.Builder(proto().lmrUnitRentDifference()).build(),
                    
                    // address
                    new MemberColumnDescriptor.Builder(proto().streetNumber()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().streetName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().streetType()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().city()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().province()).title(i18n.tr("Province")).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().country().name()).title(i18n.tr("Country")).visible(false).build(),
                    
                    // common tabular gadget stuff
                    new MemberColumnDescriptor.Builder(proto().common().propertyManger().name()).title(i18n.tr("Property Manager")).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().common().owner().company().name()).title(i18n.tr("Owner")).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().common().region()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().common().portfolio().name()).title(i18n.tr("Portfolio")).visible(false).build()
            );
        }                    
        //@formatter:on

        @Override
        public Widget initContentPanel() {
            panel = new FormFlexPanel();
            panel.setH1(0, 0, 1, (getMetadata().category().getValue() != null ? getMetadata().category().getValue() : ArrearsStatus.Category.Total).toString());
            panel.setWidget(1, 0, initListerWidget());
            return panel;
        }

        @Override
        public ISetup getSetup() {
            return new SetupForm(new CEntityDecoratableEditor<ArrearsStatus>(ArrearsStatus.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel p = new FormFlexPanel();
                    int row = -1;
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pageSize())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().category())).build());
                    return p;
                }
            });

        }

        @Override
        public void populatePage(final int pageNumber) {
            if (buildings != null) {
                service.arrearsList(new AsyncCallback<EntitySearchResult<MockupArrearsState>>() {
                    @Override
                    public void onSuccess(EntitySearchResult<MockupArrearsState> result) {
                        setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                }, new Vector<Criterion>(getListerFilterData()), new Vector<Key>(buildings), getStatusDate(), new Vector<Sort>(getSorting()), getPageNumber(),
                        getPageSize());
            } else {
                setPageData(new ArrayList<MockupArrearsState>(1), 0, 0, false);
            }
        }

        private LogicalDate getStatusDate() {
            return new LogicalDate();
        }
    }

    public ArrearsStatusGadget() {
        super(ArrearsStatus.class);
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    public String getDescription() {
        return i18n
                .tr("Shows the information about tenant arrears, including how long it is overdue, total balance, legal status information etc. This gadget can either show total arrears or arrears of specific type (i.e. rent, parking or other)");
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Arrears.toString());
    }

    @Override
    protected GadgetInstanceBase<ArrearsStatus> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsStatusGadgetInstance(gadgetMetadata);
    }

}