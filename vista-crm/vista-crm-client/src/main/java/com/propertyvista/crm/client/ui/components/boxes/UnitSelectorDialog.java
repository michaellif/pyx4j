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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.selections.SelectUnitListService;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public abstract class UnitSelectorDialog extends EntitySelectorTableVisorController<AptUnit> {

    private static final I18n i18n = I18n.get(UnitSelectorDialog.class);

    public UnitSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public UnitSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<AptUnit> emptyList());
    }

    public UnitSelectorDialog(IPane parentView, List<AptUnit> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected, i18n.tr("Select Unit"));
    }

    public UnitSelectorDialog(IPane parentView, boolean isMultiselect, List<AptUnit> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select Unit"));
    }

    public UnitSelectorDialog(IPane parentView, boolean isMultiselect, List<AptUnit> alreadySelected, String caption) {
        super(parentView, AptUnit.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                // unit data
                new MemberColumnDescriptor.Builder(proto().info().floor(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().number(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().area()).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bedrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bathrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().name()).title(i18n.tr("Floorplan")).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().marketingName(), false).build(),
                new MemberColumnDescriptor.Builder(proto().availability().availableForRent()).build(),

                // building data                
                new MemberColumnDescriptor.Builder(proto().building().propertyCode(), true).build(),
                new MemberColumnDescriptor.Builder(proto().building().complex(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().propertyManager(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().name(), true).title(i18n.tr("Building")).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().type(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().shape(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().streetNumber(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().streetNumberSuffix(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().streetName(), true).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().streetType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().streetDirection(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().city(), true).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().province(), true).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().address().country(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().totalStoreys(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().residentialStoreys(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().structureType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().structureBuildYear(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().constructionType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().foundationType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().floorType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().landArea(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().waterSupply(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().centralAir(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().info().centralHeat(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().contacts().website(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().financial().dateAcquired(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().financial().purchasePrice(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().financial().marketPrice(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().financial().lastAppraisalDate(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().financial().lastAppraisalValue(), false).build(),
                new MemberColumnDescriptor.Builder(proto().building().financial().currency().name(), false).title(proto().building().financial().currency())
                        .build(),
                new MemberColumnDescriptor.Builder(proto().building().marketing().name(), false).title(i18n.tr("Building Marketing Name")).build()
                ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().building().propertyCode(), false), new Sort(proto().info().number(), false));
    }

    @Override
    protected AbstractListService<AptUnit> getSelectService() {
        return GWT.<AbstractListService<AptUnit>> create(SelectUnitListService.class);
    }
}