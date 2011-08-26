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
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.upload;

import java.io.Serializable;

import com.pyx4j.commons.Key;

@SuppressWarnings("serial")
public class UploadResponse implements Serializable {

    public String fileName;

    /**
     * Size in bytes
     */
    public int fileSize;

    /**
     * Optional, If new process is started to handle the data received
     */
    public String processingDeferredCorrelationId;

    /**
     * Optional
     */
    public String fileContentType;

    /**
     * Optional Id if created by server. Can be null;
     */
    public Key uploadKey;

    /**
     * Optional message sent by the server. Can be null;
     */
    public String message;

}
