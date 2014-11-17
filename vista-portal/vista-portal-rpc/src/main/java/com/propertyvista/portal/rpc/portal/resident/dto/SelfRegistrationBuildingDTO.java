/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.property.asset.building.Building;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@ToStringFormat("{0,choice,null#|!null#{0} - }{1}, {2}, {3}")
public interface SelfRegistrationBuildingDTO extends IEntity {

    Building buildingKey();

    @ToString(index = 0)
    IPrimitive<String> propertyCode();

    @ToString(index = 1)
    IPrimitive<String> streetAddress();

    @ToString(index = 2)
    IPrimitive<String> municipality();

    @ToString(index = 3)
    IPrimitive<String> region();

    @Editor(type = EditorType.phone)
    IPrimitive<String> supportPhone();
}
