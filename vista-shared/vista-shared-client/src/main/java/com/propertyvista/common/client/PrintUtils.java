/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.gwt.commons.print.PrintManager;

import com.propertyvista.common.client.ui.components.MediaUtils;

public class PrintUtils {

    public static void print(Element element) {
        String logo = "<div style=\"text-align: center;\"><img src=\"" + MediaUtils.createSiteLargeLogoUrl() + "\"></div>";
        String html = logo + "<body>" + element.getInnerHTML() + "</body>";
        PrintManager.print(new HTML(html).getElement());
    }

}
