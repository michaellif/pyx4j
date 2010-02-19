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
 * Created on Feb 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtils {

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (Throwable e) {
        }
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (Throwable e) {
        }
    }

    public static void copyStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int bytesRead = 0;
        while (true) {
            bytesRead = in.read(buffer);
            if (bytesRead >= 0) {
                out.write(buffer, 0, bytesRead);
            } else {
                break;
            }
        }
    }

    public static byte[] getResource(String name) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            IOUtils.copyStream(in, os, 1024);
            return os.toByteArray();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(os);
        }
    }

    public static String getTextResource(String name) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (in == null) {
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            br.close();
        }

        return sb.toString();
    }

}
