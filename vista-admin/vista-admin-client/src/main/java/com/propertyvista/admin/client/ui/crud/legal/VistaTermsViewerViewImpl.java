/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.legal;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class VistaTermsViewerViewImpl extends AdminViewerViewImplBase<VistaTerms> implements VistaTermsViewerView {

    public VistaTermsViewerViewImpl() {
        super(AdminSiteMap.Legal.Terms.class);

        setForm(new VistaTermsForm(true));
    }
}
