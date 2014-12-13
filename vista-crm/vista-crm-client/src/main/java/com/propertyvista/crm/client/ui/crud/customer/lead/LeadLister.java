/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.lead;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadLister extends SiteDataTablePanel<Lead> {

    private final static I18n i18n = I18n.get(LeadLister.class);

    public LeadLister() {
        super(Lead.class, GWT.<AbstractCrudService<Lead>> create(LeadCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().leadId(), true).build(), //
                new ColumnDescriptor.Builder(proto().guests(), true).build(), //
                new ColumnDescriptor.Builder(proto().guests().$().person().name().lastName()).columnTitle(i18n.tr("Guest Last Name")).searchableOnly()
                        .build(), //
                new ColumnDescriptor.Builder(proto().moveInDate(), true).build(), //
                new ColumnDescriptor.Builder(proto().leaseTerm(), true).build(), //
                new ColumnDescriptor.Builder(proto().floorplan(), true).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().createDate(), true).build(), //
                new ColumnDescriptor.Builder(proto().status(), true).build());

        setDataTableModel(new DataTableModel<Lead>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leadId(), false));
    }
}
