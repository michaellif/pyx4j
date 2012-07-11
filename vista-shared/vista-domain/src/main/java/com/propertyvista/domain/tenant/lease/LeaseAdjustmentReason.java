/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.GlCode;

/**
 * 
 * Corporate-wide reasons (AS defined 20 major)
 * 
 */
@ToStringFormat("{0}{1,choice,null#|!null#, GL Code: {1}}")
public interface LeaseAdjustmentReason extends IEntity {

    @I18n
    @XmlType(name = "LeaseAdjustmentActionType")
    enum ActionType {
        charge, credit;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 0)
    IPrimitive<String> name();

    @NotNull
    IPrimitive<ActionType> actionType();

    @NotNull
    @ToString(index = 1)
    GlCode glCode();

    @Timestamp
    IPrimitive<Date> updated();
}
