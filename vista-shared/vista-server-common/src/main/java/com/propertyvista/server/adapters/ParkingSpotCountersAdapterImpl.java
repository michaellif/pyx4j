/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.ParkingSpot.Type;
import com.propertyvista.shared.adapters.ParkingSpotCountersAdapter;

public class ParkingSpotCountersAdapterImpl implements ParkingSpotCountersAdapter {

    @Override
    public boolean allowModifications(ParkingSpot entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        // comparing enums so != is OK
        Parking parking = Persistence.service().retrieve(Parking.class, entity.parking().getPrimaryKey());
        if (valueNew != null && valueOrig != valueNew) {
            if (valueOrig != null) {
                ParkingSpot.Type typeOrig = (Type) valueOrig;
                Integer i = 0;
                switch (typeOrig) {
                case disabled:
                    i = parking.disabledSpaces().getValue();
                    parking.disabledSpaces().setValue(i - 1);
                    break;
                case wide:
                    i = parking.wideSpaces().getValue();
                    parking.wideSpaces().setValue(i - 1);
                    break;
                case narrow:
                    i = parking.narrowSpaces().getValue();
                    parking.narrowSpaces().setValue(i - 1);
                    break;
                case regular:
                    i = parking.regularSpaces().getValue();
                    parking.regularSpaces().setValue(i - 1);
                    break;
                }
            }

            if (valueNew != null) {
                ParkingSpot.Type typeNew = (Type) valueNew;
                Integer i = 0;
                switch (typeNew) {
                case disabled:
                    i = parking.disabledSpaces().getValue();
                    parking.disabledSpaces().setValue(i + 1);
                    break;
                case wide:
                    i = parking.wideSpaces().getValue();
                    parking.wideSpaces().setValue(i + 1);
                    break;
                case narrow:
                    i = parking.narrowSpaces().getValue();
                    parking.narrowSpaces().setValue(i + 1);
                    break;
                case regular:
                    i = parking.regularSpaces().getValue();
                    parking.regularSpaces().setValue(i + 1);
                    break;
                }
                if (valueOrig == null) {
                    i = parking.totalSpaces().getValue();
                    parking.totalSpaces().setValue(i + 1);
                }
            }
//        } else if (valueOrig == null) {

        }
        Persistence.service().persist(parking);
        return true;
    }
}
