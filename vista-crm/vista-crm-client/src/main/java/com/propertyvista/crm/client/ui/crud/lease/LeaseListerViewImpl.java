/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.dto.LeaseDTO;

public class LeaseListerViewImpl extends CrmListerViewImplBase<LeaseDTO> implements LeaseListerView {

    public LeaseListerViewImpl() {
        setDataTablePanel(new LeaseLister() {
            @Override
            public void onPadFileDownload() {
                ((LeaseListerView.LeaseListerPresenter) getPresenter()).downloadPadFile();
            }

            @Override
            public void onPadFileUpload() {
                ((LeaseListerView.LeaseListerPresenter) getPresenter()).uploadPadFile();
            }
        });
    }
}
