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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.tester.domain.TFile;
import com.pyx4j.tester.shared.file.TFileURLBuilder;

@SuppressWarnings("serial")
public class TFileResourceServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(TFileResourceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getPathInfo();
        String id = FilenameUtils.getPathNoEndSeparator(filename);
        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        } else {
            Key key;

            boolean thumbnail = false;
            if (id.startsWith("t")) {
                id = id.substring(1);
                thumbnail = true;
            }

            if (id.startsWith("u")) {
                TFile file = FileUploadRegistry.get(id.substring(1));
                key = file.file().blobKey().getValue();
            } else {
                key = new Key(id);
            }
            byte[] data = TFileTestStorage.retrieve(key);
            if (data == null) {
                log.debug("no such document {} {}", id, filename);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            } else {
                if (thumbnail) {
                    data = ImageThumbnailCreator.resample(data, TFileURLBuilder.THUMBNAIL_SMALL);
                }
                response.getOutputStream().write(data);
            }
        }
    }

}
