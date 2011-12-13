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
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.Arrears;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatus;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ArrearsStatusGadget extends AbstractGadget<ArrearsStatus> {
    private static final I18n i18n = I18n.get(ArrearsStatusGadget.class);

    private static class ArrearsStatusGadgetInstance extends ListerGadgetInstanceBase<MockupArrearsState, ArrearsStatus> implements IBuildingGadget {
        private static final I18n i18n = I18n.get(ArrearsStatusGadgetInstance.class);

        private final ArrearsReportService service;

        private FilterData filterData;

        private FormFlexPanel panel;

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
        @SuppressWarnings("unchecked")
        @Override
        public List<ColumnDescriptor<MockupArrearsState>> defineColumnDescriptors() {
            return Arrays.asList(
                    colh(proto().propertyCode()),
                    colh(proto().buildingName()),
                    colh(proto().complexName()),
                    colv(proto().unitNumber()),
                    colh(proto().firstName()),
                    colv(proto().lastName()),
                    
                    // arrears subclass specific stuff goes here here
                    colv(arrearsProto().thisMonth()),
                    colv(arrearsProto().monthAgo()),
                    colv(arrearsProto().twoMonthsAgo()),
                    colv(arrearsProto().threeMonthsAgo()),
                    colv(arrearsProto().overFourMonthsAgo()),
                    colv(arrearsProto().totalBalance()),
                    colv(arrearsProto().prepayments()),
                                    
                    colv(proto().legalStatus()),
                    colv(proto().lmrUnitRentDifference()),
                    
                    // address
                    colh(proto().streetNumber()),
                    colh(proto().streetName()),
                    colh(proto().streetType()),
                    colh(proto().city()),
                    colh(proto().province(), i18n.tr("Province")),
                    colh(proto().country().name(), i18n.tr("Country")),
                    
                    // common tabular gadget stuff
                    colh(proto().common().propertyManger().name(), i18n.tr("Property Manager")),
                    colh(proto().common().owner().company().name(), i18n.tr("Owner")),
                    colh(proto().common().region()),
                    colh(proto().common().portfolio().name(), i18n.tr("Portfolio"))
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
        public void setFiltering(FilterData filterData) {
            this.filterData = filterData;
            populatePage(0);
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
            if (filterData != null) {
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
                }, getCustomCriteria(), new Vector<Key>(filterData.buildings), filterData.toDate == null ? null : new LogicalDate(filterData.toDate),
                        new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            } else {
                setPageData(new ArrayList<MockupArrearsState>(1), 0, 0, false);
            }
        }

        private Vector<Criterion> getCustomCriteria() {
            Vector<Criterion> customCriteria = new Vector<Criterion>();
            for (DataTableFilterData filterData : getListerFilterData()) {
                if (filterData.isFilterOK()) {
                    switch (filterData.getOperand()) {
                    case is:
                        customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.EQUAL, filterData.getValue()));
                        break;
                    case isNot:
                        customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.NOT_EQUAL, filterData.getValue()));
                        break;
                    case like:
                        customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.RDB_LIKE, filterData.getValue()));
                        break;
                    case greaterThan:
                        customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.GREATER_THAN, filterData.getValue()));
                        break;
                    case lessThan:
                        customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.LESS_THAN, filterData.getValue()));
                        break;
                    }
                }
            }
            return customCriteria;
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