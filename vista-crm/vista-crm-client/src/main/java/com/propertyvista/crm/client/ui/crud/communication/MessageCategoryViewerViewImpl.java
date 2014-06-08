/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.communication.MessageCategory;

public class MessageCategoryViewerViewImpl extends CrmViewerViewImplBase<MessageCategory> implements MessageCategoryViewerView {

    public MessageCategoryViewerViewImpl() {
        setForm(new MessageCategoryForm(this));
    }

}
