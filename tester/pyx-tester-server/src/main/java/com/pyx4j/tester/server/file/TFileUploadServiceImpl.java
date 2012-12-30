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

import com.pyx4j.essentials.server.upload.AbstractFileUploadServiceImpl;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.tester.domain.TFile;
import com.pyx4j.tester.shared.file.TFileUploadService;

public class TFileUploadServiceImpl extends AbstractFileUploadServiceImpl<TFile> implements TFileUploadService {

    public TFileUploadServiceImpl() {
        super(TFile.class);
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
    protected TFile fileUploadRecived(byte[] data, TFile uploadData) {
        uploadData.blobKey().setValue(TFileTestStorage.persist(data, uploadData.fileName().getValue(), uploadData.contentMimeType().getValue()));
        return uploadData;
    }

}
