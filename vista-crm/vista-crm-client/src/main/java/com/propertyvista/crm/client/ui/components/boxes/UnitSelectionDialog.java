/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vladlouk
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectUnitListService;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public abstract class UnitSelectionDialog extends EntitySelectorTableDialog<AptUnit> {

    private static final I18n i18n = I18n.get(UnitSelectionDialog.class);

    public UnitSelectionDialog() {
        this(Collections.<AptUnit> emptySet());
    }

    public UnitSelectionDialog(Set<AptUnit> alreadySelected) {
        this(false, alreadySelected);
    }

    public UnitSelectionDialog(boolean isMultiselect, Set<AptUnit> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Unit"));
    }

    public UnitSelectionDialog(boolean isMultiselect, Set<AptUnit> alreadySelected, String caption) {
        super(AptUnit.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                // unit data
                new ColumnDescriptor.Builder(proto().info().floor()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().info().number()).filterAlwaysShown(true).build(),

                new ColumnDescriptor.Builder(proto().info().area()).build(),
                new ColumnDescriptor.Builder(proto().info()._bedrooms()).filterAlwaysShown(true).build(),
                new ColumnDescriptor.Builder(proto().info()._bathrooms()).filterAlwaysShown(true).build(),

                new ColumnDescriptor.Builder(proto().floorplan().name()).columnTitle(i18n.tr("Floorplan")).build(),
                new ColumnDescriptor.Builder(proto().floorplan().marketingName()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().availability().availableForRent()).filterAlwaysShown(true).build(),
                new ColumnDescriptor.Builder(proto().financial()._marketRent()).build(),

                // building data
                new ColumnDescriptor.Builder(proto().building().propertyCode()).build(),
                new ColumnDescriptor.Builder(proto().building().complex()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().name()).columnTitle(i18n.tr("Building")).build(),
                new ColumnDescriptor.Builder(proto().building().info().type()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().shape()).visible(false).build(),

                new ColumnDescriptor.Builder(proto().building().info().address().streetNumber()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().address().streetName()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().address().city()).build(),
                new ColumnDescriptor.Builder(proto().building().info().address().province()).build(),
                new ColumnDescriptor.Builder(proto().building().info().address().country()).visible(false).build(),

                new ColumnDescriptor.Builder(proto().building().info().totalStoreys()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().residentialStoreys()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().structureType()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().structureBuildYear()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().constructionType()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().foundationType()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().floorType()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().landArea()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().waterSupply()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().centralAir()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().info().centralHeat()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().contacts().website()).visible(false).build(),

                new ColumnDescriptor.Builder(proto().building().financial().dateAcquired()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().financial().purchasePrice()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().financial().marketPrice()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().financial().lastAppraisalDate()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().financial().lastAppraisalValue()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().financial().currency()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().building().marketing().name()).visible(false).columnTitle(i18n.tr("Building Marketing Name")).build()
                ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().building().propertyCode(), false), new Sort(proto().info().number(), false));
    }

    @Override
    protected AbstractListCrudService<AptUnit> getSelectService() {
        return GWT.<AbstractListCrudService<AptUnit>> create(SelectUnitListService.class);
    }
}