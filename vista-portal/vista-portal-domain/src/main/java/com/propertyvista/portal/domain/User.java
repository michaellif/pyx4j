/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface User extends IEntity {

    @NotNull
    @ToString
    //@Indexed(keywordLenght = 2, indexPrimaryValue = false, adapters = KeywordsIndexAdapter.class)
    @Indexed
    IPrimitive<String> name();

    @Editor(type = EditorType.email)
    @NotNull
    //@Indexed(keywordLenght = 2, adapters = KeywordsIndexAdapter.class)
    @Indexed
    IPrimitive<String> email();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Timestamp
    IPrimitive<Date> updated();

}
