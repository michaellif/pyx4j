/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;

public class YardiProcessorUtils {
    public static String getPropertyId(PropertyID propertyID) {
        return propertyID.getIdentification() != null ? getPropertyId(propertyID.getIdentification()) : null;
    }

    public static String getPropertyId(Identification identification) {
        return identification != null ? (identification.getPrimaryID()) : null;
    }

    public static String getUnitId(RTCustomer customer) {
        return customer.getRTUnit() != null ? customer.getRTUnit().getUnitID() : null;
    }

}
