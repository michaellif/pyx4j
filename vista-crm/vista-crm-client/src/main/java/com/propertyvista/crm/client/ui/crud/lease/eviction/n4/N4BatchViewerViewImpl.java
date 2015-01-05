/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchViewerViewImpl extends CrmViewerViewImplBase<N4BatchDTO> implements N4BatchViewerView {

    public N4BatchViewerViewImpl() {
        setForm(new N4BatchForm(this));
    }
}
