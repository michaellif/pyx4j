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
package com.propertyvista.preloader.site.demo;

import java.util.Date;

import com.propertyvista.biz.preloader.site.AbstractSitePreloader;
import com.propertyvista.domain.site.SiteDescriptor.Skin;

public class DemoSitePreloader extends AbstractSitePreloader {

    @Override
    protected String pmcName() {
        return "Demo Ridge";
    }

    @Override
    protected Skin skin() {
        return Skin.skin3;
    }

    @Override
    protected Integer object1() {
        return 205;
    }

    @Override
    protected Integer object2() {
        return 205;
    }

    @Override
    protected Integer contrast1() {
        return 0;
    }

    @Override
    protected Integer contrast2() {
        return 194;
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
        return "� " + pmcName() + " " + (1900 + new Date().getYear());
    }

}
