/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.parking;

import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.dto.ParkingDTO;

public class ParkingViewerViewImpl extends CrmViewerViewImplBase<ParkingDTO> implements ParkingViewerView {

    private final ILister<ParkingSpot> spotLister;

    public ParkingViewerViewImpl() {
        spotLister = new ListerInternalViewImplBase<ParkingSpot>(new ParkingSpotLister());

        // set main form here:
        setForm(new ParkingForm(this));
    }

    @Override
    public ILister<ParkingSpot> getSpotView() {
        return spotLister;
    }
}