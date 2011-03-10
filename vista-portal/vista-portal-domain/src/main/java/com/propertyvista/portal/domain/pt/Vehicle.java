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

import com.propertyvista.portal.domain.ref.Province;

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Table(name = "pt_vehicle")
public interface Vehicle extends IEntity {

    @BusinessEqualValue
    IPrimitive<String> plateNumber();

    IPrimitive<Integer> year();

    IPrimitive<String> make();

    IPrimitive<String> model();

    @Editor(type = EditorType.combo)
    Province province();
}
