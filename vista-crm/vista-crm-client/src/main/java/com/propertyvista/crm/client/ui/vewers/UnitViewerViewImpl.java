/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.vewers;

import com.propertyvista.crm.client.ui.editors.UnitEditorForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.AptUnit;

public class UnitViewerViewImpl extends ViewerViewImplBase<AptUnit> implements IUnitViewerView {

    public UnitViewerViewImpl() {
        super(new CrmSiteMap.Viewers.Unit());
        setViewer(new UnitEditorForm(new CrmViewersComponentFactory()));
    }
}