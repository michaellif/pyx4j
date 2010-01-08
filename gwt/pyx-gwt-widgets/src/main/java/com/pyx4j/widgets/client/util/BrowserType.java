/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.util;

import com.google.gwt.core.client.GWT;

public class BrowserType {

    public enum Browser {
        UNKNOWN, IE, MOZILLA, SAFARI, OPERA, IPHONE
    };

    public static native boolean isIENative() /*-{ return ($doc.body.insertAdjacentHTML != null); }-*/;

    public native static boolean isFirefoxNative() /*-{ var agt= $wnd.navigator.userAgent.toLowerCase(); return (agt.indexOf("firefox") != -1); }-*/;

    public native static String getUserAgent() /*-{ return $wnd.navigator.userAgent; }-*/;

    public static final boolean isFirefox() {
        return (impl.getType() == Browser.MOZILLA);
    }

    public static final boolean isIE() {
        return (impl.getType() == Browser.IE);
    }

    public static final Browser getType() {
        return impl.getType();
    }

    public static final String getCompiledType() {
        return impl.getCompiledType();
    }

    private static interface Impl {

        Browser getType();

        String getCompiledType();
    }

    private static final Impl impl = GWT.create(Impl.class);

    @SuppressWarnings("unused")
    private static class ImplUnknown implements Impl {

        Browser type;

        @Override
        public final Browser getType() {
            if (type == null) {
                // Does other detection
                if (isIENative()) {
                    type = Browser.IE;
                } else {
                    type = Browser.UNKNOWN;
                }
            }
            return type;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#UNKNOWN";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplMozilla implements Impl {

        @Override
        public final Browser getType() {
            return Browser.MOZILLA;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#MOZILLA";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplIE6 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.IE;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#IE6";
        }
    }
    
    @SuppressWarnings("unused")
    private static class ImplIE8 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.IE;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#IE8";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplSafari implements Impl {

        @Override
        public final Browser getType() {
            return Browser.SAFARI;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#SAFARI";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplOpera implements Impl {

        @Override
        public final Browser getType() {
            return Browser.OPERA;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#OPERA";
        }
    }
}
