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
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.CountryReferenceAdapter;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.ref.ProvinceReferenceAdapter;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface IAddress extends IEntity {

    @Caption(name = "Address 1")
    @NotNull
    IPrimitive<String> street1();

    @Caption(name = "Address 2")
    IPrimitive<String> street2();

    @NotNull
    IPrimitive<String> city();

    @Caption(name = "Province/State")
    @NotNull
    @Editor(type = EditorType.combo)
    @Reference(adapter = ProvinceReferenceAdapter.class)
    Province province();

    @Editor(type = EditorType.suggest)
    @NotNull
    @Reference(adapter = CountryReferenceAdapter.class)
    Country country();

    @NotNull
    IPrimitive<String> postalCode();
}
