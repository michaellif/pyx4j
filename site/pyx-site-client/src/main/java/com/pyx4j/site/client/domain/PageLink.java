/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 10, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class PageLink extends Link {

    public PageUri uri;

    public PageLink(String html, PageUri uri) {
        super(html);
        this.uri = uri;
    }

}
