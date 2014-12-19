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
package com.propertyvista.preloader.site.metcup;

import java.util.Date;

import com.propertyvista.biz.preloader.site.AbstractSitePreloader;
import com.propertyvista.domain.site.SiteDescriptor.Skin;

public class MetCapSitePreloader extends AbstractSitePreloader {

    @Override
    protected String pmcName() {
        return "MetCap Property Management UK";
    }

    @Override
    protected Skin skin() {
        return Skin.skin1;
    }

    @Override
    protected Integer object1() {
        return 207;
    }

    @Override
    protected Integer object2() {
        return 123;
    }

    @Override
    protected Integer contrast1() {
        return 123;
    }

    @Override
    protected Integer contrast2() {
        return 123;
    }

    @Override
    protected Integer contrast3() {
        return 123;
    }

    @Override
    protected Integer contrast4() {
        return 123;
    }

    @Override
    protected Integer contrast5() {
        return 123;
    }

    @Override
    protected Integer contrast6() {
        return 123;
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
        return 207;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String copyright() {
        return "� " + pmcName() + " " + (1900 + new Date().getYear());
    }

}
