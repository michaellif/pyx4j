/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-03
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.ResidentSelfRegistration;

class CustomerRegistrationNameMatching {

    static boolean nameMatch(Name name, ResidentSelfRegistration selfRegistration) {
        if (equalsIgnoreCase(name.lastName(), selfRegistration.lastName()) && equalsIgnoreCase(name.firstName(), selfRegistration.firstName())) {
            return true;
        }
        String fullNameOnFile = fullNameConcat(name.firstName(), name.middleName(), name.lastName());
        String fullNameReg = fullNameConcat(selfRegistration.firstName(), selfRegistration.middleName(), selfRegistration.lastName());
        if ((fullNameOnFile.compareToIgnoreCase(fullNameReg) == 0) || (name.getStringView().compareToIgnoreCase(fullNameReg) == 0)) {
            return true;
        } else {
            return false;
        }
    }

    private static String fullNameConcat(IPrimitive<String> firstName, IPrimitive<String> middleName, IPrimitive<String> lastName) {
        StringBuilder fullName = new StringBuilder();
        if (!firstName.isNull()) {
            fullName.append(normalizeNameFragment(firstName)).append(" ");
        }
        if (!middleName.isNull()) {
            fullName.append(normalizeNameFragment(middleName)).append(" ");
        }
        if (!lastName.isNull()) {
            fullName.append(normalizeNameFragment(lastName)).append(" ");
        }
        return fullName.toString().trim();
    }

    private static String normalizeNameFragment(IPrimitive<String> nameFragment) {
        return nameFragment.getStringView().trim().replaceAll("\\s+", " ");
    }

    private static boolean equalsIgnoreCase(IPrimitive<String> name, IPrimitive<String> nameRegistration) {
        if (CommonsStringUtils.equals(name.getValue(), nameRegistration.getValue())) {
            return true;
        }
        if (name.isNull() || nameRegistration.isNull()) {
            return false;
        }
        return normalizeNameFragment(name).compareToIgnoreCase(normalizeNameFragment(nameRegistration)) == 0;
    }
}
