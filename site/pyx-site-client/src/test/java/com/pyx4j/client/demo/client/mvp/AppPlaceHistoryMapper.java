package com.pyx4j.client.demo.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.place.HelloPlace;

/**
 * PlaceHistoryMapper interface is used to attach all places which the PlaceHistoryHandler
 * should be aware of. This is done via the @WithTokenizers annotation or by extending
 * PlaceHistoryMapperWithFactory and creating a separate TokenizerFactory.
 */
@WithTokenizers({ HelloPlace.Tokenizer.class, GoodbyePlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
