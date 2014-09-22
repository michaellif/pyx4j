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

import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class SelectorDialogPortfolioLister extends EntityLister<Portfolio> {

    public AbstractListCrudService<Portfolio> selectService;

    private final ListerDataSource<Portfolio> dataSource;

    private final Collection<Portfolio> alreadySelected;

    public SelectorDialogPortfolioLister(boolean isVersioned) {
        this(isVersioned, null);
    }

    public SelectorDialogPortfolioLister(boolean isVersioned, Collection<Portfolio> alreadySelected) {
        super(Portfolio.class, isVersioned);
        this.selectService = createSelectService();
        setDataTableModel();
        this.dataSource = new ListerDataSource<Portfolio>(Portfolio.class, this.selectService);
        this.alreadySelected = (alreadySelected != null ? alreadySelected : Collections.<Portfolio> emptyList());
        setFilters(createRestrictionFilterForAlreadySelected());
        setDataSource(new ListerDataSource<Portfolio>(Portfolio.class, this.selectService));

    }

    protected AbstractListCrudService<Portfolio> createSelectService() {
        return GWT.<SelectPortfolioListService> create(SelectPortfolioListService.class);
    }

    public AbstractListCrudService<Portfolio> getSelectService() {
        return this.selectService;
    }

    public void setDataTableModel() {
        DataTableModel<Portfolio> dataTableModel = new DataTableModel<Portfolio>(defineColumnDescriptors());
        dataTableModel.setPageSize(PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    protected ColumnDescriptor[] defineColumnDescriptors() {
        return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto().name()).build(),
                new MemberColumnDescriptor.Builder(proto().description()).wordWrap(true).build()
        };
    }
    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        Portfolio proto = EntityFactory.getEntityPrototype(Portfolio.class);

        for (Portfolio entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    public void setRowsSelected(){

        if(alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<Portfolio> model = getLister().getDataTablePanel().getDataTable().getDataTableModel();

        for(DataItem<Portfolio> dataItem : model.getData()){
            if(alreadySelected.contains(dataItem.getEntity())){
                model.setRowSelected(true, model.indexOf(dataItem));
            }
        }
    }
}
