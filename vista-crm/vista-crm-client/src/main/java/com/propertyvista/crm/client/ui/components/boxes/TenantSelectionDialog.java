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
import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.lease.Tenant;

public abstract class TenantSelectionDialog extends EntitySelectorTableDialog<Tenant> {

    private final static I18n i18n = I18n.get(TenantSelectionDialog.class);

    public TenantSelectionDialog() {
        this(Collections.<Tenant> emptySet());
    }

    public TenantSelectionDialog(Set<Tenant> alreadySelected) {
        this(false, Collections.<Tenant> emptySet());
    }

    public TenantSelectionDialog(boolean isMultiselect, Set<Tenant> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Tenant"));
    }

    public TenantSelectionDialog(boolean isMultiselect, Set<Tenant> alreadySelected, String caption) {
        super(Tenant.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto().participantId()).build(),
                
                new ColumnDescriptor.Builder(proto().customer().person().name()).searchable(false).build(),
                new ColumnDescriptor.Builder(proto().customer().person().name().firstName()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().customer().person().name().lastName()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().customer().person().sex()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().customer().person().birthDate(), false).build(),
                new ColumnDescriptor.Builder(proto().customer().person().email(), false).build(),
                new ColumnDescriptor.Builder(proto().customer().person().homePhone()).build(),
                new ColumnDescriptor.Builder(proto().customer().person().mobilePhone()).build(),
                new ColumnDescriptor.Builder(proto().customer().person().workPhone()).build(),
                
                new ColumnDescriptor.Builder(proto().lease()).searchable(false).build(),
                new ColumnDescriptor.Builder(proto().lease().leaseId()).searchableOnly().build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().customer().person().name(), false));
    }

    @Override
    protected AbstractListCrudService<Tenant> getSelectService() {
        return GWT.<SelectTenantListService> create(SelectTenantListService.class);
    }
}