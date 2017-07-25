package com.steeplesoft.sunago;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.steeplesoft.sunago.data.SunagoContentProvider;

import java.io.InputStream;
import java.net.URL;

public class SunagoCursorAdapter extends CursorAdapter {
    private int INDEX_IMAGE;
    private int INDEX_BODY;

    public SunagoCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.social_media_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.body = (TextView) view.findViewById(R.id.textView);
        viewHolder.image = (ImageView) view.findViewById(R.id.imageView);

        WindowManager wm = (WindowManager) Sunago.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        viewHolder.image.getLayoutParams().width = (int) Math.round(size.x * 0.33);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        String image = cursor.getString(INDEX_IMAGE);
        if (image != null) {
            new DownloadImageTask(viewHolder.image).execute(image);
        } else {
            viewHolder.image.setImageBitmap(null);
            viewHolder.image.setVisibility(View.GONE);
        }
        viewHolder.body.setText(cursor.getString(INDEX_BODY));
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            INDEX_BODY = newCursor.getColumnIndex(SunagoContentProvider.BODY);
            INDEX_IMAGE = newCursor.getColumnIndex(SunagoContentProvider.IMAGE);
        }
        return super.swapCursor(newCursor);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap image = null;
            try (InputStream in = new URL(urls[0]).openStream()) {
                image = BitmapFactory.decodeStream(in);
            } catch (java.io.IOException e) {
                Log.e("Error", e.getMessage());
            }
            return image;
        }

            @Override
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
                imageView.setVisibility(View.VISIBLE);
                imageView.getParent().requestLayout();
            }
    }

    private static class ViewHolder {
        public TextView body;
        public ImageView image;
    }
}