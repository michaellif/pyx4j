/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.customizations;

import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.ref.ISOCountry;

@I18n
public enum CountryOfOperation {

    Canada(ISOCountry.Canada),

    US(ISOCountry.UnitedStates),

    UK(ISOCountry.UnitedKingdom);

    public final ISOCountry country;

    private CountryOfOperation(ISOCountry country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return country.toString();
    }

}
