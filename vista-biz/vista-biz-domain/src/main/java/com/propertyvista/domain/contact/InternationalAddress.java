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
package com.propertyvista.domain.contact;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.ref.ISOCountry;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@ToStringFormat("{0,choice,null#|!null#{0} }{1}{2,choice,null#|!null#, Unit {2}}, {3}, {4} {5}, {6}")
public interface InternationalAddress extends IEntity {

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> streetNumber();

    @NotNull
    @ToString(index = 1)
    IPrimitive<String> streetName();

    @ToString(index = 2)
    IPrimitive<String> suiteNumber();

    @NotNull
    @ToString(index = 3)
    IPrimitive<String> city();

    @NotNull
    @ToString(index = 4)
    @Caption(name = "Province/State/Region")
    IPrimitive<String> province();

    @NotNull
    @ToString(index = 5)
    IPrimitive<ISOCountry> country();

    @NotNull
    @ToString(index = 6)
    IPrimitive<String> postalCode();
}
