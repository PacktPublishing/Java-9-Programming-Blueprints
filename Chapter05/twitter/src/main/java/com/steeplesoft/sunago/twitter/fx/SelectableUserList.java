/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.sunago.twitter.fx;

import com.steeplesoft.sunago.api.fx.SelectableItem;
import twitter4j.UserList;

/**
 *
 * @author jason
 */
public class SelectableUserList extends SelectableItem<UserList> {

    public SelectableUserList(UserList item) {
        super(item);
    }

    @Override
    public String toString() {
        return getItem().getSlug();
    }
}
