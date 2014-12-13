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

import com.propertyvista.crm.rpc.services.selections.SelectCustomerListService;
import com.propertyvista.domain.tenant.Customer;

public abstract class CustomerSelectionDialog extends EntitySelectorTableDialog<Customer> {

    private final static I18n i18n = I18n.get(CustomerSelectionDialog.class);

    public CustomerSelectionDialog() {
        this(Collections.<Customer> emptySet());
    }

    public CustomerSelectionDialog(Set<Customer> alreadySelected) {
        this(false, alreadySelected);
    }

    public CustomerSelectionDialog(boolean isMultiselect, Set<Customer> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Customer"));
    }

    public CustomerSelectionDialog(boolean isMultiselect, Set<Customer> alreadySelected, String caption) {
        super(Customer.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto().customerId()).build(),
                new ColumnDescriptor.Builder(proto().person().name()).searchable(false).build(),
                new ColumnDescriptor.Builder(proto().person().name().firstName()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().person().name().lastName()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().person().sex()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().person().birthDate()).build(),
                new ColumnDescriptor.Builder(proto().person().email()).build(),
                new ColumnDescriptor.Builder(proto().person().homePhone()).build(),
                new ColumnDescriptor.Builder(proto().person().mobilePhone()).build(),
                new ColumnDescriptor.Builder(proto().person().workPhone()).visible(false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().person().name(), false));
    }

    @Override
    protected AbstractListCrudService<Customer> getSelectService() {
        return GWT.<SelectCustomerListService> create(SelectCustomerListService.class);
    }
}