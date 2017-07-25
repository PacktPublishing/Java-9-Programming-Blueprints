package com.steeplesoft.sunago.api.fx;

import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author jason
 */
public abstract class SelectableItem<T> {
    private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
    private final T item;
    public SelectableItem(T item) {
        this.item = item;
    }
    public T getItem() {
        return item;
    }
    public SimpleBooleanProperty getSelected() {
        return selected;
    }
}
