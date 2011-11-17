/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.person;

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public interface Name extends IEntity {

    @I18n
    public enum Title {

        Mr, Mrs, Ms, Miss, Dr;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 1)
    IPrimitive<String> namePrefix();

    @NotNull
    @ToString(index = 2)
    @BusinessEqualValue
    IPrimitive<String> firstName();

    @ToString(index = 3)
    IPrimitive<String> middleName();

    @NotNull
    @ToString(index = 4)
    @BusinessEqualValue
    IPrimitive<String> lastName();

    IPrimitive<String> maidenName();

    @ToString(index = 5)
    IPrimitive<String> nameSuffix();
}
