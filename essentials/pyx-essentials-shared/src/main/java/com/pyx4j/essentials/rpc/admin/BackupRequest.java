/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-08-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.admin;

import java.io.Serializable;

public class BackupRequest implements Serializable {

    private static final long serialVersionUID = 2094335560058692423L;

    public static final int DEFAULT_BATCH_SIZE = 25;

    private String entityKind;

    private String encodedCursorRefference;

    private int responceSize = DEFAULT_BATCH_SIZE;

    public BackupRequest() {

    }

    public String getEntityKind() {
        return entityKind;
    }

    public void setEntityKind(String entityKind) {
        this.entityKind = entityKind;
    }

    public String getEncodedCursorRefference() {
        return encodedCursorRefference;
    }

    public void setEncodedCursorRefference(String encodedCursorRefference) {
        this.encodedCursorRefference = encodedCursorRefference;
    }

    public int getResponceSize() {
        return responceSize;
    }

    public void setResponceSize(int responceSize) {
        this.responceSize = responceSize;
    }
}
