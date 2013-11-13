/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.tenant.IAgree;

@Transient
public interface LegalTermsDescriptorDTO extends IEntity {

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> description();

    LegalTermsContent content();

    IList<IAgree> agrees();
}
