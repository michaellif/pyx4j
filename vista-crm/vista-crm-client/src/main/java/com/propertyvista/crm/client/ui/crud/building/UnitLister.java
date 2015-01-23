/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-05
 * @author VladL
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitLister extends SiteDataTablePanel<AptUnitDTO> {

    private static final I18n i18n = I18n.get(UnitLister.class);

    public UnitLister() {
        super(AptUnitDTO.class, GWT.<AbstractCrudService<AptUnitDTO>> create(UnitCrudService.class), !VistaFeatures.instance().yardiIntegration());

        setAddNewActionCaption(i18n.tr("New Unit"));

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().info().floor()).build(), //
                new ColumnDescriptor.Builder(proto().info().number()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().area()).build(), //
                new ColumnDescriptor.Builder(proto().info().areaUnits()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info()._bedrooms()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info()._bathrooms()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().economicStatus()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().buildingCode()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().floorplan().name()).columnTitle(i18n.tr("Floorplan Name")).build(), //
                new ColumnDescriptor.Builder(proto().floorplan().marketingName()).visible(false).columnTitle(i18n.tr("Floorplan Marketing Name")).build(), //

                new ColumnDescriptor.Builder(proto().financial()._unitRent()).build(), //
                new ColumnDescriptor.Builder(proto().financial()._marketRent()).build(), //

                new ColumnDescriptor.Builder(proto().availability().availableForRent()).build());

        setDataTableModel(new DataTableModel<AptUnitDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().info().floor(), false), new Sort(proto().info().number(), false));
    }

    @Override
    protected void onItemNew() {
        UnitCrudService.UnitInitializationdata id = EntityFactory.create(UnitCrudService.UnitInitializationdata.class);
        id.parent().set(EntityFactory.createIdentityStub(Building.class, getDataSource().getParentEntityId()));
        editNew(getItemOpenPlaceClass(), id);
    }
}
