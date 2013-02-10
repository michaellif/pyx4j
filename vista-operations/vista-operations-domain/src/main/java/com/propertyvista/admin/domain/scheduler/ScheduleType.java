/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.scheduler;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum ScheduleType implements Serializable {

    Manual,

    Once,

    Daily,

    Weekly,

    Monthly,

    Minute,

    Hourly;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }

    public static Collection<ScheduleType> uiSet() {
        Collection<ScheduleType> set = EnumSet.allOf(ScheduleType.class);
        set.remove(Manual);
        return set;
    }
}
