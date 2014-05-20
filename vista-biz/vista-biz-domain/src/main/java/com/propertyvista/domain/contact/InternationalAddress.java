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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.CountryReferenceAdapter;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface InternationalAddress extends IEntity {

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> addressLine1();

    @ToString(index = 1)
    IPrimitive<String> addressLine2();

    @NotNull
    @ToString(index = 2)
    IPrimitive<String> city();

    @NotNull
    @ToString(index = 3)
    @Caption(name = "Province/State/Region")
    IPrimitive<String> province();

    @NotNull
    @ToString(index = 4)
    @Editor(type = EditorType.combo)
    @Reference(adapter = CountryReferenceAdapter.class)
    Country country();

    @NotNull
    @ToString(index = 5)
    IPrimitive<String> postalCode();
}
