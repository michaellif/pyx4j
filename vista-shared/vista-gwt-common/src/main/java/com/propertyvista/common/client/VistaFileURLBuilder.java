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
package com.propertyvista.common.client;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.shared.IFileURLBuilder;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class VistaFileURLBuilder<FILE extends IFile> implements IFileURLBuilder<FILE> {

    private final String servletMapping;

    public VistaFileURLBuilder(String servletMapping) {
        this.servletMapping = servletMapping;
    }

    @Override
    public String getUrl(FILE image) {
        if (image.id().isNull()) {
            return getUrl(DeploymentConsts.TRANSIENT_FILE_PREF + image.accessKey().getStringView(), "blob");
        } else {
            return getUrl(image.blobKey().getStringView(), image.fileName().getValue());
        }
    }

    protected String getUrl(String blobId, String fileName) {
        return ClientNavigUtils.getDeploymentBaseURL() + servletMapping + blobId + "/" + fileName;
    }
}
