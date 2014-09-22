/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.activity.ListerController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;

import com.propertyvista.domain.property.asset.building.Building;

public class BuildingListerController extends ListerController<Building> {

    public BuildingListerController(ILister<Building> view, AbstractListCrudService<Building> service) {
        super(Building.class, view, service);
        ((EntityLister<Building>) view).setDataTableModel(defineColumnDescriptors());
        this.populate();
    }

    protected Building proto() {
        return EntityFactory.getEntityPrototype(Building.class);
    }

    protected ColumnDescriptor[] defineColumnDescriptors() {
        return new ColumnDescriptor[] {//@formatter:off
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
                new MemberColumnDescriptor.Builder(proto().marketing().name(), false).title("Marketing Name").build()
        }; //@formatter:on
    }

}