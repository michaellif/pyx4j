/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author aroytbur
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@Translatable
public enum AptUnitOffMarketType {

    down,

    model,

    employee,

    construction,

    office,

    other;

    @Override
    public String toString() {
        return I18nEnum.tr(this);
    }
}
