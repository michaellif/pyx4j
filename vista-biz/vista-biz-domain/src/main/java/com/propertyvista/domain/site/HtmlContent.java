/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-01-29
 * @author stanp
 */
package com.propertyvista.domain.site;

import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface HtmlContent extends IEntity {

    @NotNull
    AvailableLocale locale();

    @Editor(type = Editor.EditorType.richtextarea)
    @Length(48000)
    IPrimitive<String> html();

    @Timestamp
    @Editor(type = Editor.EditorType.label)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> updated();
}
