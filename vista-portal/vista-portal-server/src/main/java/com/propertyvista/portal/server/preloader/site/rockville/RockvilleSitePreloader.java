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
package com.propertyvista.portal.server.preloader.site.rockville;

import java.util.Date;

import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.server.preloader.site.AbstractSitePreloader;

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
    protected String object1() {
        return "#072255";
    }

    @Override
    protected String object2() {
        return "#5D466B";
    }

    @Override
    protected String contrast1() {
        return "#8BAEDA";
    }

    @Override
    protected String contrast2() {
        return "#5177A6";
    }

    @Override
    protected String foreground() {
        return "#444444";
    }

    @Override
    protected String background() {
        return "#666666";
    }

    @Override
    protected String form() {
        return "#666666";
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String copyright() {
        return "© " + pmcName() + " " + (1900 + new Date().getYear());
    }

}
