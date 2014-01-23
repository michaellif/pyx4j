/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 29, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

@Transient
public interface CoapplicantDTO extends IEntity {

    @Editor(type = EditorType.radiogroup)
    @Caption(description = "Is dependent?")
    IPrimitive<Boolean> dependent();

    @Editor(type = EditorType.radiogroup)
    @Caption(description = "Is age 18 or over?")
    IPrimitive<Boolean> matured();

    @EmbeddedEntity
    Name name();

    IPrimitive<LogicalDate> birthDate();

    @NotNull
    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @NotNull
    @Caption(description = "Relation to the Main Applicant")
    IPrimitive<PersonRelationship> relationship();

    LeaseTermTenant tenantId();
}
