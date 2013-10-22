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
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.upload;

import java.util.Collection;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;

/**
 * The server side part of UploadService (Synchronous as opposite to Async)
 * 
 * @param <U>
 * @param <R>
 */
public interface UploadReciver<U extends IEntity, R extends IFile> {

    /**
     * @return Maximum size of a single uploaded file.
     */
    long getMaxSize();

    /**
     * Must return LowerCase strings, no dot in front
     */
    Collection<String> getSupportedExtensions();

    /**
     * Used for generic error messages
     */
    String getUploadFileTypeName();

    /**
     * Validate binary file type before we start receiving it to memory.
     * 
     * @param fileName
     * @param contentMimeType
     */
    public void onUploadStarted(String fileName, String contentMimeType);

    /**
     * This is called by UploadServlet and in run calls onUploadReceived
     * 
     * @param uploadInitiationData
     *            Data sent from UI when starting upload
     * @param uploadedData
     * 
     * @return created IFile response
     */
    public R onUploadReceived(U uploadInitiationData, UploadedData uploadedData);

}
