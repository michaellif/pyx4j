/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-22
 * @author aroytbur
 * @version $Id$
 */
package com.propertyvista.portal.domain;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

/**
 * For now we just assume 2 types of concessions: - Free Months: certain number of months
 * free
 * 
 * - Percent Discount: certain discount over a number of months
 * 
 * Need to redesign
 * 
 * TODO Agreed, this is probably not the best design, either indicate one class with all
 * types of concessions as members, or have one class with type and a single value that
 * will be determined by concession type
 * 
 * 
 * @author aroytbur
 * 
 */
public interface Concession extends IEntity {

    /**
     * Concession type (max 128 chars) TODO we will need to have this of type
     * ConcessionType
     */
    @ToString
    IPrimitive<String> name();

    @Translatable
    public enum ConcessionType {
        freeMonths, percentDiscount;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @MemberColumn(name = "cons_type")
    IPrimitive<ConcessionType> type();

    /**
     * Number of months to apply concession to
     */
    IPrimitive<Double> months();

    /**
     * Percent discount
     */
    IPrimitive<Double> percentage();

}
