/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2015
 * @author VladL
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@ToStringFormat("{0}")
public interface TermsPolicyItem extends IEntity {

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> caption();

    @Length(300000)
    @Editor(type = Editor.EditorType.richtextarea)
    IPrimitive<String> content();

    IPrimitive<Boolean> enabled();
}
