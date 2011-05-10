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
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.portal.domain.AptUnit;

public class UnitLister extends ListerBase<AptUnit> {

    public UnitLister() {
        super(AptUnit.class);

        // add editing on double-click: 
        getListPanel().getDataTable().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                AppPlace link = new CrmSiteMap.Editors.Unit();
                // put selected item ID in link arguments:
                HashMap<String, String> args = new HashMap<String, String>();
                int selectedRow = getListPanel().getDataTable().getSelectedRow();
                AptUnit item = getListPanel().getDataTable().getDataTableModel().getData().get(selectedRow).getEntity();
                args.put(CrmSiteMap.ARG_NAME_ITEM_ID, item.getPrimaryKey().toString());
                link.setArgs(args);

                AppSite.getPlaceController().goTo(link);
            }
        });
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<AptUnit>> columnDescriptors, AptUnit proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitEcomomicStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitEcomomicStatusDescr()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.suiteNumber()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.building().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bathrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.currentOccupancies()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.avalableForRent()));
    }

    @Override
    public void populateData(final int pageNumber) {
        UnitCrudService service = GWT.create(UnitCrudService.class);
        if (service != null) {
            EntitySearchCriteria<AptUnit> criteria = new EntitySearchCriteria<AptUnit>(AptUnit.class);
            criteria.setPageSize(getListPanel().getPageSize());
            criteria.setPageNumber(pageNumber);

            service.search(new AsyncCallback<EntitySearchResult<AptUnit>>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(EntitySearchResult<AptUnit> result) {
                    UnitLister.this.getListPanel().populateData(result.getData(), pageNumber, result.hasMoreData());
                }
            }, criteria);
        }
    }
}
