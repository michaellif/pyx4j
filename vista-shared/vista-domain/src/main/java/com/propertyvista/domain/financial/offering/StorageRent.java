/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.LockerArea;

public interface StorageRent extends Feature {

    LockerArea lockerArea();

    @Format("#0.00")
    IPrimitive<Double> largeRent();

    @Format("#0.00")
    IPrimitive<Double> regularRent();

    @Format("#0.00")
    IPrimitive<Double> smallRent();

    @Format("#0.00")
    IPrimitive<Double> deposit();
}
