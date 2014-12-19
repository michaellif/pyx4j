/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-02-10
 * @author vlads
 */
package com.pyx4j.server.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

class SMTPMailAttachmentDataSource implements DataSource {

    private final MailAttachment attachment;

    SMTPMailAttachmentDataSource(MailAttachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getName() {
        return attachment.getName();
    }

    @Override
    public String getContentType() {
        if (attachment.getContentType() == null) {
            return "application/octet-stream";
        } else {
            return attachment.getContentType();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(attachment.getBody());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }
}
