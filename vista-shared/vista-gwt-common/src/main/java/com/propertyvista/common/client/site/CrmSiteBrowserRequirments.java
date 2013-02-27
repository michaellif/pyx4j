/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.site;

import com.pyx4j.gwt.commons.BrowserType;

public class CrmSiteBrowserRequirments {

    public static boolean isBrowserCompatible() {
        if (BrowserType.isIE()) {
            return BrowserType.isIENative() && ((VistaSite.isIEVersion9Native() && BrowserType.isIE8Native()) || BrowserType.isIE10());
        } else if (BrowserType.isFirefox() || BrowserType.isSafari()) {
            return true;
        } else {
            return false;
        }
    }

}
