/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import com.propertyvista.portal.domain.ref.Country;
import com.propertyvista.portal.domain.ref.Province;
import com.propertyvista.server.domain.dev.DevelopmentUser;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

public class DevelopmentSecurityPreload extends AbstractDataPreloader {

    @Override
    public String create() {
        int developersCount = 0;

        List<DevelopmentUser> developmentUsers = EntityCSVReciver.create(DevelopmentUser.class).loadFile("contacts.csv");
        PersistenceServicesFactory.getPersistenceService().persist(developmentUsers);
        developersCount += developmentUsers.size();

        StringBuilder b = new StringBuilder();
        b.append("Created " + developersCount + " DevelopmentUsers");
        return b.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(Province.class, Country.class);
    }

}
