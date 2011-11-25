/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.misc;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

public interface EquifaxApproval extends IEntity {

    @I18n
    @XmlType(name = "Suggested Decision")
    public enum Decision {

        Pending,

        Approve,

        @Translate("Request for Guarantor or further information")
        RequestInfo,

        Decline;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Format("#0.00")
    @Caption(name = "% Rent Approved")
    IPrimitive<Double> percenrtageApproved();

    IPrimitive<Decision> suggestedDecision();
}
