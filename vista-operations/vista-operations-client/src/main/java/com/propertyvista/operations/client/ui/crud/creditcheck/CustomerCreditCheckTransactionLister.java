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
package com.propertyvista.operations.client.ui.crud.creditcheck;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.CustomerCreditCheckTransactionDTO;
import com.propertyvista.operations.rpc.services.CustomerCreditCheckTransactionCrudService;

public class CustomerCreditCheckTransactionLister extends SiteDataTablePanel<CustomerCreditCheckTransactionDTO> {

    protected static final I18n i18n = I18n.get(CustomerCreditCheckTransactionLister.class);

    public CustomerCreditCheckTransactionLister() {
        super(CustomerCreditCheckTransactionDTO.class, GWT.<CustomerCreditCheckTransactionCrudService> create(CustomerCreditCheckTransactionCrudService.class),
                false);

        setColumnDescriptors( //
                new MemberColumnDescriptor.Builder(proto().pmc()).build(), //
                new MemberColumnDescriptor.Builder(proto().amount()).build(), //
                new MemberColumnDescriptor.Builder(proto().tax()).build(), //
                new MemberColumnDescriptor.Builder(proto().paymentMethod()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().status()).build(), //
                new MemberColumnDescriptor.Builder(proto().transactionAuthorizationNumber()).build(), //
                new MemberColumnDescriptor.Builder(proto().transactionDate()).build() //
        );

        setDataTableModel(new DataTableModel<CustomerCreditCheckTransactionDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().transactionDate(), false));
    }
}
