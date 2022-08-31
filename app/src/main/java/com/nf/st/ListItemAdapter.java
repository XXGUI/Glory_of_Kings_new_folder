package com.nf.st;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ListItemAdapter extends BaseAdapter implements OnClickListener {  
    private List<String>  mList;
    private Context mContext;  
    private InnerItemOnclickListener mListener;  

    public ListItemAdapter(List<String> mList, Context mContext) {
        this.mList = mList;  
        this.mContext = mContext;  
    }  

    @Override  
    public int getCount() {  
        return mList.size();
    }  

    @Override  
    public Object getItem(int position) {  
        return mList.get(position);
    }  

    @Override  
    public long getItemId(int position) {  
        return position;
    }  

    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        final ViewHolder viewHolder;  
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_h,null);
            viewHolder.name_h = ((TextView) convertView.findViewById(R.id.name_h));
            viewHolder.rename_h = (Button) convertView.findViewById(R.id.rename_h);
            viewHolder.delete_h = (Button) convertView.findViewById(R.id.delete_h);
            viewHolder.start_h = (Button) convertView.findViewById(R.id.start_h);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name_h.setOnClickListener(this);
        viewHolder.rename_h.setOnClickListener(this);
        viewHolder.delete_h.setOnClickListener(this);
        viewHolder.start_h.setOnClickListener(this);
        viewHolder.name_h.setTag(position);
        viewHolder.name_h.setText(mList.get(position));
        viewHolder.rename_h.setTag(position);
        viewHolder.delete_h.setTag(position);
        viewHolder.start_h.setTag(position);
        return convertView;
    }  

    public final static class ViewHolder {
        TextView name_h;
        Button  rename_h,delete_h,start_h;
    }

    interface InnerItemOnclickListener {  
        void itemClick(View v);  
    }  

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){  
        this.mListener=listener;  
    }  

    @Override  
    public void onClick(View v) {  
        mListener.itemClick(v);  
    }  
}  