/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.admin.SiteDescriptorCrudService;
import com.propertyvista.crm.server.util.TransientListHelpers;
import com.propertyvista.crm.server.util.TransientListHelpers.DefaultWorkflowAdapter;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageMetaTags;
import com.propertyvista.domain.site.PortalImageResource;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteTitles;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.domain.site.gadgets.CustomGadgetContent;
import com.propertyvista.domain.site.gadgets.NewsGadgetContent;
import com.propertyvista.domain.site.gadgets.TestimonialsGadgetContent;
import com.propertyvista.dto.SiteDescriptorDTO;
import com.propertyvista.server.proxy.HttpsProxyInjection;

public class SiteGeneralCrudServiceImpl extends AbstractCrudServiceDtoImpl<SiteDescriptor, SiteDescriptorDTO> implements SiteDescriptorCrudService {

    public SiteGeneralCrudServiceImpl() {
        super(SiteDescriptor.class, SiteDescriptorDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void retrieveHomeItem(AsyncCallback<Key> callback) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        List<Key> list = Persistence.service().queryKeys(criteria);
        if (list.isEmpty()) {
            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            Persistence.service().persist(site);
            Persistence.service().commit();
            callback.onSuccess(site.getPrimaryKey());
        } else {
            callback.onSuccess(list.get(0));
        }
    }

    @Override
    protected void enhanceRetrieved(SiteDescriptor in, SiteDescriptorDTO dto, RetrieveTarget RetrieveTarget) {
        // load transient data:
        EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
        criteria.asc(criteria.proto().displayOrder().getPath().toString());
        dto.locales().addAll(Persistence.service().query(criteria));
    }

    @Override
    protected void persist(final SiteDescriptor dbo, final SiteDescriptorDTO in) {
        // generate proxy white list
        if (in.residentPortalSettings().useCustomHtml().isBooleanTrue()) {
            in.residentPortalSettings().proxyWhitelist().clear();
            for (HtmlContent customHtml : in.residentPortalSettings().customHtml()) {
                if (customHtml.isEmpty() || customHtml.html().isNull()) {
                    continue;
                }
                in.residentPortalSettings().proxyWhitelist().addAll(HttpsProxyInjection.generateWhitelist(customHtml.html().getValue()));
            }
        }
        // keep the sort order
        for (int idx = 0; idx < in.locales().size(); idx++) {
            in.locales().get(idx).displayOrder().setValue(idx);
        }
        final List<AvailableLocale> removedLocales = new ArrayList<AvailableLocale>();
        TransientListHelpers.save(in.locales(), AvailableLocale.class, new DefaultWorkflowAdapter<AvailableLocale>() {
            @Override
            public boolean doBefore(AvailableLocale item) {
                removedLocales.add(item);
                // don't delete just yet - will do it after all other nodes are saved
                return false;
            }
        });

        dbo._updateFlag().updated().setValue(new Date());
        super.persist(dbo, in);

        for (AvailableLocale locale : removedLocales) {
            onDeleteLocale(locale);
            Persistence.service().delete(locale);
        }
    }

    private void onDeleteLocale(AvailableLocale locale) {
        // check for dependents in Gadgets
        // News
        for (NewsGadgetContent content : Persistence.service().query(EntityQueryCriteria.create(NewsGadgetContent.class))) {
            Iterator<News> itNews = content.news().iterator();
            while (itNews.hasNext()) {
                News item = itNews.next();
                if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                    itNews.remove();
                }
            }
            Persistence.service().persist(content);
        }
        // Testimonials
        for (TestimonialsGadgetContent content : Persistence.service().query(EntityQueryCriteria.create(TestimonialsGadgetContent.class))) {
            Iterator<Testimonial> itTestim = content.testimonials().iterator();
            while (itTestim.hasNext()) {
                Testimonial item = itTestim.next();
                if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                    itTestim.remove();
                }
            }
            Persistence.service().persist(content);
        }
        // Custom
        EntityQueryCriteria<CustomGadgetContent> criteria = EntityQueryCriteria.create(CustomGadgetContent.class);
        criteria.or(PropertyCriterion.eq(criteria.proto().htmlContent().locale(), locale), PropertyCriterion.isNull(criteria.proto().htmlContent().locale()));
        Persistence.service().delete(criteria);

        // remove titles, logos and slogans from SiteDescriptor
        SiteDescriptor site = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
        Iterator<SiteTitles> itTitles = site.siteTitles().iterator();
        while (itTitles.hasNext()) {
            SiteTitles item = itTitles.next();
            if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                itTitles.remove();
            }
        }
        Iterator<PortalImageResource> itLogos = site.logo().iterator();
        while (itLogos.hasNext()) {
            PortalImageResource item = itLogos.next();
            if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                itLogos.remove();
            }
        }
        Iterator<HtmlContent> itSlog = site.slogan().iterator();
        while (itSlog.hasNext()) {
            HtmlContent item = itSlog.next();
            if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                itSlog.remove();
            }
        }
        Iterator<PageMetaTags> itMeta = site.metaTags().iterator();
        while (itMeta.hasNext()) {
            PageMetaTags item = itMeta.next();
            if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                itMeta.remove();
            }
        }

        // remove page content and caption from PageDescriptors
        Iterator<PageDescriptor> itPage = site.childPages().iterator();
        while (itPage.hasNext()) {
            PageDescriptor page = itPage.next();
            Iterator<PageContent> itCont = page.content().iterator();
            while (itCont.hasNext()) {
                PageContent item = itCont.next();
                if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                    itCont.remove();
                }
            }
            Iterator<PageCaption> itCapt = page.caption().iterator();
            while (itCapt.hasNext()) {
                PageCaption item = itCapt.next();
                if (item.locale().isEmpty() || item.locale().businessEquals(locale)) {
                    itCapt.remove();
                }
            }
        }
        Persistence.service().persist(site);
    }
}
