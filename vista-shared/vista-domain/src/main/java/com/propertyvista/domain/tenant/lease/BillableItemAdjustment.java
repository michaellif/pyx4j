/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;

public interface BillableItemAdjustment extends IEntity {

    @I18n
    @XmlType(name = "AdjustmentType")
    enum AdjustmentType {
        percentage, monetary, free;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "ChargeType")
    enum ChargeType {
        negotiation, discount, priceCorrection;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "TermType")
    enum TermType {
        firstMonth, lastMonth, term;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<AdjustmentType> adjustmentType();

    @NotNull
    IPrimitive<ChargeType> chargeType();

    @NotNull
    IPrimitive<TermType> termType();

    IPrimitive<String> description();

    /*
     * for percentage - percentage
     * for monetary - amount
     */
    @NotNull
    @Format("#0.00")
    @ToString(index = 1)
    @MemberColumn(name = "adjustmentValue")
    IPrimitive<BigDecimal> value();

    Employee createdBy();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createdWhen();
    
    @I18n
    @XmlType(name = "AdjustmentType")
   
    /**
     * we need to support mid lease BillableItemAdjustments such as AGI ("Above Guideline Increases"), 
     * that might be in pending status, 
     * i.e.waiting for lease to expire first and then to be aplied as part of Lease Renewal process. 
     * These adjustments (status = "postLease") need to be counted as Lease Renewal time but need not be picked up by billing during regular month.  
     * @author Alex
     *
     *    enum Status {
     * postLease, inLease, expired;
     *
     *  @Override
     *  public String toString() {
     *      return I18nEnum.toString(this);
     *  }
     *
     *
     *
     *
     */

    
    
  
    
}
