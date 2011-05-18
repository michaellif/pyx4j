/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.listers;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.Building;

public class BuildingLister extends ListerBase<Building> {

    public BuildingLister() {
        super(Building.class);

        // add editing on double-click: 
        getListPanel().getDataTable().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                AppPlace link = new CrmSiteMap.Editors.Building();
//                AppPlace link = new CrmSiteMap.Viewers.Building();
                // put selected item ID in link arguments:
                HashMap<String, String> args = new HashMap<String, String>();
                int selectedRow = getListPanel().getDataTable().getSelectedRow();
                Building item = getListPanel().getDataTable().getDataTableModel().getData().get(selectedRow).getEntity();
                args.put(CrmSiteMap.ARG_NAME_ITEM_ID, item.getPrimaryKey().toString());
                link.setArgs(args);

                AppSite.getPlaceController().goTo(link);
            }
        });
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Building>> columnDescriptors, Building proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.website()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.email().emailAddress()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address().country()));
    }

    @Override
    public void populateData(final int pageNumber) {
        BuildingCrudService service = GWT.create(BuildingCrudService.class);
        if (service != null) {
            EntitySearchCriteria<Building> criteria = new EntitySearchCriteria<Building>(Building.class);
            criteria.setPageSize(getListPanel().getPageSize());
            criteria.setPageNumber(pageNumber);

            service.search(new AsyncCallback<EntitySearchResult<Building>>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(EntitySearchResult<Building> result) {
                    BuildingLister.this.getListPanel().populateData(result.getData(), pageNumber, result.hasMoreData());
                }
            }, criteria);
        }
    }
}
