/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface AddressIO extends IEntity {

    IPrimitive<String> unitNumber();

    @NotNull
    IPrimitive<String> streetNumber();

    IPrimitive<String> streetNumberSuffix();

    @NotNull
    IPrimitive<String> streetName();

    @NotNull
    IPrimitive<StreetType> streetType();

    IPrimitive<StreetDirection> streetDirection();

    @NotNull
    IPrimitive<String> city();

    IPrimitive<String> county();

    IPrimitive<String> provinceCode();

    @NotNull
    IPrimitive<String> countryName();

    @NotNull
    IPrimitive<String> postalCode();
}
