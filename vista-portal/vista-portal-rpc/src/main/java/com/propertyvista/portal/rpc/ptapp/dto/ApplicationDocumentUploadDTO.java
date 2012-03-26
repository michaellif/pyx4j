/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

@Transient
public interface ApplicationDocumentUploadDTO extends IEntity {

    IPrimitive<Key> tenantInLeaseId();

    @NotNull
    IdentificationDocumentType identificationDocument();

    @NotNull
    IPrimitive<String> details();

}
