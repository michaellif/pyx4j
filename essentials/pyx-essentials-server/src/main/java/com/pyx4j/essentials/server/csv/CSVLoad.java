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
 * Created on 2010-04-30
 * @author vlads
 */
package com.pyx4j.essentials.server.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

import com.pyx4j.gwt.server.IOUtils;

public class CSVLoad {

    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    private static final String UTF16_BOM = "\uFEFF";

    private static final String UTF8_BOM = "\uEFBBBF";

    private static final String UTF8_BOM_InCp1252 = "﻿";

    public static int findHeaderLine(String[][] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].startsWith("#")) {
                continue;
            }
            return i;
        }
        return 0;
    }

    public static int findHeaderColumn(String[][] data, int header, String name) {
        for (int col = 0; (col < data[header].length); col++) {
            if (data[header][col].equals(name)) {
                return col;
            }
        }
        return -1;
    }

    public static int findHeaderColumn(String[] data, String name) {
        for (int col = 0; (col < data.length); col++) {
            if (data[col].equals(name)) {
                return col;
            }
        }
        return -1;
    }

    public static void loadResourceFile(String resourceName, Charset charset, CSVReceiver reciver) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new RuntimeException("Resouce [" + resourceName + "] not found");
        }
        loadFile(is, charset, new CSVParser(), reciver);
    }

    public static void loadFile(InputStream is, TextParser parser, CSVReceiver reciver) {
        loadFile(is, Charset.forName("Cp1252"), parser, reciver);
    }

    public static void loadFile(InputStream in, Charset charset, TextParser parser, CSVReceiver reciver) {
        int lineNumber = 0;
        try {
            BufferedReader reader = new BufferedReader((charset == null) ? new InputStreamReader(in) : new InputStreamReader(in, charset));
            String line = null;
            boolean header = true;

            while (((line = reader.readLine()) != null) && (reciver.canContuneLoad())) {
                lineNumber++;
                if (lineNumber == 1) {
                    line = removeUTF8BOM(line, charset);
                }
                String[] values = parser.parse(line);
                if (values == null) {
                    continue;
                }
                if (header) {
                    if (reciver.onHeader(values)) {
                        header = false;
                    }
                } else {
                    reciver.onRow(values);
                }
            }
            reader.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Load file error", ioe);
        } catch (Exception e) {
            throw new RuntimeException("Load file error, Line# " + lineNumber + "; " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private static String removeUTF8BOM(String line, Charset charset) {
        if (charset == null && line.startsWith(UTF8_BOM_InCp1252)) {
            line = line.substring(3);
        } else if (line.startsWith(UTF16_BOM) && (charset.equals(StandardCharsets.UTF_16))) {
            line = line.substring(1);
        } else if (line.startsWith(UTF8_BOM) && (charset.equals(StandardCharsets.UTF_8))) {
            line = line.substring(3);
        } else if (line.startsWith(UTF8_BOM_InCp1252) && (charset.equals(Charset.forName("Cp1252")))) {
            line = line.substring(3);
        }
        return line;
    }

    public static String[] loadUTF8File(String resourceName, final String columName) {
        return loadFile(resourceName, StandardCharsets.UTF_8, columName);
    }

    public static String[] loadFile(String resourceName, final String columName) {
        return loadFile(resourceName, Charset.forName("Cp1252"), columName);
    }

    public static String[] loadFile(String resourceName, Charset charset, final String columName) {
        final List<String> data = new Vector<String>();
        loadResourceFile(resourceName, charset, new CSVReceiver() {

            int columnIndex = 0;

            @Override
            public boolean canContuneLoad() {
                return (columnIndex >= 0);
            }

            @Override
            public boolean onHeader(String[] header) {
                columnIndex = findHeaderColumn(header, columName);
                return (columnIndex != -1);
            }

            @Override
            public void onRow(String[] value) {
                data.add(value[columnIndex]);
            }
        });
        return data.toArray(new String[data.size()]);
    }
}
