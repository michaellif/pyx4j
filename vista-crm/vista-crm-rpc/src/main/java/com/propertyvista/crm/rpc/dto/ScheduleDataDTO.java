/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-22
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ScheduleDataDTO extends IEntity {

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> date();

    @NotNull
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> timeFrom();

    @NotNull
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> timeTo();
}