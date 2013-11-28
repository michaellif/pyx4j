/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.communication;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;

public interface FeedEMail {

    public interface AttachmentDescriptor {
        public String getName();

        public String getContentType();

        public String getAttachment();
    }

    public String getTo();

    public boolean isHtmlbody();

    public String getBody();

    public String getSender();

    public String getCc();

    public String getSubject();

    public AttachmentDescriptor getAttachment();

    public IMailServiceConfigConfiguration getMailServiceConfiguration();
}
