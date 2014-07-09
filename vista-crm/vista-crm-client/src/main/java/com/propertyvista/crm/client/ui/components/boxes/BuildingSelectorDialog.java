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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class BuildingSelectorDialog extends EntitySelectorTableVisorController<Building> {

    private final static I18n i18n = I18n.get(BuildingSelectorDialog.class);

    public BuildingSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public BuildingSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<Building> emptySet());
    }

    public BuildingSelectorDialog(IPane parentView, Set<Building> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected);
    }

    public BuildingSelectorDialog(IPane parentView, boolean isMultiselect, Set<Building> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select Buildings"));
    }

    public BuildingSelectorDialog(IPane parentView, boolean isMultiselect, Set<Building> alreadySelected, String caption) {
        super(parentView, Building.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off                    
                new MemberColumnDescriptor.Builder(proto().propertyCode(), true).build(),
                new MemberColumnDescriptor.Builder(proto().complex(), false).build(),
                new MemberColumnDescriptor.Builder(proto().externalId(), false).build(),
                new MemberColumnDescriptor.Builder(proto().portfolios(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().name(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().type(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().shape(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().streetNumber(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().streetName(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().city(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().province(), true).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().country(), false).build(),
                new MemberColumnDescriptor.Builder(proto().marketing().visibility(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().totalStoreys(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().residentialStoreys(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().structureType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().structureBuildYear(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().constructionType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().foundationType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().floorType(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().landArea(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().waterSupply(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().centralAir(), false).build(),
                new MemberColumnDescriptor.Builder(proto().info().centralHeat(), false).build(),
                new MemberColumnDescriptor.Builder(proto().contacts().website(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().dateAcquired(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().purchasePrice(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().marketPrice(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalDate(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalValue(), false).build(),
                new MemberColumnDescriptor.Builder(proto().financial().currency().name(), false).title(proto().financial().currency()).build(),
                new MemberColumnDescriptor.Builder(proto().marketing().name(), false).title(i18n.tr("Marketing Name")).build()
        ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().propertyCode(), false));
    }

    @Override
    protected AbstractListService<Building> getSelectService() {
        return GWT.<AbstractListService<Building>> create(SelectBuildingListService.class);
    }
}