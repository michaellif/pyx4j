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
package com.propertyvista.domain.tenant.lease.extradata;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.CountryReferenceAdapter;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;

@Table(name = "pt_vehicle")
@DiscriminatorValue("Vehicle_ChargeItemExtraData")
public interface Vehicle extends BillableItemExtraData {

    @NotNull
    @BusinessEqualValue
    IPrimitive<String> plateNumber();

    IPrimitive<String> make();

    IPrimitive<String> model();

    IPrimitive<String> color();

    @NotNull
    @Format("yyyy")
    @Editor(type = EditorType.yearpicker)
    @MemberColumn(name = "year_made")
    IPrimitive<LogicalDate> year();

    @NotNull
    @Caption(name = "Province/State")
    @Editor(type = EditorType.combo)
    Province province();

    @NotNull
    @Editor(type = EditorType.combo)
    @Reference(adapter = CountryReferenceAdapter.class)
    Country country();
}
