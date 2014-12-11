/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.shared.config.VistaFeatures;

public class PreloadRestrictionsPolicies extends AbstractDataPreloader {

    @Override
    public String create() {
        List<RestrictionsPolicyPreloader> pp = new ArrayList<RestrictionsPolicyPreloader>();
        pp.add(new RestrictionsPolicyPreloader());
        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            pp.add(new RestrictionsPolicyPreloader().province("AB").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("BC").ageOfMajority(19));
            pp.add(new RestrictionsPolicyPreloader().province("MB").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("NB").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("NL").ageOfMajority(19));
            pp.add(new RestrictionsPolicyPreloader().province("NT").ageOfMajority(19));
            pp.add(new RestrictionsPolicyPreloader().province("NS").ageOfMajority(19));
            pp.add(new RestrictionsPolicyPreloader().province("NU").ageOfMajority(19));
            pp.add(new RestrictionsPolicyPreloader().province("ON").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("PE").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("QC").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("SK").ageOfMajority(18));
            pp.add(new RestrictionsPolicyPreloader().province("YT").ageOfMajority(19));
        }

        StringBuilder log = new StringBuilder();
        for (RestrictionsPolicyPreloader p : pp) {
            log.append(p.create()).append("\n");
        }
        return log.toString();
    }

    @Override
    public String delete() {
        return "";
    }

}
