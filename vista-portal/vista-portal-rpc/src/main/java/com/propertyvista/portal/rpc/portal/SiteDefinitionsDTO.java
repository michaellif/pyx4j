/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 7, 2011
 * @author vlads
 */
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.PortalBannerImage;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.domain.site.SiteTitles;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface SiteDefinitionsDTO extends IEntity {

    IPrimitive<Skin> skin();

    SitePalette palette();

    SiteTitles siteTitles();

    IPrimitive<Boolean> logoAvalable();

    SiteImageResource logoSmall();

    SiteImageResource logoLarge();

    SiteImageResource logoLabel();

    PortalBannerImage portalBanner();

    PmcVistaFeatures features();

    IPrimitive<Boolean> isGoogleAnalyticDisableForEmployee();

    IPrimitive<Boolean> enviromentTitleVisible();

    IPrimitive<Boolean> walkMeEnabled();

    IPrimitive<String> walkMeJsAPIUrl();
}
