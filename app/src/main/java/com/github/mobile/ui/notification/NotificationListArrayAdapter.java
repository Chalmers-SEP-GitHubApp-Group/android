package com.github.mobile.ui.notification;


import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.mobile.R;

import java.util.Date;
import java.util.List;

public class NotificationListArrayAdapter extends ArrayAdapter<Notification> /*implements NotificationListener*/{
    private final Context context;
    private static String TAG = "NotificationListArrayAdapter";
    private List<Notification>  notificationList;

    public NotificationListArrayAdapter(Context context, List<Notification> notificationList) {
        super(context, R.layout.notification_list, R.id.notification_item_message, notificationList);
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notification_list_item, parent, false);

        TextView titleView = (TextView) rowView.findViewById(R.id.notification_item_title);
        TextView dateView = (TextView) rowView.findViewById(R.id.notification_item_date);
        TextView messageView = (TextView) rowView.findViewById(R.id.notification_item_message);

        Notification notification = notificationList.get(position);

        if(notification.hasBeenViewed()){
            View circleView = (View) rowView.findViewById(R.id.notification_item_circle);
            circleView.setVisibility(View.INVISIBLE);
        }
        titleView.setText(notification.getContentTitle());
        messageView.setText(notification.getContentText());
        Date date = notification.getDate();
        String formatedDate = DateFormat.getDateFormat(context).format(date) + " " + DateFormat.getTimeFormat(context).format(date);
        dateView.setText(formatedDate);
        return rowView;
    }

    public void setNotification(List<Notification> list){
        this.notificationList.clear();
        this.notificationList.addAll(list);
        Log.d(TAG, "adapter update!");
        notifyDataSetChanged();
    }


    public Notification getNotification(int position){
        return notificationList.get(position);
    }

}
