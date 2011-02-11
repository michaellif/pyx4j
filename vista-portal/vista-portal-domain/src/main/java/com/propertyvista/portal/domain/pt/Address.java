/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author antonk
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.Phone;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Address extends IEntity {
    IPrimitive<String> street1();

    IPrimitive<String> street2();

    IPrimitive<String> city();

    IPrimitive<String> province();

    IPrimitive<String> postalCode();

    IPrimitive<Date> start();

    IPrimitive<Date> end();

    Money payment();

    Phone phone();

    IPrimitive<Boolean> rented();

    IPrimitive<Boolean> canadian();

    IPrimitive<String> managerName();
}
