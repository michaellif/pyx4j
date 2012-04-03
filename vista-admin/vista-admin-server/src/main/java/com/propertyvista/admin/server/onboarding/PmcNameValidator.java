/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.csv.CSVLoad;

import com.propertyvista.server.domain.admin.Pmc;

public class PmcNameValidator {

    private static Set<String> reservedWords;

    private static Set<String> reservedWordsWildCards;

    static {
        reservedWords = new HashSet<String>();
        load(CSVLoad.loadFile("badWords.csv", "name"));
        load(CSVLoad.loadFile("reservedPmcWords.csv", "name"));
        if (!ApplicationMode.isDevelopment()) {
            load(CSVLoad.loadFile("reservedPmcTestWords.csv", "name"));
        }
    }

    private static void load(String[] strings) {
        //TODO Support WildCards
        reservedWords.addAll(Arrays.asList(strings));
    }

    public static boolean canCreatePmcName(String dnsName) {
        return PmcNameValidator.isDnsNameValid(dnsName) && !PmcNameValidator.isDnsReserved(dnsName) && !PmcNameValidator.isDnsTaken(dnsName);
    }

    public static boolean isDnsNameValid(String value) {
        return (value.length() < 63) && value.matches("[a-z0-9-]+");
    }

    public static boolean isDnsReserved(String dnsName) {
        if (reservedWords.contains(dnsName)) {
            return true;
        }
        // TODO do WildCards matching
        return false;
    }

    public static boolean isDnsTaken(String dnsName) {
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), dnsName));
        return (Persistence.service().retrieve(criteria) != null);
    }
}
