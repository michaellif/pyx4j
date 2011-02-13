/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface PotentialTenant extends IEntity {

    IPrimitive<String> firstName();

    IPrimitive<String> middleName();

    IPrimitive<String> lastName();

    IPrimitive<Date> birthDate();

    IPrimitive<String> homePhone();

    IPrimitive<String> mobilePhone();

    IPrimitive<String> email();

    IPrimitive<String> relationship();

    Money payment();

    IPrimitive<Boolean> dependant();

    IPrimitive<Boolean> takeOwnership();

}
