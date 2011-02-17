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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
public interface PotentialTenant extends IApplicationEntity {

    public enum Relationship {
        applicant, spouse, sun, daughter, other
    }

    @Caption(name = "First")
    IPrimitive<String> firstName();

    @Caption(name = "Middle")
    IPrimitive<String> middleName();

    @Caption(name = "Last")
    IPrimitive<String> lastName();

    @Caption(name = "Birth Date")
    IPrimitive<Date> birthDate();

    @Caption(name = "Home")
    IPrimitive<String> homePhone();

    @Caption(name = "Mobile")
    IPrimitive<String> mobilePhone();

    @Caption(name = "Email")
    IPrimitive<String> email();

    IPrimitive<Relationship> relationship();

    IPrimitive<Float> payment();

    IPrimitive<Boolean> dependant();

    IPrimitive<Boolean> takeOwnership();

}
