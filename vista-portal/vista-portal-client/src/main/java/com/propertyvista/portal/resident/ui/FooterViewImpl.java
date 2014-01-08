/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 8, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractFooterView;

public class FooterViewImpl extends AbstractFooterView implements FooterView {

    public FooterViewImpl() {
        super(new ResidentPortalSiteMap.ResidentPortalTerms.ResidentTermsAndConditions());
    }

}
