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
 */
package com.propertyvista.common.client;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.gwt.shared.IFileURLBuilder;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class VistaFileURLBuilder implements IFileURLBuilder {

    private final String fileClassName;

    public VistaFileURLBuilder(Class<? extends IHasFile<?>> fileClass) {
        fileClassName = GWTJava5Helper.getSimpleName(fileClass);
    }

    @Override
    public String getUrl(IFile<?> file) {
        if (!file.accessKey().isNull()) {
            return getUrl(DeploymentConsts.TRANSIENT_FILE_PREF + file.accessKey().getStringView(), file.fileName().getValue());
        } else if (file.getOwner().id().isNull() || file.blobKey().isNull()) {
            return null;
        } else {
            return getUrl(file.getOwner().id().getStringView(), file.fileName().getValue());
        }
    }

    protected String getUrl(String fileId, String fileName) {
        return GWT.getModuleBaseURL() + DeploymentConsts.FILE_SERVLET_MAPPING + fileClassName + "/" + fileId + "/" + fileName;
    }
}
