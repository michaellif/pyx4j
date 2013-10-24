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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.FileURLBuilder;

public class CFile<E extends IFile> extends CField<E, NFile<E>> {

    private IFormat<E> format;

    private final UploadService<?, E> service;

    private FileURLBuilder<E> fileUrlBuilder;

    public CFile(UploadService<?, E> service, FileURLBuilder<E> fileURLBuilder) {
        super("");
        this.service = service;
        this.fileUrlBuilder = fileURLBuilder;

        setNativeWidget(new NFile<E>(this));

        setNavigationCommand(new Command() {

            @Override
            public void execute() {
                Window.open(fileUrlBuilder.getUrl(getValue()), "_blank", null);
            }
        });

        setFormat(new IFormat<E>() {
            @Override
            public String format(E value) {
                return value.fileName().getStringView();
            }

            @Override
            public E parse(String string) {
                return getValue();
            }
        });

    }

    public void setFormat(IFormat<E> format) {
        this.format = format;
    }

    public IFormat<E> getFormat() {
        return format;
    }

    public String getImageUrl(E file) {
        if (file == null || file.isNull() || fileUrlBuilder == null) {
            return null;
        } else {
            return fileUrlBuilder.getUrl(file);
        }
    }

    UploadService<?, E> getUploadService() {
        return service;
    }
}