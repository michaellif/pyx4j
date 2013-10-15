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
package com.pyx4j.gwt.rpc.upload;

import java.io.Serializable;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;

@SuppressWarnings("serial")
@Deprecated
public class UploadResponse<E extends IEntity> implements Serializable {

    @Deprecated
    public String fileName;

    /**
     * Size in bytes
     */
    @Deprecated
    public int fileSize;

    /**
     * Server side time when we got the file
     */
    @Deprecated
    public long timestamp;

    /**
     * Optional, If new process is started to handle the data received
     */
    public String processingDeferredCorrelationId;

    /**
     * Optional
     */
    @Deprecated
    public String fileContentType;

    /**
     * Optional Id if created by server. Can be null;
     */
    @Deprecated
    public Key uploadKey;

    /**
     * Optional message sent by the server. Can be null;
     */
    public String message;

    public E data;

}
