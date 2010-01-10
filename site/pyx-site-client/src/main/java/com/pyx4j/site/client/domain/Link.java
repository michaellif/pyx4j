/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 10, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class Link {

    public String html;

    public String href;

    public boolean internal;

    public Link(String html, String href, boolean internal) {
        this.html = html;
        this.href = href;
        this.internal = internal;
    }

}
