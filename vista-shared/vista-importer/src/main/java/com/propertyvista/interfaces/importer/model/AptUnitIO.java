/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface AptUnitIO extends IEntity {

    @XmlTransient
    ImportInformation _import();

    IPrimitive<String> number();

    IPrimitive<Double> area();

    IPrimitive<Integer> floor();

    IPrimitive<AreaMeasurementUnit> areaUnits();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> unitRent();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> marketRent();

    IPrimitive<LogicalDate> availableForRent();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<AptUnitOccupancyIO> AptUnitOccupancySegment();

}
