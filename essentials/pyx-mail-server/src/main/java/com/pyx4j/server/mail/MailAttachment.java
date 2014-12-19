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

import java.io.Serializable;

public class MailAttachment implements Serializable {

    private static final long serialVersionUID = -2749075741519478796L;

    protected String name;

    protected String contentType;

    protected byte[] body;

    public MailAttachment(String name, String contentType, byte[] body) {
        this.name = name;
        this.contentType = contentType;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }
}
