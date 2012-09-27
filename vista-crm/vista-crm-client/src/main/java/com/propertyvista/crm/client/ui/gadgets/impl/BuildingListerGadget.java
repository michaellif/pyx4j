/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerGadget extends ListerGadgetInstanceBase<BuildingDTO, BuildingListerGadgetMetadata> {

    private final AbstractListService<BuildingDTO> service;

    @SuppressWarnings("unchecked")
    public BuildingListerGadget(BuildingListerGadgetMetadata gmd) {
        super(gmd, BuildingListerGadgetMetadata.class, null, BuildingDTO.class, false);
        service = (AbstractListService<BuildingDTO>) GWT.create(BuildingCrudService.class);
        initView();
    }

    @Override
    protected Widget initContentPanel() {
        return initListerWidget();
    }

    @Override
    protected void populatePage(final int pageNumber) {
        EntityListCriteria<BuildingDTO> criteria = new EntityListCriteria<BuildingDTO>(BuildingDTO.class);
        criteria.setPageSize(getPageSize());
        criteria.setPageNumber(pageNumber);
        // apply sorts:
        criteria.setSorts(new Vector<Sort>(getListerSortingCriteria()));

        service.list(new AsyncCallback<EntitySearchResult<BuildingDTO>>() {
            @Override
            public void onSuccess(EntitySearchResult<BuildingDTO> result) {
                setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                populateSucceded();
            }

            @Override
            public void onFailure(Throwable caught) {
                populateFailed(caught);
            }
        }, criteria);
    }

    @Override
    protected void onItemSelect(BuildingDTO item) {
        if ((item != null) && (item.getPrimaryKey() != null)) {
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Building.class, item.getPrimaryKey()));
        }
    }

}