/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
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
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.LeaseApplicationDTO;

public class ApplicationsDetailsFactory extends AbstractListerDetailsFactory<LeaseApplicationDTO, CounterGadgetFilter> {

    private static final I18n i18n = I18n.get(ApplicationsDetailsFactory.class);

    private static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;

    static {
        LeaseApplicationDTO proto = EntityFactory.getEntityPrototype(LeaseApplicationDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new Builder(proto.leaseId()).build(),
                    new Builder(proto.type()).build(),
                    
                    new Builder(proto.unit().building().propertyCode()).build(),
                    new Builder(proto.unit()).build(),
                    
                    new Builder(proto.leaseApplication().status(), true).build(),
                    
                    new Builder(proto.currentTerm().termFrom()).build(),
                    new Builder(proto.currentTerm().termTo()).build(),
                    
                    new Builder(proto.expectedMoveIn()).build(),
                    new Builder(proto.expectedMoveOut(), false).build(),
                    new Builder(proto.actualMoveIn(), false).build(),
                    new Builder(proto.actualMoveOut(), false).build(),
                    new Builder(proto.moveOutSubmissionDate(), false).build(),
                    
                    new Builder(proto.creationDate(), false).build(),
                    
                    new Builder(proto._applicant().customer().person().name()).columnTitle(i18n.tr("Primary Tenant Name")).searchable(false).build(),
                    new Builder(proto._applicant().customer().person().name().firstName(), false).columnTitle(i18n.tr("Primary Tenant First Name")).build(),
                    new Builder(proto._applicant().customer().person().name().lastName(), false).columnTitle(i18n.tr("Primary Tenant Last Name")).build(),
                    new Builder(proto.leaseParticipants().$().customer().customerId(), false).build(),
                    
                    new Builder(proto.numberOfOccupants(), false).sortable(false).searchable(false).title(i18n.tr("Occupants")).build(),
                    new Builder(proto.numberOfApplicants(), true).sortable(false).searchable(false).title(i18n.tr("Applicants")).build(),
                    new Builder(proto.numberOfGuarantors(), true).sortable(false).searchable(false).title(i18n.tr("Guarantors")).build(),
                    
                    new Builder(proto.currentTerm().version().tenants()).build()
                );//@formatter:on

    }

    public ApplicationsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<LeaseApplicationDTO, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                LeaseApplicationDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<LeaseApplicationViewerCrudService>create(LeaseApplicationViewerCrudService.class),
                filterDataProvider,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on
    }
}
