/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class Site extends PageContainer {

    public String homePageName;

    public String logoUrl;

    public Site() {

    }

    public void addHomePage(Page page) {
        homePageName = page.name;
        addPage(page);
    }

    public Page getPage(String pageBreadcrumb) {
        return pages.get(pageBreadcrumb);
    }

    public Page getHomePage() {
        return pages.get(homePageName);
    }

}
