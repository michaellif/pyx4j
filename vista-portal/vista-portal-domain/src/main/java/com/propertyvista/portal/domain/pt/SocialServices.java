/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author antonk
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IPrimitive;

public interface SocialServices extends IAddress {

    @Caption(name = "Social Services Agency")
    IPrimitive<String> agency();

    @Caption(name = "Social Service Agent or Case Worker")
    IPrimitive<String> worker();

    @Caption(name = "Social Service Agent's or Case Worker's phone")
    IPrimitive<String> workerPhone();

    @Caption(name = "Monthly amount")
    Money monthlyAmount();

    @Caption(name = "Years receiving")
    IPrimitive<String> yearsReceiving();

    @Caption(name = "Started on")
    IPrimitive<Date> from();

    @Caption(name = "Ended on")
    IPrimitive<Date> to();

}
