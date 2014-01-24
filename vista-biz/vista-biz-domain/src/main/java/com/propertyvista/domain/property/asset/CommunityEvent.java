/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

public interface CommunityEvent extends IEntity {
    IPrimitive<String> caption();

    IPrimitive<String> location();

    @NotNull
    IPrimitive<LogicalDate> date();

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    @NotNull
    IPrimitive<Time> time();

    @NotNull
    @Length(2048)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Owner
    @Detached
    @NotNull
    @MemberColumn(notNull = true)
    @JoinColumn
    Building building();
}
