/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.domain.site.PageContent;
import com.propertyvista.portal.domain.site.PageDescriptor;
import com.propertyvista.portal.server.generator.CommonsGenerator;

public class PortalSitePreload extends AbstractDataPreloader {

    @Override
    public String create() {

        PageDescriptor landingPage = EntityFactory.create(PageDescriptor.class);
        landingPage.type().setValue(PageDescriptor.Type.landing);
        landingPage.content().content().setValue(CommonsGenerator.lipsum());
        landingPage.content().path().setValue(PageContent.PATH_SEPARATOR);

        {
            PageDescriptor page = EntityFactory.create(PageDescriptor.class);
            page.type().setValue(PageDescriptor.Type.staticContent);
            page.caption().setValue("Home");
            page.content().content().setValue(CommonsGenerator.lipsum());
            landingPage.childPages().add(page);
        }

        {

            PageDescriptor page = EntityFactory.create(PageDescriptor.class);
            page.type().setValue(PageDescriptor.Type.findApartment);
            page.content().content().setValue(CommonsGenerator.lipsum());
            landingPage.childPages().add(page);
        }

        {
            PageDescriptor page = EntityFactory.create(PageDescriptor.class);
            page.type().setValue(PageDescriptor.Type.residence);
            landingPage.childPages().add(page);
        }

        {
            PageDescriptor page = EntityFactory.create(PageDescriptor.class);
            page.type().setValue(PageDescriptor.Type.staticContent);
            page.caption().setValue("About Us");
            page.content().content().setValue(CommonsGenerator.lipsum());
            {
                PageDescriptor page2 = EntityFactory.create(PageDescriptor.class);
                page2.type().setValue(PageDescriptor.Type.staticContent);
                page2.caption().setValue("Overview");
                page2.content().content().setValue(CommonsGenerator.lipsum());
                page.childPages().add(page2);
            }
            {
                PageDescriptor page2 = EntityFactory.create(PageDescriptor.class);
                page2.type().setValue(PageDescriptor.Type.staticContent);
                page2.caption().setValue("Team");
                page2.content().content().setValue(CommonsGenerator.lipsum());
                page.childPages().add(page2);
            }
            landingPage.childPages().add(page);
        }

        {
            PageDescriptor page = EntityFactory.create(PageDescriptor.class);
            page.type().setValue(PageDescriptor.Type.staticContent);
            page.caption().setValue("Contact Us");
            page.content().content().setValue(CommonsGenerator.lipsum());
            landingPage.childPages().add(page);
        }

        int pagesCount = saveCascade(landingPage);

        StringBuilder b = new StringBuilder();
        b.append("Created " + pagesCount + " Pages");
        return b.toString();
    }

    private int saveCascade(PageDescriptor page) {
        PersistenceServicesFactory.getPersistenceService().persist(page);
        int pagesCount = 1;
        for (PageDescriptor c : page.childPages()) {
            String path = page.content().path().getValue();
            if (!path.endsWith(PageContent.PATH_SEPARATOR)) {
                path += PageContent.PATH_SEPARATOR;
            }
            if (c.caption().isNull()) {
                path += c.type().getStringView();
            } else {
                path += c.caption().getStringView();
            }
            c.content().path().setValue(path);
            c.parent().set(page);
            pagesCount += saveCascade(c);
            pagesCount++;
        }
        return pagesCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(PageDescriptor.class, PageContent.class);
    }

}
