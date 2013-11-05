
package com.adam.app.customgallery;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private String[] date;
    private ImageAdapter imageAdapter;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        
        int image_colum_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[count];
        this.arrPath = new String[count];
        this.date = new String[count];
        this.thumbnailsselection = new boolean[count];
        
        for(int i = 0; i < count; i++) {
            imagecursor.moveToPosition(i);
            
            int id = imagecursor.getInt(image_colum_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int dateColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            thumbnails[i] =  MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, 
                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            
            arrPath[i] = imagecursor.getString(dataColumnIndex);
            date[i] = imagecursor.getString(dateColumnIndex);
        }
        
        GridView imagegrid = (GridView)this.findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imagecursor.close();
        
        final Button selectBtn = (Button)this.findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                
                for(int i = 0; i < len; i++) {
                    if(thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                
                if(cnt == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "You've selected Total " + cnt + " image(s).",
                            Toast.LENGTH_LONG).show();
                }
                
            }
            
        });
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        
        
        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return count;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            
            if(convertView == null) {
                holder = new ViewHolder();
                
                convertView = mInflater.inflate(
                        R.layout.galleryitem, null);
                
                holder.imageView = (ImageView)convertView.findViewById(R.id.thumbImage);
                holder.checkBox = (CheckBox)convertView.findViewById(R.id.itemCheckBox);
                holder.imageText = (TextView)convertView.findViewById(R.id.ImageText);
                
                convertView.setTag(holder);
            }
            else {
                
                holder = (ViewHolder)convertView.getTag();
            }
            
            holder.imageView.setId(position);
            holder.checkBox.setId(position);
            
            holder.checkBox.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox)v;
                    
                    int id = cb.getId();
                    
                    if(thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    }
                    else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true; 
                    }
                }
                
            });
            
            holder.imageView.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int id = v.getId();
                    
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://"+arrPath[id]), "image/*");
                    startActivity(intent);
                }
                
            });
            
            holder.imageView.setImageBitmap(thumbnails[position]);
            holder.checkBox.setChecked(thumbnailsselection[position]);
            holder.imageText.setText(date[position]);
            holder.id = position;
            return convertView;
        }
        
    }
    
    public class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        TextView imageText;
        int id;
    }
    
}
