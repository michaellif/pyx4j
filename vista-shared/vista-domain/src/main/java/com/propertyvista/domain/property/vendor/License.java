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
package com.propertyvista.domain.property.vendor;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface License extends IEntity {

    @ToString(index = 1)
    @MemberColumn(name = "licenceNumber")
    IPrimitive<String> number();

    @ToString(index = 2)
    @Caption(name = "License Expiration")
    @Editor(type = EditorType.yearpicker)
    @Format("yyyy")
    IPrimitive<LogicalDate> expiration();

    @Caption(name = "License Renewal")
    @Editor(type = EditorType.yearpicker)
    @Format("yyyy")
    IPrimitive<LogicalDate> renewal();
}
