/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 10, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.SiteMap;

public class PageLink extends LinkBarItem {

    public final String uri;

    public PageLink(String html, String uri) {
        super(html);
        this.uri = uri;
    }

    public PageLink(String html, Class<? extends NavigNode> node) {
        this(html, SiteMap.getPageUri(node));
    }

}
