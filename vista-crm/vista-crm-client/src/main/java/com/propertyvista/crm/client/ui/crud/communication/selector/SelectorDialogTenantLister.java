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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.lease.Tenant;

public class SelectorDialogTenantLister extends EntityLister<Tenant> {

    private final AbstractListCrudService<Tenant> selectService;

    private final ListerDataSource<Tenant> dataSource;

    private final Collection<Tenant> alreadySelected;

    public SelectorDialogTenantLister(boolean isVersioned, Collection<Tenant> alreadySelected) {
        super(Tenant.class, isVersioned);
        this.selectService = createSelectService();
        setDataTableModel();
        this.dataSource = new ListerDataSource<Tenant>(Tenant.class, this.selectService);
        this.alreadySelected = (alreadySelected != null ? alreadySelected : Collections.<Tenant> emptyList());
        setFilters(createRestrictionFilterForAlreadySelected());
        setDataSource(dataSource);
    }

    public SelectorDialogTenantLister(boolean isVersioned) {
        this(isVersioned, null);

    }

    protected AbstractListCrudService<Tenant> createSelectService() {
        return GWT.<SelectTenantListService> create(SelectTenantListService.class);
    }

    public AbstractListCrudService<Tenant> getSelectService() {
        return this.selectService;
    }

    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        Tenant proto = EntityFactory.getEntityPrototype(Tenant.class);

        for (Tenant entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    public void setDataTableModel() {
        DataTableModel<Tenant> dataTableModel = new DataTableModel<Tenant>(defineColumnDescriptors());
        dataTableModel.setPageSize(PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    protected ColumnDescriptor[] defineColumnDescriptors() {
        return new ColumnDescriptor[] {//@formatter:off
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
        };
    }

    public void setRowsSelected(){

        if(alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<Tenant> model = getLister().getDataTablePanel().getDataTable().getDataTableModel();

        for(DataItem<Tenant> dataItem : model.getData()){
            if(alreadySelected.contains(dataItem.getEntity())){
                model.setRowSelected(true, model.indexOf(dataItem));
            }
        }
    }
}
