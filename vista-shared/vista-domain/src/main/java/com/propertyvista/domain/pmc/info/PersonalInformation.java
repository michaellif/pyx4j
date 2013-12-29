/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.pmc.info;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.person.Name;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface PersonalInformation extends IEntity {

    @NotNull
    @EmbeddedEntity
    Name name();

    @EmbeddedEntity
    @NotNull
    PmcAddressSimple personalAddress();

    @NotNull
    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @NotNull
    IPrimitive<LogicalDate> dateOfBirth();

    @Caption(name = "SIN / SSN")
    IPrimitive<String> sin();

    @Owned
    IList<PmcPersonalInformationDocument> documents();

}
