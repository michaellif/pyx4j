package com.pyx4j.wicketsite.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * Homepage
 */
public class SLSite extends WebPage {

    private static final long serialVersionUID = 1L;

    public SLSite() {
        add(new BookmarkablePageLink("homeLink", Home.class));
        add(new BookmarkablePageLink("servicesLink", Services.class));
        add(new BookmarkablePageLink("aboutUsLink", AboutUs.class));
        add(new BookmarkablePageLink("contactLink", Contact.class));

    }
}
