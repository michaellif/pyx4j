/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.NamespaceNotFoundException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.SystemState;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteTitles;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.SiteWasNotSetUpUserRuntimeException;
import com.propertyvista.shared.i18n.CompiledLocale;

@IgnoreSessionToken
public class SiteThemeServicesImpl implements SiteThemeServices {

    private static final I18n i18n = I18n.get(SiteThemeServicesImpl.class);

    public static SiteDescriptor getSiteDescriptorFromCache() {
        SiteDescriptor descriptor = null;
        Key descriptorKey = (Key) CacheService.get(SiteDescriptor.cacheKey);
        if (descriptorKey != null) {
            descriptor = Persistence.service().retrieve(SiteDescriptor.class, descriptorKey);
        }
        if (descriptor == null) {
            SiteDescriptor siteDescriptor = null;
            try {
                siteDescriptor = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
            } catch (NamespaceNotFoundException e) {
                siteDescriptor = null;
            }
            if (siteDescriptor != null) {
                descriptor = siteDescriptor.duplicate();
                CacheService.put(SiteDescriptor.cacheKey, descriptor.getPrimaryKey());
            }
        }
        return descriptor;
    }

    @Override
    public void retrieveSiteDescriptor(AsyncCallback<SiteDefinitionsDTO> callback, CompiledLocale locale) {

        // This is the fist service the GWT application is calling. Manage the system down.
        if (SystemMaintenance.getState() == SystemState.Unavailable) {
            throw new UserRuntimeException(SystemMaintenance.getApplicationMaintenanceMessage());
        }

        SiteDescriptor descriptor = getSiteDescriptorFromCache();
        if (descriptor == null) {
            throw new SiteWasNotSetUpUserRuntimeException(i18n.tr("This property management site was not set-up yet"));
        }
        SiteDefinitionsDTO def = EntityFactory.create(SiteDefinitionsDTO.class);
        def.palette().setValue(descriptor.sitePalette().getValue());
        def.skin().setValue(descriptor.skin().getValue());

        def.features().set(VistaDeployment.getCurrentVistaFeatures());

        def.isGoogleAnalyticDisableForEmployee().setValue(
                ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).isGoogleAnalyticDisableForEmployee());
        def.enviromentTitleVisible().setValue(((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).enviromentTitleVisible());

        // TODO different resources base on locale
        if (descriptor.logo().size() > 0) {
            def.logoAvalable().setValue(Boolean.TRUE);
        }

        for (SiteTitles t : descriptor.siteTitles()) {
            if (locale == t.locale().lang().getValue()) {
                def.siteTitles().setValue(t.getValue());
                break;
            }
        }
        // Second pass to match en_US to en
        if (def.siteTitles().isNull()) {
            for (SiteTitles t : descriptor.siteTitles()) {
                if ((locale.name().startsWith(t.locale().lang().getValue().name())) || (t.locale().lang().getValue().name().startsWith(locale.name()))) {
                    def.siteTitles().setValue(t.getValue());
                    break;
                }
            }
        }

        callback.onSuccess(def);
    }
}
