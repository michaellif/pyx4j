/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.Collection;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;

import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class SelectorDialogPortfolioLister extends EntityLister<Portfolio> {

    public SelectorDialogPortfolioLister(SelectRecipientsDialogForm parent, Collection<Portfolio> alreadySelected) {
        super(Portfolio.class, GWT.<SelectPortfolioListService> create(SelectPortfolioListService.class), parent, alreadySelected);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().description()).build());

        DataTableModel<Portfolio> dataTableModel = new DataTableModel<Portfolio>();
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

}
