/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-19
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface AptUnitFinancial extends IEntity {

    /**
     * TODO Should be updated when Lease for this Unit is saved.
     */
    @Format("#0.00")
    IPrimitive<Double> _unitRent();

    /**
     * TODO Should be updated when ServiceItem for this Unit is saved.
     */
    @Format("#0.00")
    IPrimitive<Double> _marketRent();
}
