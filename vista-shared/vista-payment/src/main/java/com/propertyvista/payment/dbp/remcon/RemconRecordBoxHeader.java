/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp.remcon;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.propertyvista.payment.dbp.remcon.RemconField.RemconFieldType;

public class RemconRecordBoxHeader implements RemconRecord {

    @Override
    public char recordType() {
        return '1';
    };

    //Numeric; Static box number assigned to customer by the Bank
    @RemconField(4)
    public String boxNumber;

    @RemconField(value = 145, type = RemconFieldType.Filler)
    public String filler;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
