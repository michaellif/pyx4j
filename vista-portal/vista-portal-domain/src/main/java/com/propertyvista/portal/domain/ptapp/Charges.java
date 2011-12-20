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
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.charges.ChargeLineList;

public interface Charges extends IEntity, IBoundToApplication {

    /*
     * Calculated on back-end:
     * 
     * "Monthly rent" taken from UnitSelection page. TODO add there rent. rent now in
     * Application
     * 
     * Other items: DUMMY data on back-end for now
     */

    // When changed the ChargesSharedCalculation.calculateCharges should be called on UI.
    @Owned
    ChargeLineList monthlyCharges();

    @Owned
    ChargeLineList oneTimeCharges();

    // TODO add Pro-Rated duration,fraction information for calculation to be possible
    @Owned
    ChargeLineList proratedCharges();

    //Calculated. Also "deposit" may be changed by ChargesSharedCalculation.calculateCharges
    @Owned
    @Caption(name = "Other Charges")
    ChargeLineList applicationCharges();

    //Calculated based on percentage and total monthly payable. ChargesSharedCalculation.calculateCharges
    @Owned
    @Caption(name = "Payment Charges Split")
    TenantChargeList paymentSplitCharges();

    // We need this here so that calculation library can figure out pro-rated charges
    @Transient
    IPrimitive<LogicalDate> rentStart();
}
