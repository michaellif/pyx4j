/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ContainerInfo;
import org.apache.wicket.markup.DefaultMarkupCacheKeyProvider;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupCache;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class CustomizablePage extends BasePage implements IMarkupResourceStreamProvider {
    private static final long serialVersionUID = 1L;

    @Override
    public Markup getAssociatedMarkup() {
        if (getCM().isSiteUpdated()) {
            // remove old markup from cache
            IMarkupCache cache = MarkupFactory.get().getMarkupCache();
            String cacheKey = new DefaultMarkupCacheKeyProvider().getCacheKey(this, getClass());
            cache.removeMarkup(cacheKey);
        }
        return super.getAssociatedMarkup();
    }

    /**
     * Page variation is part of the cache key above. This will trigger markup caching per PMC and locale
     */
    @Override
    public String getVariation() {
        return NamespaceManager.getNamespace() + "_" + ((PMSiteWebRequest) getRequest()).getSiteLocale().getStringView();
    }

    @Override
    public IResourceStream getMarkupResourceStream(final MarkupContainer container, Class<?> containerClass) {
        PMSiteContentManager cm = getCM();
        if (this instanceof ResidentsPage && cm != null && cm.isCustomResidentsContentEnabled()) {
            String content = cm.getCustomResidentsContent(((PMSiteWebRequest) getRequest()).getSiteLocale());
            content = content.replaceFirst(DeploymentConsts.RESIDENT_CONTENT_ID, "wicket:id=\"" + RESIDENT_CUSTOM_CONTENT_PANEL + "\"");
            content = content.replaceFirst(DeploymentConsts.RESIDENT_LOGIN_ID, "wicket:id=\"" + RESIDENT_LOGIN_PANEL + "\"");
            return new MarkupResourceStream(new StringResourceStream(content), new ContainerInfo(this), getClass());
        } else {
            return new DefaultMarkupResourceStreamProvider().getMarkupResourceStream(container, containerClass);
        }
    }
}
