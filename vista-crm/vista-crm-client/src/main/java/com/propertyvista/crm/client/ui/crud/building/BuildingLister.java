/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingLister extends SiteDataTablePanel<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingLister.class);

    public BuildingLister() {
        super(BuildingDTO.class, GWT.<AbstractCrudService<BuildingDTO>> create(BuildingCrudService.class), !VistaFeatures.instance().yardiIntegration());

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().propertyCode()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().complex().name()).columnTitle(proto().complex().getMeta().getCaption()).width("150px").build(), //
                new ColumnDescriptor.Builder(proto().complexPrimary()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().externalId()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().integrationSystemId()).visible(VistaFeatures.instance().yardiIntegration()).build(), //
                new ColumnDescriptor.Builder(proto().suspended()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().updated()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().info().name()).width("150px").filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().type()).build(), //
                new ColumnDescriptor.Builder(proto().info().shape()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().streetNumber()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().streetName()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().city()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().address().province()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().address().country()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().marketing().visibility()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().totalStoreys()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().residentialStoreys()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().structureType()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().structureBuildYear()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().constructionType()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().foundationType()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().floorType()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().landArea()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().waterSupply()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().centralAir()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().centralHeat()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().contacts().website()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().financial().dateAcquired()).build(), //
                new ColumnDescriptor.Builder(proto().financial().purchasePrice()).build(), //
                new ColumnDescriptor.Builder(proto().financial().marketPrice()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalDate()).build(), //
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalValue()).build(), //

                new ColumnDescriptor.Builder(proto().defaultProductCatalog()).visible(false).sortable(false).build(), //

                new ColumnDescriptor.Builder(proto().merchantAccountPresent()).visible(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().currency()).visible(false).searchable(false).build(), //

                new ColumnDescriptor.Builder(proto().marketing().name()).visible(false).columnTitle(i18n.tr("Marketing Name")).build());

        setDataTableModel(new DataTableModel<BuildingDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().propertyCode(), false));
    }
}
