/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 28, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.yardi.beans.Messages;

@SuppressWarnings("serial")
public class YardiServiceMessageException extends YardiServiceException {

    private final Messages messages;

    public YardiServiceMessageException(Messages messages) {
        this.messages = messages;
    }

    public Messages getMessages() {
        return messages;
    }

}
