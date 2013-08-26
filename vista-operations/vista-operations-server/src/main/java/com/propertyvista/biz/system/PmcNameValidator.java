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
package com.propertyvista.biz.system;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.csv.CSVLoad;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.ReservedPmcNames;

public class PmcNameValidator {

    private static Logger log = LoggerFactory.getLogger(PmcNameValidator.class);

    private static final Set<String> reservedWords = new HashSet<String>();

    private static final Set<String> reservedWordsRegex = new HashSet<String>();

    static {
        loadReservedWords();
    }

    private static void load(String[] strings) {
        for (String reservedWord : strings) {
            if (hasWildCard(reservedWord)) {
                if (reservedWord.matches("[a-z0-9-\\?\\*]+")) {
                    reservedWordsRegex.add(wildCardToRegex(reservedWord));
                } else {
                    log.warn(SimpleMessageFormat
                            .format("skipping reserved or forbidden domain name pattern ''{0}'' because it contains charachters that cannot be used in a domain name anyway",
                                    reservedWord));

                }
            } else {
                reservedWords.add(reservedWord);
            }
        }
    }

    static void setReservedWords(String[] words) {
        reservedWordsRegex.clear();
        reservedWords.clear();
        load(words);
    }

    static void loadReservedWords() {
        reservedWordsRegex.clear();
        reservedWords.clear();
        load(CSVLoad.loadFile("badWords.csv", "name"));
        load(CSVLoad.loadFile("reservedPmcWords.csv", "name"));
        if (!ApplicationMode.isDevelopment()) {
            load(CSVLoad.loadFile("reservedPmcTestWords.csv", "name"));
        }
        reservedWordsRegex.add("^.*prod\\d*$");
        reservedWordsRegex.add("^.*staging\\d*$");
    }

    public static boolean canCreatePmcName(String dnsName, String onboardingAccountId) {
        return PmcNameValidator.isDnsNameValid(dnsName) && !PmcNameValidator.isDnsReserved(dnsName)
                && !PmcNameValidator.isDnsReservedByCustomers(dnsName, onboardingAccountId) && !PmcNameValidator.isDnsTaken(dnsName);
    }

    public static boolean isDnsNameValid(String value) {
        return (value.length() < 63) && value.matches("[a-zA-Z0-9]*[a-zA-Z][a-zA-Z0-9-]*");
    }

    public static boolean isDnsReserved(String dnsName) {
        if (reservedWords.contains(dnsName)) {
            return true;
        }
        for (String reservedWordRegex : reservedWordsRegex) {
            if (dnsName.matches(reservedWordRegex)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDnsReservedByCustomers(String dnsName, String onboardingAccountId) {
        EntityQueryCriteria<ReservedPmcNames> criteria = EntityQueryCriteria.create(ReservedPmcNames.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), dnsName));

        ReservedPmcNames resPmc = Persistence.service().retrieve(criteria);

        if (resPmc == null) {
            return false; // Never reserved
        } else if ((resPmc.onboardingAccountId().getValue() == null) || (onboardingAccountId == null)) {
            return true; // Reserved through Admin app or called through admin app
        } else {
            return !resPmc.onboardingAccountId().getValue().equals(onboardingAccountId); // reserved by another onboarding account
        }
    }

    public static boolean isDnsTaken(String dnsName) {
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.or(PropertyCriterion.eq(criteria.proto().dnsName(), dnsName), PropertyCriterion.eq(criteria.proto().namespace(), dnsName));
        return (Persistence.service().retrieve(criteria) != null);
    }

    private static String wildCardToRegex(String reservedPmcName) {
        String pmcNameRegex = reservedPmcName.replaceAll("\\*", ".*");
        pmcNameRegex = pmcNameRegex.replaceAll("\\?", ".");
        pmcNameRegex = "^" + pmcNameRegex + "$";
        return pmcNameRegex;
    }

    private static boolean hasWildCard(String reservedWord) {
        return reservedWord.contains("*") | reservedWord.contains("?");
    }

}
