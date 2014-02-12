/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.AptUnitDTO;

public class UnitDetailsFactory extends AbstractListerDetailsFactory<AptUnitDTO, CounterGadgetFilter> {

    private static final I18n i18n = I18n.get(UnitDetailsFactory.class);

    private static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;

    static {
        AptUnitDTO proto = EntityFactory.create(AptUnitDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.buildingCode()).build(),
                new MemberColumnDescriptor.Builder(proto.floorplan().name()).title(i18n.tr("Floorplan Name")).build(),
                new MemberColumnDescriptor.Builder(proto.floorplan().marketingName()).title(i18n.tr("Floorplan Marketing Name")).build(),
                new MemberColumnDescriptor.Builder(proto.info().economicStatus()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.info().floor()).build(),
                new MemberColumnDescriptor.Builder(proto.info().number()).build(),
                new MemberColumnDescriptor.Builder(proto.info().area()).build(),
                new MemberColumnDescriptor.Builder(proto.info().areaUnits()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.info()._bedrooms()).build(),
                new MemberColumnDescriptor.Builder(proto.info()._bathrooms()).build(),
                new MemberColumnDescriptor.Builder(proto.financial()._unitRent()).build(),
                new MemberColumnDescriptor.Builder(proto.financial()._marketRent()).build(),
                new MemberColumnDescriptor.Builder(proto.availability().availableForRent()).build()
            );//@formatter:on
    }

    public UnitDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider, ICriteriaProvider<AptUnitDTO, CounterGadgetFilter> criteriaProvider,
            Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                AptUnitDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<UnitCrudService> create(UnitCrudService.class),
                filterDataProvider,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on
    }

}
