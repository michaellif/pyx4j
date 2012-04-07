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
package com.propertyvista.crm.client.ui.gadgets.other;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerGadget extends AbstractGadget<BuildingLister> {
    private static final I18n i18n = I18n.get(BuildingListerGadget.class);

    public static class BuildingListerGadgetInstance extends ListerGadgetInstanceBase<BuildingDTO, BuildingLister> {

        private static final I18n i18n = I18n.get(BuildingListerGadgetInstance.class);

        private final AbstractListService<BuildingDTO> service;

        @SuppressWarnings("unchecked")
        public BuildingListerGadgetInstance(GadgetMetadata gmd) {
            super(gmd, BuildingDTO.class, BuildingLister.class);
            service = (AbstractListService<BuildingDTO>) GWT.create(BuildingCrudService.class);
            initView();
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
                    populateSucceded();
                }

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }
            }, criteria);
        }

        @Override
        protected boolean isFilterRequired() {
            return false;
        }

        @Override
        protected void onItemSelect(BuildingDTO item) {
            if ((item != null) && (item.getPrimaryKey() != null)) {
                AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Building.class, item.getPrimaryKey()));
            }
        }

        @Override
        public List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().complex()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().propertyCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().propertyManager()).build(),
                    new MemberColumnDescriptor.Builder(proto().marketing().name()).title(i18n.tr("Marketing Name")).build(),
                    new MemberColumnDescriptor.Builder(proto().info().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().info().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().info().shape()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().streetName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().city()).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().province()).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().country()).build()
            );//@formatter:on
        }

        @Override
        public Widget initContentPanel() {
            return initListerWidget();
        }
    }

    public BuildingListerGadget() {
        super(BuildingLister.class);
    }

    @Override
    public String getDescription() {
        return i18n.tr("Table-list-like gadget which displays building data according to prefered rules. Query and display data can be set up");
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Buildings.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return false;
    }

    @Override
    protected GadgetInstanceBase<BuildingLister> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new BuildingListerGadgetInstance(gadgetMetadata);
    }

}
