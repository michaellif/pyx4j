/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-14
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;

public interface Charges extends IApplicationEntity {

    /*
     * Calculated on back-end:
     * 
     * "Monthly rent" taken from UnitSelection page. TODO add there rent. rent now in
     * Application
     * 
     * Other items: DUMMY data on back-end for now
     */

    @Owned
    @Caption(name = "Rent-Related Charges")
    @ReadOnly
    ChargeLineList rentCharges();

    // When changed the ChargesSharedCalculation.calculateCharges should be called on UI.
    @Owned
    @Caption(name = "Available Upgrades")
    ChargeLineList upgradeCharges();

    // TODO add Pro-Rated duration,fraction information for calulation to be posible 

    @Owned
    @Caption(name = "Pro-Rated Charges")
    ChargeLineList proRatedCharges();

    //Calculated. DUMMY data on back-end for now., Also "deposit" may be changed by ChargesSharedCalculation.calculateCharges
    @Owned
    ChargeLineList applicationCharges();

    //Calculated base on percentage and total monthly payable. ChargesSharedCalculation.calculateCharges
    @Owned
    TenantChargeList paymentSplitCharges();

}
