/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.vendor.LicencedWarrantedMaintained;

@ToStringFormat("{0} {1}")
public interface Roof extends LicencedWarrantedMaintained, Notes {

    @Owner
    @Detached
    @ReadOnly
    Building belongsTo();

    @ToString(index = 0)
    @MemberColumn(name = "roofType")
    IPrimitive<String> type();

    @ToString(index = 1)
    @MemberColumn(name = "roofYear")
    @Editor(type = EditorType.yearpicker)
    IPrimitive<LogicalDate> year();

    // TODO create some notes object/domain which defines list of notes with dates and creators (one user can't delete notes of the others)...
    IPrimitive<String> notes();
}