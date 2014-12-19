/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 28, 2010
 * @author michaellif
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.IFileURLBuilder;

public class CFile extends CField<IFile<?>, NFile> {

    private IFormatter<IFile<?>, String> format;

    private final UploadService<?, ?> service;

    private IFileURLBuilder fileUrlBuilder;

    public CFile(UploadService<?, ?> service, IFileURLBuilder fileURLBuilder) {
        super();
        this.service = service;
        this.fileUrlBuilder = fileURLBuilder;

        setNativeComponent(new NFile(this));

        setFormat(new IFormatter<IFile<?>, String>() {
            @Override
            public String format(IFile<?> value) {
                return value.fileName().getStringView();
            }
        });
    }

    public void setFormat(IFormatter<IFile<?>, String> format) {
        this.format = format;
    }

    public IFormatter<IFile<?>, String> getFormat() {
        return format;
    }

    public String getImageUrl() {
        IFile<?> file = getValue();
        if (file == null || file.isNull() || fileUrlBuilder == null) {
            return null;
        } else {
            return fileUrlBuilder.getUrl(file);
        }
    }

    UploadService<?, ?> getUploadService() {
        return service;
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || !getValue().hasValues();
    }

}