/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.settings;

import com.propertyvista.crm.client.ui.editors.EditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.portal.domain.site.PageContent;

public class ContentEditorImpl extends EditorViewImplBase<PageContent> implements ContentEditor {
    public ContentEditorImpl() {
        super(new CrmSiteMap.Settings.Content());
        setEditor(new ContentEditorForm());
    }
}
