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
package com.propertyvista.domain.dashboard.gadgets.vacancyreport;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

@DiscriminatorValue("TurnoverAnalysisSettings")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface TurnoverAnalysisSettings extends AbstractGadgetSettings {
    IPrimitive<Boolean> isTurnoverMeasuredByPercent();

    IPrimitive<AnalysisResolution> turnoverAnalysisResolution();
}
