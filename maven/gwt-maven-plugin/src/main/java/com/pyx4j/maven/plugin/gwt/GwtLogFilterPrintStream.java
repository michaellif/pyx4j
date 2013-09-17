/*
 * Copyright (C) 2007 pyx4j.com.
 *
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

import java.io.PrintStream;

/**
 * Created on 11-Sep-07
 */
public class GwtLogFilterPrintStream extends PrintStream {

    public GwtLogFilterPrintStream(PrintStream origStdOut) {
        super(origStdOut);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        int skip = 0;
        while (b[off + skip] == ' ') {
            skip++;
        }
        skip--;
        if (skip > 0) {
            off += skip;
            len -= skip;
        }
        super.write(b, off, len);
    }

}
