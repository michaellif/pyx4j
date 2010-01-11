/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 11, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.site.client.themes.SiteCSSClass;

public class HtmlPortlet extends HTML {

    public HtmlPortlet(String html) {
        super(html);
        setStyleName(SiteCSSClass.pyx4j_Site_HtmlPortlet.name());
    }

}
