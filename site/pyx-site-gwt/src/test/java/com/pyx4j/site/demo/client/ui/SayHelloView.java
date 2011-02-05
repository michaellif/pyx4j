package com.pyx4j.site.demo.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface SayHelloView extends IsWidget {

  public void setPresenter(Presenter presenter);
  
  
  public interface Presenter {
    public void sayGoodbye();
  }
  
}
