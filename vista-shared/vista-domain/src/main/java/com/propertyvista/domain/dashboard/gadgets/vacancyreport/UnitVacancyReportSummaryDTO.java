/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.vacancyreport;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface UnitVacancyReportSummaryDTO extends IEntity {
    public enum SummarySubject {
        Complex, Region, Ownership, PropertyManager;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public IPrimitive<SummarySubject> summarySubject();

    @Caption(name = "Net Exposure #")
    public IPrimitive<Integer> netExposureAbsolute();

    @Caption(name = "Net Exposure %")
    @Format("#0.00")
    public IPrimitive<Double> netExposureRelative();

    @Caption(name = "Notice #")
    public IPrimitive<Integer> noticeAbsolute();

    @Caption(name = "Notice %")
    @Format("#0.00")
    public IPrimitive<Double> noticeRelative();

    public IPrimitive<Integer> noticeRented();

    @Caption(name = "Occupancy #")
    public IPrimitive<Integer> occupancyAbsolute();

    @Caption(name = "Occupancy %")
    @Format("#0.00")
    public IPrimitive<Double> occupancyRelative();

    @Caption(name = "Vacancy #")
    public IPrimitive<Integer> vacancyAbsolute();

    @Caption(name = "Vacancy %")
    @Format("#0.00")
    public IPrimitive<Double> vacancyRelative();

    public IPrimitive<Integer> vacantRented();

    @Caption(name = "Total Units")
    public IPrimitive<Integer> total();

}
