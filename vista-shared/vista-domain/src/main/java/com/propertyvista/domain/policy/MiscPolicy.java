/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.domain.policy;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IPrimitive;

public interface MiscPolicy extends UnitPolicy {

    @Caption(description = "Some landlords force all occupants over 18 to be on LEASE and therefore anyone OVER 18 MUST be Applicant//Co-Applicant and cannot be Dependent")
    IPrimitive<Boolean> occupantsOver18areApplicants();

    @Caption(description = "Initial payment includes one or two-month deposit")
    IPrimitive<Boolean> oneMonthDeposit();

}
