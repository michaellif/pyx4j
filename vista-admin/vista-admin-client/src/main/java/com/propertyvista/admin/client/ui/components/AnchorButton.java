/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.components;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

public class AnchorButton extends Anchor {

    public static String DEFAULT_STYLE_PREFIX = "vistaAdmin_AnchorButton";

    public AnchorButton(String caption) {
        super(caption);
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    public AnchorButton(String caption, ClickHandler handler) {
        this(caption);
        addClickHandler(handler);
    }
}
