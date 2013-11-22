/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.n4;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.legal.ltbcommon.LtbAgentContactInfo;
import com.propertyvista.domain.legal.ltbcommon.LtbOwedRent;
import com.propertyvista.domain.legal.ltbcommon.LtbRentalUnitAddress;

@Transient
public interface N4FormFieldsData extends IEntity {

    /** Tenant names and address */
    IPrimitive<String> to();

    /** Landlord's name */
    IPrimitive<String> from();

    LtbRentalUnitAddress rentalUnitAddress();

    IPrimitive<LogicalDate> terminationDate();

    IPrimitive<BigDecimal> totalRentOwed();

    LtbOwedRent owedRent();

    N4Signature signature();

    LtbAgentContactInfo landlordsContactInfo();

}
