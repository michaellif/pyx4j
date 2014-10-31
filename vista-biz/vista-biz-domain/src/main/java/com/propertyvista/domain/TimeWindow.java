/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 30, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain;

import java.sql.Time;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@EmbeddedEntity
@ToStringFormat("{0}-{1}")
public interface TimeWindow extends IEntity {

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    @NotNull
    @Caption(name = "From")
    @ToString(index = 0)
    IPrimitive<Time> timeFrom();

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    @NotNull
    @Caption(name = "To")
    @ToString(index = 1)
    IPrimitive<Time> timeTo();
}
