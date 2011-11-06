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
package com.propertyvista.portal.server.preloader.site.star;

import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.server.preloader.site.AbstractSitePreloader;

public class StarlightSitePreloader extends AbstractSitePreloader {

    @Override
    protected String pmcName() {
        return "Starlight";
    }

    @Override
    protected Skin skin() {
        return Skin.skin1;
    }

    @Override
    protected Integer object1() {
        return 223;
    }

    @Override
    protected Integer object2() {
        return 98;
    }

    @Override
    protected Integer contrast1() {
        return 120;
    }

    @Override
    protected Integer contrast2() {
        return 170;
    }

    @Override
    protected Integer foreground() {
        return 220;
    }

    @Override
    protected Integer background() {
        return 270;
    }

    @Override
    protected String copyright() {
        return "© Starlight Apartments 2011";
    }

}
