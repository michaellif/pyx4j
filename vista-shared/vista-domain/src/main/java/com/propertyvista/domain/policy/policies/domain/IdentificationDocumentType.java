/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
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

@ToStringFormat("{0}")
public interface IdentificationDocumentType extends IEntity {

    @I18n
    @XmlType(name = "IdentificationDocumentType")
    public enum Type {

        passport,

        canadianSIN,

        citizenship,

        immigration,

        license,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
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
    @Caption(name = "ID Name")
    IPrimitive<String> name();

    IPrimitive<Boolean> required();

    @NotNull
    @ToString(index = 0)
    @Caption(name = "ID Type")
    @MemberColumn(name = "idType")
    IPrimitive<Type> type();
}
