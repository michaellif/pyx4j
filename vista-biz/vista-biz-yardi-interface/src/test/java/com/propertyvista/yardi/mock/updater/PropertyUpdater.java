/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2013
 * @author michaellif
 */
package com.propertyvista.yardi.mock.updater;

import com.yardi.entity.resident.ChargeDetail;

public class PropertyUpdater extends Updater<ChargeDetail, PropertyUpdater> {

    private final String propertyID;

    public enum ADDRESS implements com.propertyvista.yardi.mock.updater.Name {
        Address1, PostalCode, Country, State;
    }

    public enum MockFeatures implements com.propertyvista.yardi.mock.updater.Name {

        BlockAccess, BlockBatchOpening, BlockBatchPost, BlockTransactionPostLeases;

    }

    public PropertyUpdater(String propertyID) {
        assert propertyID != null : "Property with id " + propertyID + " is not found.";
        this.propertyID = propertyID;
    }

    public String getPropertyID() {
        return propertyID;
    }

}
