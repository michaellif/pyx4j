/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

import java.util.ArrayList;
import java.util.List;

public class PageContainer {

    public final List<Page> pages = new ArrayList<Page>();

    public PageContainer() {

    }

    public void addPage(Page page) {
        pages.add(page);
        page.parent = this;
    }

}
