/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.extra;

import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.site.client.IsView;

public interface QuickTipView extends IsView {

    public interface QuickTipPresenter {

    }

    void setQuickTip(HTMLPanel contentPanel, ThemeColor background);
}
