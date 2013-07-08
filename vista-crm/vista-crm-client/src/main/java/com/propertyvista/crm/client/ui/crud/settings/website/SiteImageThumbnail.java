/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 8, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class SiteImageThumbnail extends Image {
    private double thumbSize = 80;

    public SiteImageThumbnail() {
    }

    public SiteImageThumbnail(double size) {
        thumbSize = size;
    }

    @Override
    public void setUrl(String url) {
        super.setUrl(url);
        if (getWidth() > 0 && getHeight() > 0) {
            scaleToFit();
        } else {
            setVisible(false);
            addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    scaleToFit();
                    setVisible(true);
                }
            });
        }
    }

    private void scaleToFit() {
        if (1.0 * getWidth() / getHeight() > 1) {
            setWidth(thumbSize + "px");
        } else {
            setHeight(thumbSize + "px");
        }
    }

}