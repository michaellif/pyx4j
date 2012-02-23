/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit.dialogs;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;

public interface ScopingResultDTO extends IEntity {

    public enum ScopingResult {

        available, renovation, offMarket;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    @NotNull
    IPrimitive<ScopingResult> scopingResult();

    @NotNull
    IPrimitive<OffMarketType> offMarketType();

    @NotNull
    IPrimitive<LogicalDate> renovationEndsOn();

}
