/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.selector.IOptionsGrabber;

import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingOptionsGrabber implements IOptionsGrabber<BuildingForSelectionDTO> {

    private final SelectBuildingListService service;

    private List<BuildingForSelectionDTO> filtered;

    public BuildingOptionsGrabber() {
        service = //createCachingProxy(
        GWT.<SelectBuildingListService> create(SelectBuildingListService.class);
        filtered = new LinkedList<BuildingForSelectionDTO>();
    }

    @Override
    public void grabOptions(final IOptionsGrabber.Request request, final IOptionsGrabber.Callback<BuildingForSelectionDTO> callback) {

        AsyncCallback<Vector<BuildingForSelectionDTO>> callbackOptionsGrabber = new DefaultAsyncCallback<Vector<BuildingForSelectionDTO>>() {
            @Override
            public void onSuccess(Vector<BuildingForSelectionDTO> result) {
                filter(result, request.getQuery().toLowerCase());
                callback.onOptionsReady(request, new Response<BuildingForSelectionDTO>(filtered));
            }

        };

        EntityListCriteria<Building> criteria = EntityListCriteria.create(Building.class);
        criteria.setPageSize(request.getLimit());
        service.getBuildingsForSelection(callbackOptionsGrabber, criteria);

    }

    protected int evaluate(BuildingForSelectionDTO item, String suggestion) {
        if (item.name().getValue().toLowerCase().contains(suggestion)) {
            return 1;
        } else {
            return 0;
        }
    }

    private void filter(Vector<BuildingForSelectionDTO> result, String suggestion) {
        filtered = new LinkedList<BuildingForSelectionDTO>();
        if ("".equals(suggestion)) {
            filtered.addAll(result);
        } else {
            for (BuildingForSelectionDTO item : result) {
                if (evaluate(item, suggestion) > 0) {
                    filtered.add(item);
                }
            }
        }
    }
}
