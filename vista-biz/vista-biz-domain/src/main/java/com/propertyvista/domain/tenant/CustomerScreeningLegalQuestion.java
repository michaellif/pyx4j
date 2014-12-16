/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@ToStringFormat("{0}{1,choice,null#|!null# - {1,choice,0#No|1#Yes}}")
public interface CustomerScreeningLegalQuestion extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    CustomerScreening.CustomerScreeningV owner();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();

    // -------------------------------------------

    @ToString(index = 0)
    @ReadOnly
    @Editor(type = EditorType.label)
    IPrimitive<String> question();

    @ToString(index = 1)
    @Editor(type = EditorType.radiogroup)
    IPrimitive<Boolean> answer();

    IPrimitive<String> notes();
}
