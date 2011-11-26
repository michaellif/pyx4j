/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.ui.crud.building.BuildingLister;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerGadget extends ListerGadgetBase<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingLister.class);

    private final AbstractListService<BuildingDTO> service;

    @SuppressWarnings("unchecked")
    public BuildingListerGadget(GadgetMetadata gmd) {
        super(gmd, BuildingDTO.class);
        service = (AbstractListService<BuildingDTO>) GWT.create(BuildingCrudService.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue(GadgetType.BuildingLister.toString());
    }

    @Override
    public Widget asWidget() {
        return getListerWidget().asWidget();
    }

    @Override
    public void populatePage(final int pageNumber) {
        EntityListCriteria<BuildingDTO> criteria = new EntityListCriteria<BuildingDTO>(BuildingDTO.class);
        criteria.setPageSize(getPageSize());
        criteria.setPageNumber(pageNumber);
        // apply sorts:
        criteria.setSorts(getSorting());

        service.list(new AsyncCallback<EntitySearchResult<BuildingDTO>>() {
            @Override
            public void onSuccess(EntitySearchResult<BuildingDTO> result) {
                setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO report error                        
            }
        }, criteria);
    }

    @Override
    protected boolean isFilterRequired() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onItemSelect(BuildingDTO item) {
        if ((item != null) && (item.getPrimaryKey() != null)) {
            AppSite.getPlaceController().goTo(MainActivityMapper.getCrudAppPlace(Building.class).formViewerPlace(item.getPrimaryKey()));
        }
    }

    //@formatter:off
    @SuppressWarnings("unchecked")
    @Override    
    public List<ColumnDescriptor<BuildingDTO>> defineColumnDescriptors() {
        return Arrays.asList(
                colh(proto().complex()),
                colv(proto().propertyCode()),
                colv(proto().propertyManager()),
                colv(proto().marketing().name(), i18n.tr("Marketing Name")),
                colv(proto().info().name()),
                colv(proto().info().type()),
                colh(proto().info().shape()),
                colh(proto().info().address().streetName()),
                colv(proto().info().address().city()),
                colv(proto().info().address().province()),
                colv(proto().info().address().country())
        );
    }
    //@formatter:on
}
