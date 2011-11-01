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

import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.portal.server.preloader.site.AbstractSitePreloader;

public class ProdSitePreloader extends AbstractSitePreloader {

    @Override
    protected String pmcName() {
        return (String) getParameter(VistaDataPreloaderParameter.pmcName);
    }

    @Override
    protected Skin skin() {
        return Skin.skin1;
    }

    @Override
    protected String baseColor() {
        return "#4488bb";
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String copyright() {
        return "© " + pmcName() + " " + (1900 + new Date().getYear());
    }

    @Override
    protected void createTestimonial(List<LocaleInfo> siteLocale) {
    }

    @Override
    protected void createNews(List<LocaleInfo> siteLocale) {
    }

}
