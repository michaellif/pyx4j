/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.utils;

import java.math.BigDecimal;

import com.pyx4j.commons.SimpleMessageFormat;

public class MoneyFormatter implements Formatter {

    @Override
    public String format(Object object) {
        return SimpleMessageFormat.format("{0,number,#,##0.00}", (BigDecimal) object);
    }

}
