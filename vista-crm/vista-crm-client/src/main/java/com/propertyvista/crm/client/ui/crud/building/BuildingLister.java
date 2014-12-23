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
                new ColumnDescriptor.Builder(proto().propertyCode(), true).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().complex()).width("150px").build(), //
                new ColumnDescriptor.Builder(proto().complexPrimary(), false).build(), //
                new ColumnDescriptor.Builder(proto().externalId(), false).build(), //
                new ColumnDescriptor.Builder(proto().integrationSystemId(), VistaFeatures.instance().yardiIntegration()).build(), //
                new ColumnDescriptor.Builder(proto().suspended(), false).build(), //
                new ColumnDescriptor.Builder(proto().updated(), false).build(), //

                new ColumnDescriptor.Builder(proto().info().name(), true).width("150px").build(), //
                new ColumnDescriptor.Builder(proto().info().type(), true).build(), //
                new ColumnDescriptor.Builder(proto().info().shape(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().streetNumber(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().streetName(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().city(), true).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().address().province(), true).build(), //
                new ColumnDescriptor.Builder(proto().info().address().country(), false).build(), //
                new ColumnDescriptor.Builder(proto().marketing().visibility(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().totalStoreys(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().residentialStoreys(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().structureType(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().structureBuildYear(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().constructionType(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().foundationType(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().floorType(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().landArea(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().waterSupply(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().centralAir(), false).build(), //
                new ColumnDescriptor.Builder(proto().info().centralHeat(), false).build(), //

                new ColumnDescriptor.Builder(proto().contacts().website(), false).build(), //

                new ColumnDescriptor.Builder(proto().financial().dateAcquired(), true).build(), //
                new ColumnDescriptor.Builder(proto().financial().purchasePrice(), true).build(), //
                new ColumnDescriptor.Builder(proto().financial().marketPrice(), false).build(), //
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalDate(), true).build(), //
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalValue(), true).build(), //

                new ColumnDescriptor.Builder(proto().defaultProductCatalog(), false).sortable(false).build(), //

                new ColumnDescriptor.Builder(proto().merchantAccountPresent(), false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().currency(), false).build(), //

                new ColumnDescriptor.Builder(proto().marketing().name(), false).columnTitle(i18n.tr("Marketing Name")).build());

        setDataTableModel(new DataTableModel<BuildingDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().propertyCode(), false));
    }
}
