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

import java.io.Serializable;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
@ToStringFormat("{0} {1} {2}")
public interface PotentialTenant extends IPerson, IApplicationEntity {

    public enum Relationship implements Serializable {
        Applicant, CoApplicant, Spouse, Son, Daughter, Other
    }

    @ToString(index = 0)
    @NotNull
    IPrimitive<Relationship> relationship();

    IPrimitive<Double> payment();

    IPrimitive<Boolean> dependant();

    IPrimitive<Boolean> takeOwnership();

}
