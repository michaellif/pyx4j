/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.csv.CSVLoad;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.onboarding.CheckAvailabilityRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.domain.admin.Pmc;

public class CheckAvailabilityRequestHandler extends AbstractRequestHandler<CheckAvailabilityRequestIO> {

    private static Set<String> reservedWords;

    private static Set<String> reservedWordsWildCards;

    public CheckAvailabilityRequestHandler() {
        super(CheckAvailabilityRequestIO.class);

        if (reservedWords == null) {
            reservedWords = new HashSet<String>();
            load(CSVLoad.loadFile("badWords.csv", "name"));
            load(CSVLoad.loadFile("reservedPmcWords.csv", "name"));
        }
    }

    private static void load(String[] strings) {
        //TODO Support WildCards
        reservedWords.addAll(Arrays.asList(strings));
    }

    @Override
    public ResponseIO execute(CheckAvailabilityRequestIO request) {
        boolean isReserved = false;
        String dnsName = request.dnsName().getValue().toLowerCase();
        isReserved = !isValidName(dnsName);
        isReserved = isReserved || reservedWords.contains(dnsName);

        if (!isReserved) {
            // TODO do WildCards matching
        }

        if (!isReserved) {
            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), dnsName));
            if (Persistence.service().retrieve(criteria) != null) {
                isReserved = true;
            }
        }

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(!isReserved);
        return response;
    }

    private boolean isValidName(String value) {
        return (value.length() < 63) && value.matches("[a-z0-9-]+");
    }

}
