/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-22
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.client.ui.gadgets.GadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public abstract class VacancyGadgetBase extends GadgetBase implements IBuildingGadget {

    protected FilterDataDemoAdapter filter;

    public VacancyGadgetBase(GadgetMetadata gmd) {
        super(gmd);
    }

    @Override
    public void setBuilding(Key id) {
        // Just fake function.
        List<Key> s = new ArrayList<Key>();
        s.add(id);
        setBuildings(s);
    }

    @Override
    public void setBuildings(List<Key> ids) {
        // Also fake function! not to be used
        FilterData filter = new FilterData();
        setFiltering(filter);
    }

    @Override
    public void setFiltering(FilterData filter) {
        setFilteringCriteria(new FilterDataDemoAdapter(filter));
    }

    abstract protected void setFilteringCriteria(FilterDataDemoAdapter filterDataDemoAdapter);
}
