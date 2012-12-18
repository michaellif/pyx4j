/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public class UnitOccupancyViewerViewImpl extends CrmViewerViewImplBase<AptUnitOccupancySegment> implements UnitOccupancyViewerView {

    public UnitOccupancyViewerViewImpl() {
        super(CrmSiteMap.Properties.UnitOccupancy.class);
        setForm(new UnitOccupancyForm(this));
    }
}