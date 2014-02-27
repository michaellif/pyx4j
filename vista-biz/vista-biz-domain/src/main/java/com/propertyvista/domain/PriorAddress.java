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
package com.propertyvista.domain;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressStructured;

@EmbeddedEntity
@Table(name = "pt_address")
public interface PriorAddress extends AddressStructured {

    @I18n
    public enum OwnedRented {

        owned,

        rented;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<LogicalDate> moveInDate();

    IPrimitive<LogicalDate> moveOutDate();

    @Caption(name = "Monthly Payment")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> payment();

    @NotNull
    @Caption(name = "Owned/Rented")
    @Editor(type = EditorType.radiogroup)
    IPrimitive<OwnedRented> rented();

    @NotNull
    @Caption(name = "Management Company")
    IPrimitive<String> propertyCompany();

    @NotNull
    IPrimitive<String> managerName();

    @NotNull
    @Editor(type = EditorType.phone)
    IPrimitive<String> managerPhone();

    @Editor(type = EditorType.email)
    IPrimitive<String> managerEmail();
}
