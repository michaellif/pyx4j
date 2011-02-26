/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-16
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IPrimitive;

public interface TenantCharge extends Charge {

    //TODO @Detached
    // TODO Back-end to retrieve only values for ToString
    @Editor(type = EditorType.label)
    PotentialTenantInfo tenant();

    IPrimitive<Integer> percentage();

    //Calculated base on percentage and total monthly payable
}
