/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 9, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.adapters;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.adapters.FileUploadBlobSecurityAdapter;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;

public class FileUploadBlobSecurityAdapterImpl implements FileUploadBlobSecurityAdapter {

    @Override
    public boolean allowModifications(IFile entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        return FileUploadRegistry.allowModifications(entity, meta, valueOrig, valueNew);
    }

}
