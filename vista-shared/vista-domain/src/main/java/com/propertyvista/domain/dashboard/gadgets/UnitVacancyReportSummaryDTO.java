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
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface UnitVacancyReportSummaryDTO extends IEntity {
    public enum SummarySubject {
        Complex, Region, Ownership, PropertyManager
    }

    public IPrimitive<SummarySubject> summarySubject();

    /** could be complex, Property Manager, ownership or region */
    IEntity summarySubjectValue();

    public IPrimitive<Integer> vacant();

    public IPrimitive<Integer> notice();

    public IPrimitive<Integer> vacantRented();

    public IPrimitive<Integer> noticeRented();

    public IPrimitive<Integer> netExposure();

    @Caption(name = "Occupancy #")
    public IPrimitive<Integer> occupancyAbsolute();

    @Caption(name = "Vacancy #")
    public IPrimitive<Integer> vacancyAbsolute();

    @Caption(name = "Occupancy %")
    public IPrimitive<Integer> occupancyRelative();

    @Caption(name = "Vacancy %")
    public IPrimitive<Integer> vacancyRelative();
}
