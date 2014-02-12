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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitLister extends AbstractLister<AptUnitDTO> {

    public static final I18n i18n = I18n.get(UnitLister.class);

    public UnitLister() {
        this(true);
    }

    public UnitLister(boolean allowAddNew) {
        super(AptUnitDTO.class, !VistaFeatures.instance().yardiIntegration() ? allowAddNew : false);

        if (getDataTablePanel().getAddButton() != null) {
            getDataTablePanel().getAddButton().setCaption(i18n.tr("New Unit"));
        }

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
                
                new MemberColumnDescriptor.Builder(proto().availability().availableForRent()).build()
       );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().buildingCode(), false), new Sort(proto().info().number(), false));
    }

    @Override
    protected void onItemNew() {
        final Key parentBuildingPk = getPresenter().getParent();
        final UnitCrudService.UnitInitializationdata id = EntityFactory.create(UnitCrudService.UnitInitializationdata.class);
        if (parentBuildingPk == null) {
            new BuildingSelectorDialog() {
                @Override
                public boolean onClickOk() {
                    if (!getSelectedItems().isEmpty()) {
                        id.parent().set(getSelectedItems().get(0));
                        getPresenter().editNew(getItemOpenPlaceClass(), id);
                        return true;
                    } else {
                        return false;
                    }
                }
            }.show();
        } else {
            id.parent().set(EntityFactory.createIdentityStub(Building.class, parentBuildingPk));
            getPresenter().editNew(getItemOpenPlaceClass(), id);
        }
    }
}
