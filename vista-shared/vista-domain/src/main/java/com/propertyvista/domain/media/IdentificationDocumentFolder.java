/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.media;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.tenant.CustomerScreening.CustomerScreeningV;

@DiscriminatorValue("IdentificationDocument")
public interface IdentificationDocumentFolder extends ApplicationDocumentFolder<CustomerScreeningV> {

    @NotNull
    @ToString(index = 0)
    IdentificationDocumentType idType();

    @NotNull
    @ToString(index = 1)
    IPrimitive<String> idNumber();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> notes();
}
