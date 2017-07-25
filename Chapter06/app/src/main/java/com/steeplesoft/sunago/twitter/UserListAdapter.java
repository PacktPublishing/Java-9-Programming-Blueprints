package com.steeplesoft.sunago.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.Sunago;
import com.steeplesoft.sunago.SunagoUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.UserList;

public class UserListAdapter extends ArrayAdapter<UserList> {
    private List<UserListModel> userLists;

    public UserListAdapter(Context context, int textViewResourceId,
                           List<UserList> userLists) {
        super(context, textViewResourceId, userLists);

        Set<String> selected = SunagoUtil.getPreferences().getStringSet(
                Sunago.getAppContext().getString(R.string.twitter_selected_lists), new HashSet<>());

        this.userLists = new ArrayList<>();
        for (UserList userList : userLists) {
            UserListModel model = new UserListModel(userList);
            model.checked = selected.contains(String.valueOf(userList.getId()));
            this.userLists.add(model);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) Sunago.getAppContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.user_list_info, null);

            holder = new ViewHolder();
            holder.userList = (TextView) convertView.findViewById(R.id.userList);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);

            ViewHolder finalHolder = holder;
            View.OnClickListener clickListener = new UserListClickListener(finalHolder);
            holder.checkbox.setOnClickListener(clickListener);
            holder.userList.setOnClickListener(clickListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserListModel model = userLists.get(position);
        holder.userList.setText(model.userList.getName());
        holder.checkbox.setChecked(model.checked);
        holder.model = model;

        return convertView;

    }

    private class UserListModel {
        UserList userList;
        boolean checked = false;
        public UserListModel(UserList userList) {
            this.userList = userList;
        }
    }

    private class ViewHolder {
        TextView userList;
        CheckBox checkbox;
        UserListModel model;
    }

    private class UserListClickListener implements View.OnClickListener {
        private final ViewHolder finalHolder;

        public UserListClickListener(ViewHolder finalHolder) {
            this.finalHolder = finalHolder;
        }

        public void onClick(View v) {
            finalHolder.model.checked = !finalHolder.model.checked;
            finalHolder.checkbox.setChecked(finalHolder.model.checked);

            // Make a copy of the set to work around an Android bug: https://code.google.com/p/android/issues/detail?id=27801#c2
            final Set<String> selected = new HashSet<>(SunagoUtil.getPreferences().getStringSet(
                    Sunago.getAppContext().getString(R.string.twitter_selected_lists), new HashSet<>()));
            final SharedPreferences.Editor editor = SunagoUtil.getPreferences().edit();

            if (finalHolder.model.checked) {
                selected.add(String.valueOf(finalHolder.model.userList.getId()));
            } else {
                selected.remove(String.valueOf(finalHolder.model.userList.getId()));
            }
            editor.putStringSet(Sunago.getAppContext().getString(R.string.twitter_selected_lists), selected);
            editor.commit();
        }
    }
}