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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.rpc.upload.UploadResponse;

public interface UploadReciver<U extends IEntity, R extends IEntity> {

    /**
     * @return Maximum size of a single uploaded file.
     */
    long getMaxSize();

    String getUploadFileTypeName();

    public void onUploadStart(String fileName);

    public enum ProcessingStatus {

        completed,

        processWillContinue
    }

    public ProcessingStatus onUploadRecived(UploadData data, UploadDeferredProcess<U, R> process, UploadResponse<R> response);

}
