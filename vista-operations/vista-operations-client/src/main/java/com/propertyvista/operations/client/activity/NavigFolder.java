/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2011
 * @author Misha
 */
package com.propertyvista.operations.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.AppPlace;

public class NavigFolder {

    public enum Type {
        Regular, Shortcuts, Favorites
    }

    private final Type type;

    private final String title;

    private final ImageResource normal;

    private final ImageResource hover;

    private final ImageResource active;

    private final List<AppPlace> navigItems;

    public NavigFolder(String title) {
        this(title, null, null, null);
    }

    public NavigFolder(Type type, String title) {
        this(type, title, null, null, null);
    }

    public NavigFolder(String title, ImageResource normal, ImageResource hover, ImageResource active) {
        this(Type.Regular, title, normal, hover, active);
    }

    public NavigFolder(Type type, String title, ImageResource normal, ImageResource hover, ImageResource active) {
        this.type = type;
        this.title = title;
        this.normal = normal;
        this.hover = hover;
        this.active = active;

        navigItems = new ArrayList<AppPlace>();
    }

    public List<AppPlace> getNavigItems() {
        return navigItems;
    }

    @Deprecated
    public void addNavigItem(AppPlace item) {
        navigItems.add(item);
    }

    public void addNavigItem(AppPlace item, Behavior... behaviors) {
        if (SecurityController.check(behaviors)) {
            navigItems.add(item);
        }
    }

    // resources:

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public ImageResource getImageNormal() {
        return normal;
    }

    public ImageResource getImageHover() {
        return hover;
    }

    public ImageResource getImageActive() {
        return active;
    }
}
