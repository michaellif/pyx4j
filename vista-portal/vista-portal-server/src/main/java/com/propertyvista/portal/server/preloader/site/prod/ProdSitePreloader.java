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
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.site.prod;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.portal.server.preloader.site.AbstractSitePreloader;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ProdSitePreloader extends AbstractSitePreloader {

    @Override
    protected String pmcName() {
        return (String) getParameter(VistaDataPreloaderParameter.pmcName);
    }

    @Override
    protected List<CompiledLocale> getLocale() {
        List<CompiledLocale> l = new Vector<CompiledLocale>();
        l.add(CompiledLocale.en);

        if (VistaFeatures.instance().countryOfOperation() != CountryOfOperation.UK) {
            l.add(CompiledLocale.fr);
        }
        return l;
    }

    @Override
    protected Skin skin() {
        return Skin.skin5;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String copyright() {
        return "© " + pmcName() + " " + (1900 + new Date().getYear());
    }

    @Override
    protected void createTestimonialGadget(SiteDescriptor site, List<LocaleInfo> siteLocale) {
    }

    @Override
    protected void createNewsGadget(SiteDescriptor site, List<LocaleInfo> siteLocale) {
    }

    @Override
    protected Integer object1() {
        return 200;
    }

    @Override
    protected Integer object2() {
        return 100;
    }

    @Override
    protected Integer contrast1() {
        return 150;
    }

    @Override
    protected Integer contrast2() {
        return 200;
    }

    @Override
    protected Integer foreground() {
        return 250;
    }

    @Override
    protected Integer background() {
        return 300;
    }

}
