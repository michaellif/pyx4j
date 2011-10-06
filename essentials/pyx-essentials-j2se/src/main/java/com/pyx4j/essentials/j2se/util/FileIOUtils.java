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
 * Created on Aug 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

import com.pyx4j.gwt.server.IOUtils;

public class FileIOUtils {

    public static void writeToFile(File file, String data) {
        Writer w = null;
        try {
            w = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
            w.write(data);
            w.flush();
        } catch (IOException e) {
            throw new RuntimeException("File " + file.getAbsolutePath() + " write error", e);
        } finally {
            IOUtils.closeQuietly(w);
        }
    }

    public static String loadTextFile(File file) {
        BufferedReader br = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("File " + file.getAbsolutePath() + " read error", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    public static byte[] loadBinary(File file, int bufferSize) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            IOUtils.copyStream(in, b, bufferSize);
            return b.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("File " + file.getAbsolutePath() + " read error", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(b);
        }
    }

    public static File findFileIgnoreCase(File file) {
        File dir = file.getParentFile();
        if (!dir.isDirectory() || !dir.exists()) {
            return file;
        }
        String name = file.getName().toLowerCase(Locale.ENGLISH);
        for (File f : dir.listFiles()) {
            if (name.equals(f.getName().toLowerCase(Locale.ENGLISH))) {
                return f;
            }
        }
        return file;
    }
}
