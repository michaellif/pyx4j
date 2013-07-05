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
package com.propertyvista.portal.server.portal.services.resident;

import java.util.Locale;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.server.I18nManager;

import com.propertyvista.domain.site.PortalImageResource;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.portal.rpc.portal.dto.PortalFooterContentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.FooterContentService;

public class FooterContentServiceImpl implements FooterContentService {

    @Override
    public void getFooterContent(AsyncCallback<PortalFooterContentDTO> callback) {
        PortalFooterContentDTO portalFooterDto = EntityFactory.create(PortalFooterContentDTO.class);

        SiteDescriptor siteDescriptor = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
        for (PortalImageResource logo : siteDescriptor.logo()) {
            if (new Locale(logo.locale().lang().getValue().name()).getLanguage().equals(I18nManager.getThreadLocale().getLanguage())) {
                portalFooterDto.logo().set(siteDescriptor.logo().get(0).imageResource().duplicate());
                break;
            }
        }
        if (portalFooterDto.logo().isNull()) {
            // TODO throw exception logo image for current locale not found???
        }
        portalFooterDto.content().html()
                .setValue("<div><h3>Content Content Content<h3><div>Even More Content Content Content Content and some Contact Info Info Info</div></div>");
        portalFooterDto.socialLinks().addAll(siteDescriptor.socialLinks());

        callback.onSuccess(portalFooterDto);
    }

}
