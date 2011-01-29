package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.client.demo.client.activity.Presenter;

/**
 * View interface. Extends IsWidget so a view impl can easily provide its container
 * widget.
 * 
 * @author drfibonacci
 */
public interface HelloView extends IsWidget {
    void setName(String helloName);

    void setPresenter(Presenter listener);

}