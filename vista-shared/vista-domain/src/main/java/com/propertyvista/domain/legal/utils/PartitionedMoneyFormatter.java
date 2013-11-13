/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.utils;

import java.math.BigDecimal;

import com.pyx4j.commons.SimpleMessageFormat;

/**
 * 
 * formats money to be ready for partitioning into sub fields ie. value <code>1000</code> will be formatted into <code>100000</code>. basically appends two
 * digits representing decimal signs to the right.
 * 
 */
public class PartitionedMoneyFormatter implements PdfFormFieldFormatter {

    @Override
    public String format(Object object) {
        BigDecimal value = (BigDecimal) object;
        return SimpleMessageFormat.format("{0,number,#0.00}", value).replace(".", "");
    }

}
