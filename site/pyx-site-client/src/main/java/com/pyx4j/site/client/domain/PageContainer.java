/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

import java.util.HashMap;
import java.util.Map;

public class PageContainer {

    public final Map<String, Page> pages = new HashMap<String, Page>();

    public PageContainer() {

    }

    public void addPage(Page page) {
        pages.put(page.name, page);
        page.parent = this;
    }

}
