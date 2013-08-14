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

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectCustomerListService;
import com.propertyvista.domain.tenant.Customer;

public abstract class CustomerSelectorDialog extends EntitySelectorTableDialog<Customer> {

    private final static I18n i18n = I18n.get(CustomerSelectorDialog.class);

    public CustomerSelectorDialog() {
        this(Collections.<Customer> emptyList());
    }

    public CustomerSelectorDialog(boolean isMultiselect) {
        this(isMultiselect, Collections.<Customer> emptyList());
    }

    public CustomerSelectorDialog(List<Customer> alreadySelected) {
        this(true, alreadySelected);
    }

    public CustomerSelectorDialog(boolean isMultiselect, List<Customer> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Customer"));
    }

    public CustomerSelectorDialog(boolean isMultiselect, List<Customer> alreadySelected, String caption) {
        super(Customer.class, isMultiselect, alreadySelected, caption);
        setWidth("700px");
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().person().name()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().person().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().person().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().person().sex()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().person().birthDate()).build(),
                new MemberColumnDescriptor.Builder(proto().person().email()).build(),
                new MemberColumnDescriptor.Builder(proto().person().homePhone()).build(),
                new MemberColumnDescriptor.Builder(proto().person().mobilePhone()).build(),
                new MemberColumnDescriptor.Builder(proto().person().workPhone()).visible(false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().person().name(), false));
    }

    @Override
    protected AbstractListService<Customer> getSelectService() {
        return GWT.<SelectCustomerListService> create(SelectCustomerListService.class);
    }
}