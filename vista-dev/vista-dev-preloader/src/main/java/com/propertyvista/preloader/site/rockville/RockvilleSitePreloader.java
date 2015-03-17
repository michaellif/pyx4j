/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vlads
 */
package com.propertyvista.preloader.site.rockville;

import java.util.Date;

import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.biz.preloader.site.AbstractSitePreloader;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.shared.config.VistaFeatures;

public class RockvilleSitePreloader extends AbstractSitePreloader {

    @Override
    protected String pmcName() {
        return "Rock Ville";
    }

    @Override
    protected Skin skin() {
        return Skin.skin3;
    }

    @Override
    protected Integer object1() {
        return 200;
    }

    @Override
    protected Integer object2() {
        return 277;
    }

    @Override
    protected Integer contrast1() {
        return 120;
    }

    @Override
    protected Integer contrast2() {
        return 244;
    }

    @Override
    protected Integer contrast3() {
        return 194;
    }

    @Override
    protected Integer contrast4() {
        return 194;
    }

    @Override
    protected Integer contrast5() {
        return 194;
    }

    @Override
    protected Integer contrast6() {
        return 194;
    }

    @Override
    protected Integer foreground() {
        return 220;
    }

    @Override
    protected Integer formBackground() {
        return 270;
    }

    @Override
    protected Integer siteBackground() {
        return 270;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String copyright() {
        return "© " + pmcName() + " " + (1900 + new Date().getYear());
    }

    @Override
    protected String address() {
        ISOCountry country = VistaFeatures.instance().countryOfOperation().country;
        InternationalAddress address = CommonsGenerator.createInternationalAddress(country);
        return getFormattedAddress(address);
    }

    @Override
    protected String phone1() {
        return "+1-" + CommonsGenerator.createPhone(DataGenerator.randomPhone("800"));
    }

    @Override
    protected String phone2() {
        return "+1-" + CommonsGenerator.createPhone(DataGenerator.randomPhone("408"), "123");
    }

    @Override
    protected String fax() {
        return "+1-" + CommonsGenerator.createPhone(DataGenerator.randomPhone("408"));
    }

}
