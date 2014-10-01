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

    private final Collection<Tenant> alreadySelected;

    private final Collection<Tenant> selectedOnTab;

    private final SelectRecipientsDialogForm parent;

    public SelectorDialogTenantLister(SelectRecipientsDialogForm parent, Collection<Tenant> alreadySelected) {
        super(Tenant.class, false);
        this.parent = parent;
        this.selectService = createSelectService();
        setDataTableModel();
        this.alreadySelected = (alreadySelected != null ? alreadySelected : new ArrayList<Tenant>());
        addItemSelectionHandler(new ItemSelectionHandler<Tenant>() {
            @Override
            public void onSelect(Tenant selectedItem) {
                identifySelected();
            }
        });

        setFilters(createRestrictionFilterForAlreadySelected());
        setDataSource(new ListerDataSource<Tenant>(Tenant.class, this.selectService));
        selectedOnTab = new ArrayList<Tenant>();
    }

    public SelectorDialogTenantLister(SelectRecipientsDialogForm parent, boolean isVersioned) {
        this(parent, null);
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


    @Override
    protected void onObtainSuccess() {
        super.onObtainSuccess();
        setRowsSelected();
        setTabSelected();
    }

    public void setRowsSelected(){

        if(alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<Tenant> model = getLister().getDataTablePanel().getDataTable().getDataTableModel();

        for(DataItem<Tenant> dataItem : model.getData()){
            if(alreadySelected.contains(dataItem.getEntity())){
                model.selectRow(true, model.indexOf(dataItem));
            }
        }
    }
    private void refreshAlreadySelected(Tenant selectedItem){
        if (!alreadySelected.contains(selectedItem)) {
            alreadySelected.add(selectedItem);
        } else {
            alreadySelected.remove(selectedItem);
        }
    }

    private void setTabSelected(){
        selectedOnTab.clear();
        for(DataItem<Tenant> dataItem : getLister().getDataTablePanel().getDataTable().getDataTableModel().getSelectedRows()){
            selectedOnTab.add(dataItem.getEntity());
        }
    }

    private void identifySelected(){
        Collection<Tenant> changed = new ArrayList<Tenant>();

        for(DataItem<Tenant> dataItem : getLister().getDataTablePanel().getDataTable().getDataTableModel().getSelectedRows()){
            changed.add(dataItem.getEntity());
        }

        if(changed.size() > selectedOnTab.size()){
            changed.removeAll(selectedOnTab);
            if(changed.size()!=0){
                addItem(((ArrayList<Tenant>)changed).get(0));
            }
        }else{
            selectedOnTab.removeAll(changed);
            if(selectedOnTab.size()!=0){
                removeItem(((ArrayList<Tenant>)selectedOnTab).get(0));
            }
            selectedOnTab.addAll(changed);
        }
    }

    private void addItem(Tenant addItem){
        selectedOnTab.add(addItem);
        alreadySelected.add(addItem);
        parent.addSelected(addItem);
    }

    private void removeItem(Tenant removeItem){
        selectedOnTab.remove(removeItem);
        alreadySelected.remove(removeItem);
        parent.removeSelected(removeItem, Tenant.class);
    }

}
