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

import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

public class SelectorDialogBuildingLister extends EntityLister<Building> {

    public SelectorDialogBuildingLister(SelectRecipientsDialogForm parent) {
        this(parent, null);
    }

    public SelectorDialogBuildingLister(SelectRecipientsDialogForm parent, Collection<Building> alreadySelected) {
        super(Building.class, GWT.<SelectBuildingListService> create(SelectBuildingListService.class), parent, alreadySelected);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().propertyCode()).filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().info().name()).build(),//
                new ColumnDescriptor.Builder(proto().info().address().streetNumber()).visible(false).build(),//
                new ColumnDescriptor.Builder(proto().info().address().streetName()).visible(false).build(),//
                new ColumnDescriptor.Builder(proto().info().address().city()).filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().info().address().province()).filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().info().address().country()).visible(false).filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().marketing().name()).visible(false).columnTitle("Marketing Name").build() //
        );

        DataTableModel<Building> dataTableModel = new DataTableModel<Building>();
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

}
