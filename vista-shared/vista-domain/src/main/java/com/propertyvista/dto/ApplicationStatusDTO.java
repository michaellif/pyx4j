/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Name;

@Transient
public interface ApplicationStatusDTO extends IEntity {

    /**
     * Tenant/Guarantor
     */
    Name person();

    /**
     * Primary Tenant, Co-signer or Guarantor
     */
    IPrimitive<String> type();

    /**
     * Completed steps/total steps in %
     */
    IPrimitive<Double> progress();

    IPrimitive<String> description();
}
