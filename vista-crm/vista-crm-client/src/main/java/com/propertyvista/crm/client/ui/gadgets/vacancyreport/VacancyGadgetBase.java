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

import com.propertyvista.crm.client.ui.gadgets.GadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public abstract class VacancyGadgetBase extends GadgetBase implements IBuildingGadget {

    protected FilterDataDemoAdapter filter;

    public VacancyGadgetBase(GadgetMetadata gmd) {
        super(gmd);
    }

    @Override
    public void setFiltering(FilterData filter) {
        setFilteringCriteria(new FilterDataDemoAdapter(filter));
    }

    abstract protected void setFilteringCriteria(FilterDataDemoAdapter filterDataDemoAdapter);
}
