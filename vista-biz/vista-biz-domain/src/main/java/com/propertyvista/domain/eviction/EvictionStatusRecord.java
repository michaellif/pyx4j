/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2014
 * @author stanp
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.domain.eviction;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;

@ToStringFormat("{0}: {1}")
public interface EvictionStatusRecord extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @ReadOnly
    @Detached
    EvictionCaseStatus evictionStatus();

    @Editor(type = EditorType.textarea)
    @ToString(index = 1)
    IPrimitive<String> note();

    @ReadOnly
    @Timestamp(Update.Created)
    @ToString(index = 0)
    IPrimitive<Date> addedOn();

    @ReadOnly
    @Detached
    Employee addedBy();

    @Detached
    @OrderBy(PrimaryKey.class)
    IList<EvictionDocument> attachments();
}
