/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.n4;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.legal.LegalLetter;

@DiscriminatorValue("N4LegalLetter")
public interface N4LegalLetter extends LegalLetter {

    IPrimitive<BigDecimal> amountOwed();

}
