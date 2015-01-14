/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author igors
 */
package com.propertyvista.crm.client.ui.crud.communication.listers;

import com.propertyvista.dto.MessageDTO.ViewScope;

public class MessageListerViewTicketCategoryImpl extends MessageListerViewImpl implements MessageListerViewTicketCategory {

    @Override
    protected void setDataTablePanel() {
        super.setDataTablePanel(new MessageLister(this, ViewScope.AllMessages));
    }

}
