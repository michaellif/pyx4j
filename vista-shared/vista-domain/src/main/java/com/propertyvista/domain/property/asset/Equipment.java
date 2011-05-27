/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.vendor.Contract;
import com.propertyvista.domain.property.vendor.Licence;
import com.propertyvista.domain.property.vendor.Warranty;

public interface Equipment extends IEntity {

    Building belongsTo();

    @MemberColumn(name = "equipmentType")
    IPrimitive<String> type();

    IPrimitive<String> description();

    IPrimitive<String> make();

    IPrimitive<String> model();

    IPrimitive<Date> build();

    Licence licence();

    Warranty warranty();

    @Caption(name = "Maitenance Conrtact")
    Contract maitenance();

// TODO create some notes object/domain which defines list of notes with dates and creators (one user can't delete notes of the others)...
    IPrimitive<String> notes();
}
