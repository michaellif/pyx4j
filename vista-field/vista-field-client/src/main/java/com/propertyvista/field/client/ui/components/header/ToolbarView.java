/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components.header;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.PageOrientation;

public interface ToolbarView extends IsWidget {

    void showNavigationDetails(boolean isVisible);

    void setContextMenuVisibility();

    void setPageOrientation(PageOrientation orientation);
}
