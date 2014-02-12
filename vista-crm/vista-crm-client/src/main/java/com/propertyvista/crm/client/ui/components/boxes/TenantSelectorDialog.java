/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-11
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.lease.Tenant;

public abstract class TenantSelectorDialog extends EntitySelectorTableVisorController<Tenant> {

    private final static I18n i18n = I18n.get(TenantSelectorDialog.class);

    public TenantSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public TenantSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<Tenant> emptyList());
    }

    public TenantSelectorDialog(IPane parentView, List<Tenant> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected);
    }

    public TenantSelectorDialog(IPane parentView, boolean isMultiselect, List<Tenant> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select Tenant"));
    }

    public TenantSelectorDialog(IPane parentView, boolean isMultiselect, List<Tenant> alreadySelected, String caption) {
        super(parentView, Tenant.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
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
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().customer().person().name(), false));
    }

    @Override
    protected AbstractListService<Tenant> getSelectService() {
        return GWT.<SelectTenantListService> create(SelectTenantListService.class);
    }
}