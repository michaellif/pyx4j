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

import com.propertyvista.crm.rpc.services.selections.SelectLeaseTermListService;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public abstract class LeaseTermSelectionDialog extends EntitySelectorTableDialog<LeaseTerm> {

    private final static I18n i18n = I18n.get(LeaseTermSelectionDialog.class);

    public LeaseTermSelectionDialog() {
        this(Collections.<LeaseTerm> emptySet());
    }

    public LeaseTermSelectionDialog(Set<LeaseTerm> alreadySelected) {
        this(false, alreadySelected);
    }

    public LeaseTermSelectionDialog(boolean isMultiselect, Set<LeaseTerm> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Term"));
    }

    public LeaseTermSelectionDialog(boolean isMultiselect, Set<LeaseTerm> alreadySelected, String caption) {
        super(LeaseTerm.class, true, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto().termFrom()).build(),
                new ColumnDescriptor.Builder(proto().termTo()).build(),
                
                new ColumnDescriptor.Builder(proto().type()).build(),
                new ColumnDescriptor.Builder(proto().status()).build(),
                new ColumnDescriptor.Builder(proto().creationDate()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().termFrom(), false));
    }

    @Override
    protected AbstractListCrudService<LeaseTerm> getSelectService() {
        return GWT.<SelectLeaseTermListService> create(SelectLeaseTermListService.class);
    }
}