/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.mock.updater;

import com.yardi.entity.guestcard40.RentableItemType;

public class RentableItemTypeUpdater extends Updater<RentableItemType, RentableItemTypeUpdater> {

    private final String code;

    private final String propertyID;

    public enum Name implements com.propertyvista.yardi.mock.updater.Name {

        Description,

        Code,

        ChargeCode,

        Rent,

        Property,

        Item;
    }

    public RentableItemTypeUpdater(String propertyID, String code) {
        assert propertyID != null : "'propertyID' should not be null";
        assert code != null : "'code' should not be null";

        this.propertyID = propertyID;
        this.code = code;

        set(Name.Code, code);
        set(Name.Property, propertyID);
    }

    public String getCode() {
        return code;
    }

    public String getPropertyID() {
        return propertyID;
    }
}
