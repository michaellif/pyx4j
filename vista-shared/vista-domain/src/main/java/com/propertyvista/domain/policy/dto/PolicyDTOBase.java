/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.PolicyNode;

public abstract interface PolicyDTOBase extends IEntity {

    PolicyNode node();

    @Caption(name = "Scope")
    IPrimitive<String> nodeType();

    @Caption(name = "Applied To")
    IPrimitive<String> nodeRepresentation();
}
