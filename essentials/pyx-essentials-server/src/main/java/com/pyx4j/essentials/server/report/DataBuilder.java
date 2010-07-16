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
 * Created on 2010-05-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.Charset;

public class DataBuilder implements Externalizable {

    protected transient StringBuilder data;

    private static Charset utf8_charset = Charset.forName("UTF-8");

    public DataBuilder() {
        data = new StringBuilder();
    }

    public DataBuilder append(char paramChar) {
        data.append(paramChar);
        return this;
    }

    public DataBuilder append(String paramString) {
        data.append(paramString);
        return this;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        data = new StringBuilder();
        int length = in.readInt();
        byte[] b = new byte[length];
        in.readFully(b);
        data.append(new String(b, utf8_charset));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        byte[] b = data.toString().getBytes(utf8_charset);
        out.writeInt(b.length);
        out.write(b);
    }

    public byte[] getBinaryData() {
        return data.toString().getBytes(utf8_charset);
    }

    public int getBinaryDataSize() {
        return data.length();
    }

}
