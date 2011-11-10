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
 * Created on Oct 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.shared;

import com.pyx4j.config.shared.ApplicationMode;

/**
 * This exception should be excluded form serialization policy for production version of GWT application.
 * 
 */
@SuppressWarnings("serial")
public class DevInfoUnRecoverableRuntimeException extends UnRecoverableRuntimeException {

    protected DevInfoUnRecoverableRuntimeException() {

    }

    public DevInfoUnRecoverableRuntimeException(String message) {
        super(ApplicationMode.DEV + message);
    }

    public DevInfoUnRecoverableRuntimeException(Throwable cause) {
        super(ApplicationMode.DEV + ((cause.getMessage() != null) ? cause.getMessage() : "") + ", Throwable " + cause.getClass().toString());
    }

    public DevInfoUnRecoverableRuntimeException(String message, Throwable cause) {
        super(ApplicationMode.DEV + message + " " + ((cause.getMessage() != null) ? cause.getMessage() : "") + ", Throwable " + cause.getClass().toString());
    }
}
