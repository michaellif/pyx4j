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
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.crm.rpc.services.selections.SelectBuildingCrudService;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class BuildingSelectorDialog extends EntitySelectorDialog<Building> {

    private final static I18n i18n = I18n.get(BuildingSelectorDialog.class);

    public BuildingSelectorDialog(List<Building> alreadySelected) {
        super(Building.class, true, alreadySelected, i18n.tr("Select Buildings"));
        setSize("700px", "400px");
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off                    
                new MemberColumnDescriptor.Builder(proto().propertyCode()).build(),
                new MemberColumnDescriptor.Builder(proto().complex()).build(),
                new MemberColumnDescriptor.Builder(proto().propertyManager()).build(),
                new MemberColumnDescriptor.Builder(proto().marketing().name()).build(),
                new MemberColumnDescriptor.Builder(proto().info().name()).build(),
                new MemberColumnDescriptor.Builder(proto().info().type()).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().city()).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().province()).build(),
                new MemberColumnDescriptor.Builder(proto().info().address().country()).build()
        ); //@formatter:on
    }

    @Override
    protected AbstractListService<Building> getSelectService() {
        return GWT.<AbstractListService<Building>> create(SelectBuildingCrudService.class);
    }
}