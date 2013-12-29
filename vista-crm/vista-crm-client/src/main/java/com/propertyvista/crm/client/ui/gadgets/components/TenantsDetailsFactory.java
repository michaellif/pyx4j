/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.dto.TenantDTO;

public class TenantsDetailsFactory extends AbstractListerDetailsFactory<TenantDTO, CounterGadgetFilter> {

    private static final I18n i18n = I18n.get(TenantsDetailsFactory.class);

    private static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {

        TenantDTO proto = EntityFactory.getEntityPrototype(TenantDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new Builder(proto.participantId()).build(),
                    new Builder(proto.role()).sortable(false).searchable(false).build(),
                    
                    new Builder(proto.customer().person().name()).searchable(false).build(),
                    new Builder(proto.customer().person().name().firstName(), false).build(),
                    new Builder(proto.customer().person().name().lastName(), false).build(),
                    new Builder(proto.customer().person().sex(), false).build(),
                    new Builder(proto.customer().person().birthDate()).build(),
                    
                    new Builder(proto.customer().person().homePhone()).build(),
                    new Builder(proto.customer().person().mobilePhone(), false).build(),
                    new Builder(proto.customer().person().workPhone(), false).build(),
                    new Builder(proto.customer().person().email()).build(),
                    
                    new Builder(proto.leaseTermV().holder()).columnTitle(i18n.tr("Lease Term")).searchable(false).build(),
                    new Builder(proto.leaseTermV().holder().lease().leaseId()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build()
         ); // @formatter:on
    }

    public TenantsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider, ICriteriaProvider<TenantDTO, CounterGadgetFilter> criteriaProvider) {
        super(//@formatter:off
                TenantDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<TenantCrudService>create(TenantCrudService.class),
                filterDataProvider,
                criteriaProvider,
                null
        );//@formatter:on
    }
}
