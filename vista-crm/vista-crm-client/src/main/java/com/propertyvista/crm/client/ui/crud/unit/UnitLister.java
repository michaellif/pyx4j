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
package com.propertyvista.crm.client.ui.crud.unit;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;

public class UnitLister extends ListerBase<AptUnitDTO> {

    private static final I18n i18n = I18n.get(UnitLister.class);

    public UnitLister() {
        this(true);
    }

    public UnitLister(boolean allowAddNew) {
        super(AptUnitDTO.class, false, allowAddNew);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().buildingCode()).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().name()).title(i18n.tr("Floorplan Name")).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().marketingName()).title(i18n.tr("Floorplan Marketing Name")).build(),
                new MemberColumnDescriptor.Builder(proto().info().economicStatus()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().info().floor()).build(),
                new MemberColumnDescriptor.Builder(proto().info().number()).build(),
                new MemberColumnDescriptor.Builder(proto().info().area()).build(),
                new MemberColumnDescriptor.Builder(proto().info().areaUnits()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bedrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bathrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().financial()._unitRent()).build(),
                new MemberColumnDescriptor.Builder(proto().financial()._marketRent()).build(),
                new MemberColumnDescriptor.Builder(proto()._availableForRent()).build()
       );//@formatter:on

    }

    @Override
    protected void onItemNew() {
        Key parentBuildingPk = getPresenter().getParent();
        if (parentBuildingPk == null) {
            new BuildingSelectorDialog() {
                @Override
                public boolean onClickOk() {
                    if (!getSelectedItems().isEmpty()) {
                        AptUnitDTO newUnit = EntityFactory.create(AptUnitDTO.class);
                        newUnit.belongsTo().set(getSelectedItems().get(0));
                        getPresenter().editNew(getItemOpenPlaceClass(), newUnit);
                        return true;
                    } else {
                        return false;
                    }
                }
            }.show();
        } else {
            GWT.<BuildingCrudService> create(BuildingCrudService.class).retrieve(new DefaultAsyncCallback<BuildingDTO>() {
                @Override
                public void onSuccess(BuildingDTO result) {
                    AptUnitDTO newUnit = EntityFactory.create(AptUnitDTO.class);
                    newUnit.belongsTo().set(result.duplicate(Building.class));
                    getPresenter().editNew(getItemOpenPlaceClass(), newUnit);
                }
            }, parentBuildingPk, RetrieveTraget.View);
        }
    }
}
