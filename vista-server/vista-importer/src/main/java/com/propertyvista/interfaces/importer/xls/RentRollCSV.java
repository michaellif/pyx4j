/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.xls;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface RentRollCSV extends IEntity {

    IPrimitive<String> unit();

    IPrimitive<String> unitType();

    IPrimitive<String> unitSqFt();

    IPrimitive<String> resident();

    IPrimitive<String> name();

    IPrimitive<String> marketRent();

    IPrimitive<String> actualRent();

    IPrimitive<String> residentDeposit();

    IPrimitive<String> otherDeposit();

    IPrimitive<String> moveIn();

    IPrimitive<String> leaseExpiration();

    IPrimitive<String> moveOut();

    IPrimitive<String> balance();

}
