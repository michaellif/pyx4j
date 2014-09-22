/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.activity.ListerController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;

import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantListerController extends ListerController<Tenant> {

    public TenantListerController(ILister<Tenant> view, AbstractListCrudService<Tenant> service) {
        super(Tenant.class, view, service);
        ((EntityLister<Tenant>) view).setDataTableModel(defineColumnDescriptors());
    }

    protected Tenant proto() {
        return EntityFactory.getEntityPrototype(Tenant.class);
    }

    protected ColumnDescriptor[] defineColumnDescriptors() {
        return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto().participantId()).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().name()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().sex()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().birthDate(), false).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().email(), false).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().homePhone()).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().mobilePhone()).build(),
                new MemberColumnDescriptor.Builder(proto().customer().person().workPhone()).build(),
                new MemberColumnDescriptor.Builder(proto().lease()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().lease().leaseId()).searchableOnly().build()
        };
    }

}
