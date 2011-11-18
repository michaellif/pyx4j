/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.person;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;

public interface Person extends IEntity {

    @I18n
    public enum Sex {

        Male, Female;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 1)
    @BusinessEqualValue
    @EmbeddedEntity
    Name name();

    @NotNull
    @ToString(index = 1)
    IPrimitive<Sex> sex();

    @EmbeddedEntity
    Phone homePhone();

    @EmbeddedEntity
    Phone mobilePhone();

    @EmbeddedEntity
    Phone workPhone();

    @Caption(name = "Email Address")
    @EmbeddedEntity
    Email email();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> birthDate();
}