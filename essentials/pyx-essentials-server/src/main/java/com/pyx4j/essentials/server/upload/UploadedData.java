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
 * Created on Aug 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.upload;

public class UploadedData {

    /**
     * The data from HTTP MultipartContent Post, Received from prepareUploadProcess
     */
    public String deferredCorrelationId;

    /**
     * Returns the original filename in the client's filesystem, as provided by the browser. (No path from Opera, Filename only)
     */
    public String fileName;

    /**
     * Server side time when we got the file
     */
    public long timestamp;

    public String contentMimeType;

    /**
     * Size in bytes
     */
    public int binaryContentSize;

    /**
     * HTTP MultipartContent Post binary data (file context)
     */
    public byte[] binaryContent;

}
