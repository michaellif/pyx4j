/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Inheritance
@AbstractEntity
public interface CommunicationEndpoint extends IEntity {
    @NotNull
    @ToString
    @Indexed
    IPrimitive<String> name();

    @Editor(type = EditorType.email)
    @Length(64)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    IPrimitive<String> email();
}
