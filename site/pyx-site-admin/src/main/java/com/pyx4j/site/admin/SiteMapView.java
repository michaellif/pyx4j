/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;

public class SiteMapView extends AbstractView {

    public SiteMapView() {
        super(new VerticalPanel(), "Site Map", ImageFactory.getImages().image());
        VerticalPanel contentPane = (VerticalPanel) getContentPane();

    }

    @Override
    public Widget getFooterPane() {
        return null;
    }

    @Override
    public MenuBar getMenu() {
        return null;
    }

    @Override
    public Widget getToolbarPane() {
        return null;
    }

}
