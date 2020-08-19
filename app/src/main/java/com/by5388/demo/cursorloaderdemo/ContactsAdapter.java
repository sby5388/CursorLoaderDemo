package com.by5388.demo.cursorloaderdemo;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * @author Administrator  on 2020/8/19.
 */
public class ContactsAdapter extends CursorAdapter {
    public static final String TAG = "ContactsAdapter";

    public ContactsAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            Log.e(TAG, "bindView: cursor == null");
            return;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        final String name = cursor.getString(IContactsApi.INDEX_NAME);
        holder.bind(name);
    }

    private static class ViewHolder {
        private final TextView mTextView;

        public ViewHolder(View view) {
            mTextView = (TextView) view;
        }

        void bind(String name) {
            if (TextUtils.isEmpty(name)) {
                Log.e(TAG, "bind: name == null ");
                return;
            }
            mTextView.setText(name);
        }

    }
}
