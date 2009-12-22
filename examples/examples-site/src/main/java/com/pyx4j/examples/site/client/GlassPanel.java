package com.pyx4j.examples.site.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Created on 18-Sep-06
 * 
 * Block access to GUI elemends while service is running.
 */
public class GlassPanel extends SimplePanel implements ResizeHandler {

    private static GlassPanel instance;
    
    private static int showRequestCount;
    
    protected GlassPanel () {
        super(DOM.createDiv());
        Window.addResizeHandler(this);
        DOM.setInnerHTML(getElement(), "<table style=\"width: 100%;height: 100%;\"><tr><td>&nbsp;</td></tr></table>");
	        setSize("100%", "100%");
	        
	        DOM.setStyleAttribute(getElement(), "left", "0px");
	        DOM.setStyleAttribute(getElement(), "top", "0px");
	        DOM.setStyleAttribute(getElement(), "position", "absolute");
	        DOM.setStyleAttribute(getElement(), "zIndex", "-10");
	        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
    }
    
    public static GlassPanel instance() {
        if (instance == null) {
            instance = new GlassPanel();
        }
        return instance;
    }
    
    public static void show() {
        showRequestCount++;
        if (showRequestCount == 1) {
            instance().setPixelSize(Window.getClientWidth(), Window.getClientHeight());
    		DOM.setStyleAttribute(instance.getElement(), "zIndex", "10");
            DOM.setStyleAttribute(instance.getElement(), "cursor", "wait");
        }
    }

    public static boolean isShown() {
        return (showRequestCount > 0);
    }
    
    public static void hide() {
        showRequestCount--;
        assert showRequestCount > -1;
        if (showRequestCount == 0) {
            DOM.setStyleAttribute(instance.getElement(), "cursor", "move");
        	DOM.setStyleAttribute(instance.getElement(), "zIndex", "-10");
        }
    }

	@Override
	public void onResize(ResizeEvent event) {
        instance().setPixelSize(Window.getClientWidth(), Window.getClientHeight());
	}

}