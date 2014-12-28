package se.merryweather.callerlookup.app;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import se.merryweather.callerlookup.app.Business.CompanyLookup;
import se.merryweather.callerlookup.app.Business.PersonLookup;
import se.merryweather.callerlookup.app.Interfaces.IOnTaskCompleted;
import se.merryweather.callerlookup.app.Models.DetailsModel;

public class PhoneReciever extends BroadcastReceiver implements IOnTaskCompleted {

    private Context mContext;
    private List<AsyncTask> mRunningTasks = new ArrayList<AsyncTask>();
    private String savedIncomingNumber;
    private View overlay;

    public void onReceive(Context context, Intent intent) {
        this.mContext = context;

        mRunningTasks.add(new PersonLookup(mContext, PhoneReciever.this));
        mRunningTasks.add(new CompanyLookup(mContext, PhoneReciever.this));

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void onTaskCompleted(Object info) {
        DetailsModel parsedInfo = (DetailsModel)info;

        if(parsedInfo.haveResult()) {
            this.AddOverlay(parsedInfo);
        }
        else {
            AsyncTask task = GetNextAsyncTask();
            if(task != null) {
                task.execute(savedIncomingNumber);
            }
        }
    }

    private void AddOverlay(DetailsModel personInfo) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.activity_popup, null);
        TextView nameView = (TextView)view.findViewById(R.id.Result_name);

        if(personInfo.getName() == null || personInfo.getName().isEmpty()) {
            nameView.setVisibility(View.GONE);
        }
        nameView.setText(personInfo.getName());

        TextView numberView = (TextView)view.findViewById(R.id.Result_number);
        numberView.setText(savedIncomingNumber);

        TextView addressView = (TextView)view.findViewById(R.id.Result_address);

        if(personInfo.getAddress() == null || personInfo.getAddress().isEmpty()) {
            addressView.setVisibility(View.GONE);
        }

        addressView.setText(personInfo.getAddress());

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP;
        overlay = view;
        wm.addView(view, params);
    }

    private void RemoveOverlay() {
       WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
       try {
           wm.removeView(overlay);
       }
       catch (Exception ex) {
            ex.printStackTrace();
       }
    }

    private AsyncTask GetNextAsyncTask() {
        for (AsyncTask task : mRunningTasks) {
            if(task.getStatus().equals(AsyncTask.Status.PENDING)) {
                return task;
            }
        }
        return null;
    }

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    RemoveOverlay();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if(numberInPhonebook(incomingNumber) == false) {
                        this.StartLookup(incomingNumber);
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

        private Boolean numberInPhonebook(String incomingNumber) {
            String contactId = "";
            ContentResolver mResolver = mContext.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
            Cursor cursor = mResolver.query(uri, new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID }, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                }
                while (cursor.moveToNext());
            }
            cursor.close();

            if(!contactId.equals("")) {
                return true;
            }
            return false;
        }

        private void StartLookup(String incomingNumber) {
            savedIncomingNumber = incomingNumber;
            AsyncTask task = GetNextAsyncTask();
            if(task != null) {
                task.execute(incomingNumber);
            }
        }
    };
}