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
package com.propertyvista.field.client.ui.crud.unit;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitLister extends AbstractLister<AptUnitDTO> {

    public static final I18n i18n = I18n.get(UnitLister.class);

    public UnitLister() {
        this(true);
        getDataTablePanel().getAddButton().setCaption(i18n.tr("New Unit"));
    }

    public UnitLister(boolean allowAddNew) {
        super(AptUnitDTO.class, !VistaFeatures.instance().yardiIntegration() ? allowAddNew : false);
        setupColumns();
    }

    protected void setupColumns() {
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().buildingCode()).build(),
                
                new MemberColumnDescriptor.Builder(proto().floorplan().name()).title(i18n.tr("Floorplan Name")).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                
                new MemberColumnDescriptor.Builder(proto().info().floor()).build(),
                new MemberColumnDescriptor.Builder(proto().info().number()).build(),
                new MemberColumnDescriptor.Builder(proto().info().area()).build(),
                new MemberColumnDescriptor.Builder(proto().info().areaUnits()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bedrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bathrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().info().economicStatus()).visible(false).build(),
                
                new MemberColumnDescriptor.Builder(proto().financial()._unitRent()).build(),
                new MemberColumnDescriptor.Builder(proto().financial()._marketRent()).build(),
                
                new MemberColumnDescriptor.Builder(proto()._availableForRent()).build()
       );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().buildingCode(), false), new Sort(proto().info().number(), false));
    }

    @Override
    protected void onItemNew() {
//        Key parentBuildingPk = getPresenter().getParent();
//        if (parentBuildingPk == null) {
//            new BuildingSelectorDialog() {
//                @Override
//                public boolean onClickOk() {
//                    if (!getSelectedItems().isEmpty()) {
//                        AptUnitDTO newUnit = EntityFactory.create(AptUnitDTO.class);
//                        newUnit.building().set(getSelectedItems().get(0));
//                        getPresenter().editNew(getItemOpenPlaceClass(), newUnit);
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            }.show();
//        } else {
//            GWT.<BuildingCrudService> create(BuildingCrudService.class).retrieve(new DefaultAsyncCallback<BuildingDTO>() {
//                @Override
//                public void onSuccess(BuildingDTO result) {
//                    AptUnitDTO newUnit = EntityFactory.create(AptUnitDTO.class);
//                    newUnit.building().set(result.duplicate(Building.class));
//                    getPresenter().editNew(getItemOpenPlaceClass(), newUnit);
//                }
//            }, parentBuildingPk, RetrieveTarget.View);
//        }
    }
}
