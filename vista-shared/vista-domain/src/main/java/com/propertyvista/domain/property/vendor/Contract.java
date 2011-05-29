/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.vendor;

import java.sql.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.Document;

public interface Contract extends IEntity {

    IPrimitive<String> identification();

    Vendor contractor();

    @MemberColumn(name = "vendorCost")
    IPrimitive<Double> cost();

    @Caption(name = "Start Date")
    @MemberColumn(name = "contractStart")
    IPrimitive<Date> start();

    @Caption(name = "Expirty Date")
    @MemberColumn(name = "contractEnd")
    IPrimitive<Date> end();

    Document document();
}
