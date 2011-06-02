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
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.vendor.Maintenance;
import com.propertyvista.domain.property.vendor.Warranty;

public interface Roof extends IEntity, Notes {

    @Owner
    @Detached
    Building belongsTo();

    @MemberColumn(name = "roofType")
    IPrimitive<String> type();

    @MemberColumn(name = "roofYear")
    @Editor(type = EditorType.yearpicker)
    IPrimitive<LogicalDate> year();

    @EmbeddedEntity
    Warranty warranty();

    @Caption(name = "Maitenance Conrtact")
    @EmbeddedEntity
    Maintenance maitenance();

    // TODO create some notes object/domain which defines list of notes with dates and creators (one user can't delete notes of the others)...
    IPrimitive<String> notes();
}