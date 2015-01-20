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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;

@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@AbstractEntity
@ToStringFormat("{0}: {1}, By {2}: {3}")
public interface EvictionCaseStatus extends IEntity {

    @Owner
    @JoinColumn
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed(group = { "c,1" })
    EvictionCase evictionCase();

    @JoinColumn
    @ReadOnly
    @Indexed(group = { "c,2" })
    @ToString(index = 0)
    EvictionFlowStep evictionStep();

    @ReadOnly
    @Timestamp(Update.Created)
    @ToString(index = 1)
    IPrimitive<Date> addedOn();

    @ReadOnly
    @Detached
    @ToString(index = 2)
    Employee addedBy();

    @NotNull
    @Editor(type = EditorType.textarea)
    @ToString(index = 3)
    IPrimitive<String> note();

    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<EvictionStatusRecord> statusRecords();
}
