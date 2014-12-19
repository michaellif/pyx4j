/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class BuildingSelectionDialog extends EntitySelectorTableDialog<Building> {

    private final static I18n i18n = I18n.get(BuildingSelectionDialog.class);

    public BuildingSelectionDialog() {
        this(Collections.<Building> emptySet());
    }

    public BuildingSelectionDialog(Collection<Building> alreadySelected) {
        this(false, alreadySelected);
    }

    public BuildingSelectionDialog(boolean isMultiselect, Collection<Building> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Building"));
    }

    public BuildingSelectionDialog(boolean isMultiselect, Collection<Building> alreadySelected, String caption) {
        super(Building.class, isMultiselect, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off                    
                new ColumnDescriptor.Builder(proto().propertyCode(), true).build(),
                new ColumnDescriptor.Builder(proto().complex(), false).build(),
                new ColumnDescriptor.Builder(proto().externalId(), false).build(),
                new ColumnDescriptor.Builder(proto().portfolios(), true).build(),
                new ColumnDescriptor.Builder(proto().info().name(), true).build(),
                new ColumnDescriptor.Builder(proto().info().type(), true).build(),
                new ColumnDescriptor.Builder(proto().info().shape(), false).build(),
                new ColumnDescriptor.Builder(proto().info().address().streetNumber(), false).build(),
                new ColumnDescriptor.Builder(proto().info().address().streetName(), false).build(),
                new ColumnDescriptor.Builder(proto().info().address().city(), true).build(),
                new ColumnDescriptor.Builder(proto().info().address().province(), true).build(),
                new ColumnDescriptor.Builder(proto().info().address().country(), false).build(),
                new ColumnDescriptor.Builder(proto().marketing().visibility(), false).build(),
                new ColumnDescriptor.Builder(proto().info().totalStoreys(), false).build(),
                new ColumnDescriptor.Builder(proto().info().residentialStoreys(), false).build(),
                new ColumnDescriptor.Builder(proto().info().structureType(), false).build(),
                new ColumnDescriptor.Builder(proto().info().structureBuildYear(), false).build(),
                new ColumnDescriptor.Builder(proto().info().constructionType(), false).build(),
                new ColumnDescriptor.Builder(proto().info().foundationType(), false).build(),
                new ColumnDescriptor.Builder(proto().info().floorType(), false).build(),
                new ColumnDescriptor.Builder(proto().info().landArea(), false).build(),
                new ColumnDescriptor.Builder(proto().info().waterSupply(), false).build(),
                new ColumnDescriptor.Builder(proto().info().centralAir(), false).build(),
                new ColumnDescriptor.Builder(proto().info().centralHeat(), false).build(),
                new ColumnDescriptor.Builder(proto().contacts().website(), false).build(),
                new ColumnDescriptor.Builder(proto().financial().dateAcquired(), false).build(),
                new ColumnDescriptor.Builder(proto().financial().purchasePrice(), false).build(),
                new ColumnDescriptor.Builder(proto().financial().marketPrice(), false).build(),
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalDate(), false).build(),
                new ColumnDescriptor.Builder(proto().financial().lastAppraisalValue(), false).build(),
                new ColumnDescriptor.Builder(proto().financial().currency(), false).build(),
                new ColumnDescriptor.Builder(proto().marketing().name(), false).columnTitle(i18n.tr("Marketing Name")).build()
        ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().propertyCode(), false));
    }

    @Override
    protected AbstractListCrudService<Building> getSelectService() {
        return GWT.<AbstractListCrudService<Building>> create(SelectBuildingListService.class);
    }
}