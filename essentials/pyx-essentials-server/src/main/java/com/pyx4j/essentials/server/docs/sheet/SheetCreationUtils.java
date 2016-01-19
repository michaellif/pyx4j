/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jan 18, 2016
 * @author vlads
 */
package com.pyx4j.essentials.server.docs.sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.gwt.server.IOUtils;

public class SheetCreationUtils {

    public static <E extends IEntity> void createEntityReportFile(String fileName, List<E> reportModels) {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
        @SuppressWarnings("unchecked")
        Class<? extends E> entityClass = (Class<? extends E>) reportModels.get(0).getInstanceValueClass();
        EntityReportFormatter<E> entityFormatter = new EntityReportFormatter<E>(entityClass);
        entityFormatter.createHeader(formatter);
        entityFormatter.reportAll(formatter, reportModels);

        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formatter.getBinaryData());
            out.flush();
        } catch (Throwable e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
