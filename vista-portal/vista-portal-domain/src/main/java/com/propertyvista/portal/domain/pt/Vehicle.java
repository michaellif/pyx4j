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
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.portal.domain.ref.Country;
import com.propertyvista.portal.domain.ref.CountryReferenceAdapter;
import com.propertyvista.portal.domain.ref.Province;

@Table(name = "pt_vehicle")
public interface Vehicle extends IEntity {

    @BusinessEqualValue
    IPrimitive<String> plateNumber();

    @Format("yyyy")
    @Editor(type = EditorType.yearpicker)
    @MemberColumn(name = "year_made")
    IPrimitive<java.sql.Date> year();

    IPrimitive<String> make();

    IPrimitive<String> model();

    @Caption(name = "Province/State")
    @Editor(type = EditorType.combo)
    Province province();

    @Editor(type = EditorType.combo)
    @NotNull
    @Reference(adapter = CountryReferenceAdapter.class)
    Country country();
}
