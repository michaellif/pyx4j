/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Mar 31, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.rpc;

import java.io.Serializable;

public class SystemWallMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private boolean isWarning;

    public SystemWallMessage() {

    }

    public SystemWallMessage(String message, boolean isWarning) {
        this.message = message;
        this.isWarning = isWarning;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String systemWallMessage) {
        this.message = systemWallMessage;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void setWarning(boolean systemWallMessageIsWarning) {
        this.isWarning = systemWallMessageIsWarning;
    }

}
