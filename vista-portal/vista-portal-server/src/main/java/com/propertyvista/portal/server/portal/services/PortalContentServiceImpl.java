/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.Locale;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.server.I18nManager;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.domain.site.PortalBannerImage;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteLogoImageResource;
import com.propertyvista.portal.rpc.portal.services.PortalContentService;
import com.propertyvista.portal.rpc.portal.web.dto.PortalContentDTO;

public class PortalContentServiceImpl implements PortalContentService {

    @Override
    @IgnoreSessionToken
    public void getPortalContent(AsyncCallback<PortalContentDTO> callback) {
        SiteDescriptor site = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));

        PortalContentDTO portalContentDto = EntityFactory.create(PortalContentDTO.class);

        portalContentDto.skin().set(site.skin());
        portalContentDto.sitePalette().set(site.sitePalette());

        // logoSmall();
        // logoLarge();
        String lang = I18nManager.getThreadLocale().getLanguage();
        for (SiteLogoImageResource logo : site.logo()) {
            if (new Locale(logo.locale().lang().getValue().name()).getLanguage().equals(lang)) {
                portalContentDto.logoSmall().set(logo.small().duplicate());
                portalContentDto.logoLarge().set(logo.large().duplicate());
                break;
            }
        }
        if (portalContentDto.logoSmall().isNull()) {
            // TODO throw exception logo image for current locale not found???
        }

        // pmcInfo();
        for (HtmlContent cont : site.pmcInfo()) {
            if (lang.equals(cont.locale().lang().getValue().getLanguage())) {
                portalContentDto.pmcInfo().set(cont);
                break;
            }
        }

        // portalBanner();
        for (PortalBannerImage banner : site.portalBanner()) {
            if (lang.equals(banner.locale().lang().getValue().getLanguage())) {
                portalContentDto.portalBanner().set(banner);
                break;
            }
        }

        // socialLinks();
        portalContentDto.socialLinks().addAll(site.socialLinks());

        callback.onSuccess(portalContentDto);
    }
}
