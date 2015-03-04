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
package com.propertyvista.crm.client.ui.crud.administration.merchantaccount;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingLister extends SiteDataTablePanel<Building> {

    private static final I18n i18n = I18n.get(BuildingLister.class);

    public BuildingLister() {
        super(Building.class, GWT.<AbstractListCrudService<Building>> create(SelectBuildingListService.class), false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().propertyCode()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().complex()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().externalId()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().integrationSystemId()).visible(VistaFeatures.instance().yardiIntegration()).build(), //
                new ColumnDescriptor.Builder(proto().info().name()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().type()).build(), //
                new ColumnDescriptor.Builder(proto().info().shape()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().streetNumber()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().streetName()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().info().address().city()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().address().province()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().info().address().country()).visible(false).filterAlwaysShown(true).build(), //
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
                new ColumnDescriptor.Builder(proto().financial().dateAcquired()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().purchasePrice()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().marketPrice()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalDate()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalValue()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().financial().currency()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().marketing().name()).visible(false).columnTitle(i18n.tr("Marketing Name")).build());

        setDataTableModel(new DataTableModel<Building>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().propertyCode(), false));
    }
}
