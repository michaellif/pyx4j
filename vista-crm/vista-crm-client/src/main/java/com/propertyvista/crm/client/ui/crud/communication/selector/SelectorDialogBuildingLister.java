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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.Collection;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

public class SelectorDialogBuildingLister extends EntityLister<Building> {

    public AbstractListCrudService<Building> selectService;

    public SelectorDialogBuildingLister(SelectRecipientsDialogForm parent) {
        this(parent, null);
    }

    public SelectorDialogBuildingLister(SelectRecipientsDialogForm parent, Collection<Building> alreadySelected) {
        super(Building.class, parent, alreadySelected);

        this.selectService = createSelectService();
        setDataTableModel();
        setDataSource(new ListerDataSource<Building>(Building.class, this.selectService));
    }

    protected AbstractListCrudService<Building> createSelectService() {
        return GWT.<SelectBuildingListService> create(SelectBuildingListService.class);
    }

    public AbstractListCrudService<Building> getSelectService() {
        return this.selectService;
    }

    public void setDataTableModel() {
        DataTableModel<Building> dataTableModel = new DataTableModel<Building>(defineColumnDescriptors());
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    protected ColumnDescriptor[] defineColumnDescriptors() {
        return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto().propertyCode(), true).build(),
                new MemberColumnDescriptor.Builder(proto().complex(), false).build(),
                new MemberColumnDescriptor.Builder(proto().externalId(), false).build(),
                new MemberColumnDescriptor.Builder(proto().portfolios(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().name(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().type(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().shape(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().streetNumber(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().streetName(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().city(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().province(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().country(), false).build(),
                new MemberColumnDescriptor.Builder(proto().marketing().visibility(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().totalStoreys(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().residentialStoreys(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().structureType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().structureBuildYear(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().constructionType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().foundationType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().floorType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().landArea(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().waterSupply(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().centralAir(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().centralHeat(), false).build(),
                new MemberColumnDescriptor.Builder(proto().contacts().website(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().dateAcquired(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().purchasePrice(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().marketPrice(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalDate(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalValue(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().currency().name(), false).title(proto().financial().currency()).build(),
                new MemberColumnDescriptor.Builder(proto().marketing().name(), false).title("Marketing Name").build()
        }; //@formatter:on
    }
}
