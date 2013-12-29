/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;

public interface EmailTemplate extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    @Indexed(uniqueConstraint = true, group = { "UPT,1" })
    EmailTemplatesPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInPolicy();

    @NotNull
    IPrimitive<String> subject();

    IPrimitive<Boolean> useHeader();

    IPrimitive<Boolean> useFooter();

    @NotNull
    @Owned
    @Length(20845)
    @Editor(type = Editor.EditorType.richtextarea)
    IPrimitive<String> content();

    @MemberColumn(name = "templateType")
    @ToString
    @Indexed(uniqueConstraint = true, group = { "UPT,2" })
    IPrimitive<EmailTemplateType> type();
}
