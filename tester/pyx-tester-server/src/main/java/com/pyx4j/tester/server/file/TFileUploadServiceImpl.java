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
 * Created on 2012-12-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.server.file;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.tester.domain.TFileBlob;
import com.pyx4j.tester.shared.file.TFileUploadService;

public class TFileUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, TFileBlob> implements TFileUploadService {

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG);

    public TFileUploadServiceImpl() {
    }

    @Override
    public long getMaxSize() {
        return 5 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public String getUploadFileTypeName() {
        return "Image";
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<TFileBlob> response) {
        response.blobKey()
                .setValue(TFileTestStorage.persist(uploadedData.binaryContent, response.fileName().getValue(), response.contentMimeType().getValue()));
    }
}
