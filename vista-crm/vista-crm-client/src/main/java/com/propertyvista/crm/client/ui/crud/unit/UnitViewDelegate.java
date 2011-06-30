/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;

public class UnitViewDelegate implements UnitView {

    private final IListerView<AptUnitItem> detailsLister;

    private final IListerView<AptUnitOccupancy> OccupanciesLister;

    public UnitViewDelegate(boolean readOnly) {
        detailsLister = new ListerInternalViewImplBase<AptUnitItem>(new DetailLister());
        OccupanciesLister = new ListerInternalViewImplBase<AptUnitOccupancy>(new UnitOccupancyLister());
    }

    @Override
    public IListerView<AptUnitItem> getDetailsListerView() {
        return detailsLister;
    }

    @Override
    public IListerView<AptUnitOccupancy> getOccupanciesListerView() {
        return OccupanciesLister;
    }
}
