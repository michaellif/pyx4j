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

import com.propertyvista.crm.rpc.services.selections.SelectLeaseTermListService;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public abstract class LeaseTermSelectorDialog extends EntitySelectorTableVisorController<LeaseTerm> {

    private final static I18n i18n = I18n.get(LeaseTermSelectorDialog.class);

    public LeaseTermSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public LeaseTermSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<LeaseTerm> emptyList());
    }

    public LeaseTermSelectorDialog(IPane parentView, List<LeaseTerm> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected);
    }

    public LeaseTermSelectorDialog(IPane parentView, boolean isMultiselect, List<LeaseTerm> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select Term"));
    }

    public LeaseTermSelectorDialog(IPane parentView, boolean isMultiselect, List<LeaseTerm> alreadySelected, String caption) {
        super(parentView, LeaseTerm.class, true, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().termFrom()).build(),
                new MemberColumnDescriptor.Builder(proto().termTo()).build(),
                
                new MemberColumnDescriptor.Builder(proto().type()).build(),
                new MemberColumnDescriptor.Builder(proto().status()).build(),
                new MemberColumnDescriptor.Builder(proto().creationDate()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().termFrom(), false));
    }

    @Override
    protected AbstractListService<LeaseTerm> getSelectService() {
        return GWT.<SelectLeaseTermListService> create(SelectLeaseTermListService.class);
    }
}