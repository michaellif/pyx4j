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
import com.pyx4j.site.client.backoffice.activity.ListerController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;

import com.propertyvista.domain.property.asset.building.Building;

public class SelectorDialogBuildingListerController extends ListerController<Building> {

    public SelectorDialogBuildingListerController(ILister<Building> view, AbstractListCrudService<Building> service) {
        super(Building.class, view, service);
        this.populate();
        ((SelectorDialogBuildingLister) view).setRowsSelected();
    }

    protected Building proto() {
        return EntityFactory.getEntityPrototype(Building.class);
    }

}