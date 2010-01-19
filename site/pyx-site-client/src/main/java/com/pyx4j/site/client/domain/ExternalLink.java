/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 10, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class ExternalLink extends Link {

    public String href;

    public ExternalLink(String html, String href) {
        super(html);
        this.href = href;
    }

}
