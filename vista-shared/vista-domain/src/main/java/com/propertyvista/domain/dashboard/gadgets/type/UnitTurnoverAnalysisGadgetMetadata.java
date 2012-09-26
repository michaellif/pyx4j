/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 17, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

@DiscriminatorValue("TurnoverAnalysisSettings")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Caption(name = "Turnover Analysis")
@Transient
@GadgetDescription(keywords = { "Units", "Chart", "Turnover" })
public interface UnitTurnoverAnalysisGadgetMetadata extends GadgetMetadata, BuildingGadget {

    IPrimitive<Boolean> isTurnoverMeasuredByPercent();

    /**
     * This is not going to be used in the first released version: the resolution is always one month.
     */
    @Deprecated
    IPrimitive<AnalysisResolution> turnoverAnalysisResolution();

    IPrimitive<Boolean> customizeDate();

    IPrimitive<LogicalDate> asOf();
}
