/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.equifax.model;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;

/**
 * Value is in months
 */
public class TimeAtPresentEmployer implements EquifaxParameter {

    private final static I18n i18n = I18n.get(TimeAtPresentEmployer.class);

    private final int numMonths;

    public TimeAtPresentEmployer(int numMonths) {
        if (numMonths < 1) {
            throw new UserRuntimeException(i18n.tr("Number at Present Employer of months must be a positive number"));
        }
        this.numMonths = numMonths;
    }

    @Override
    public String getId() {
        return "P0005";
    }

    @Override
    public String getValue() {
        return String.valueOf(numMonths);
    }
}
