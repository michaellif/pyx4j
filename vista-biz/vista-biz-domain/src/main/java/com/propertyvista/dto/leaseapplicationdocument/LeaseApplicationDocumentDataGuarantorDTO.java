/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 25, 2014
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.dto.leaseapplicationdocument;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.PersonRelationship;

@Transient
public interface LeaseApplicationDocumentDataGuarantorDTO extends IEntity {

    IPrimitive<String> firstName();

    IPrimitive<String> lastName();

    @NotNull
    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @NotNull
    @Caption(description = "Relation to the Applicant")
    IPrimitive<PersonRelationship> relationship();

}
