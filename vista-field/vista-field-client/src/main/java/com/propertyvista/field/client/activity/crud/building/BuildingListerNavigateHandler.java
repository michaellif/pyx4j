/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.activity.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.field.client.event.ListerNavigateEvent;
import com.propertyvista.field.client.event.ListerNavigateHandler;
import com.propertyvista.field.client.ui.crud.building.BuildingListerView;
import com.propertyvista.field.client.ui.viewfactories.BuildingViewFactory;
import com.propertyvista.field.rpc.FieldSiteMap;

public class BuildingListerNavigateHandler implements ListerNavigateHandler {

    BuildingListerView view;

    public BuildingListerNavigateHandler() {
        view = BuildingViewFactory.instance(BuildingListerView.class);
    }

    @Override
    public void onListerNavigate(ListerNavigateEvent event) {
        Key newItem = null;
        Key currentItem = AppSite.getPlaceController().getWhere().getItemId();
        ArrayList<DataItem<BuildingDTO>> tableData = view.getLister().getDataTablePanel().getDataTable().getDataTableModel().getData();

        switch (event.getAction()) {
        case NextItem:
            newItem = getNextItem(currentItem, tableData);
            break;
        case PreviousItem:
            newItem = getPreviousItem(currentItem, tableData);
            break;
        case Back:
            AppSite.getPlaceController().goTo(new FieldSiteMap.Properties.Building().formListerPlace());
            return;
        default:
            break;
        }

        if (newItem != null) {
            view(view.getLister().getItemOpenPlaceClass(), newItem);
        }
    }

    private Key getNextItem(Key currentItem, List<DataItem<BuildingDTO>> tableData) {
        for (int i = 0; i < tableData.size(); i++) {
            int nextItemCount = i + 1;
            Key item = tableData.get(i).getEntity().getPrimaryKey();
            if (currentItem.equals(item) && tableData.size() > nextItemCount && tableData.get(nextItemCount) != null) {
                return tableData.get(nextItemCount).getEntity().getPrimaryKey();
            }
        }

        return null;
    }

    private Key getPreviousItem(Key currentItem, ArrayList<DataItem<BuildingDTO>> tableData) {
        for (int i = 0; i < tableData.size(); i++) {
            int previousItemCount = i - 1;
            Key item = tableData.get(i).getEntity().getPrimaryKey();
            if (currentItem.equals(item) && previousItemCount >= 0 && tableData.get(previousItemCount) != null) {
                return tableData.get(previousItemCount).getEntity().getPrimaryKey();
            }
        }

        return null;
    }

    private void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formViewerPlace(itemID));
    }

}
