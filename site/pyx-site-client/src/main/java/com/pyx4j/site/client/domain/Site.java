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

    public SiteProperties properties;

    public String logoUrl;

    public Site() {

    }

    //TODO
    public Page getPage(String pageBreadcrumb) {
        return pages.size() > 1 ? pages.get(1) : null;
    }

    public Page getHomePage() {
        return pages.size() > 0 ? pages.get(0) : null;
    }

}
