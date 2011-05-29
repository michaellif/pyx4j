/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.vewers;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.editors.forms.InquiryEditorForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.Inquiry;

public class InquiryViewerViewImpl extends ViewerViewImplBase<Inquiry> implements InquiryViewerView {

    public InquiryViewerViewImpl() {
        super(new CrmSiteMap.Viewers.Inquiry(), new CrmSiteMap.Editors.Inquiry());
        setViewer(new InquiryEditorForm(new CrmViewersComponentFactory()));
    }
}