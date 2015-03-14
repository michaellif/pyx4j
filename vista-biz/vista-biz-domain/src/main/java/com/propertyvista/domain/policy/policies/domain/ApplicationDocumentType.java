/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2014
 * @author VladL
 */
package com.propertyvista.domain.policy.policies.domain;

import java.util.EnumSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
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
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;

@AbstractEntity
@ToStringFormat("{0}, {1}")
public interface ApplicationDocumentType extends IEntity {

    @I18n
    @XmlType(name = "ApplicationDocumentImportance")
    public enum Importance {

        Required, Preferred, Optional;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static Set<Importance> activate() {
            return EnumSet.of(Importance.Preferred, Importance.Required);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    ApplicationDocumentationPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInPolicy();

    @ToString(index = 0)
    @Caption(name = "Document Name")
    IPrimitive<String> name();

    @NotNull
    @ToString(index = 1)
    @MemberColumn(notNull = true)
    IPrimitive<Importance> importance();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> notes();
}
