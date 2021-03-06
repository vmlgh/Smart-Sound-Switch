package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

public class ACSettingsAct extends AppCompatActivity implements View.OnClickListener, SwipeMenuListView.OnMenuItemClickListener {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_Calender = 113;
    ACLocationAdapter pLocationAdapter;

    public Intent mServiceIntent;
    public SensorService mSensorService;

    /*Context ctx;

    public Context getCtx()
    {
        return ctx;
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("ACSettingsAct", "OnCreate");

        super.onCreate(savedInstanceState);

        //this.ctx = this;

        i.helper.hideTitleAndTitleBar(this);

        setContentView(R.layout.activity_acsettings);

        i.helper.changeStatusBarColor(this, ACSettingsAct.this);
        this.setHeader();
        //this.setListviewHeader();
        this.setPermissionsToCreateDirectories();
        setPermissionToLocation();
        setPermisionsToCalender();

        Button btn_addLocation = (Button)findViewById(R.id.btn_addLocation);
        btn_addLocation.setOnClickListener(this);

        if (i.helper.isDebug)
        {
            Button btn_temp = (Button) findViewById(R.id.btn_temp);
            btn_temp.setOnClickListener(this);
            btn_temp.setVisibility(View.VISIBLE);
        }

        if (i.helper.locationsListUser != null)
        {
            i.helper.allData.readPlist();
        }

        i.helper.updateCategories();
        i.helper.updateVolumeRanges();

        ACFavourites pACFavourites = new ACFavourites(this);
        pACFavourites.updatePermissionsForContactsAndPhoneCalls(ACSettingsAct.this);

        //i.helper.favouriteNumbersList = pACFavourites.getFavouriteContactNumbers(this, ACSettingsAct.this); //temp

        Log.e("Categories: ", i.helper.categoriesList.toString());
        Log.e("Locations user: ", i.helper.locationsListUser.toString());
        Log.e("FavouriteNumbersList: ", i.helper.favouriteNumbersList.toString());

        SwipeMenuListView pListview = (SwipeMenuListView)findViewById(R.id.lv_locationsInfo);
        pLocationAdapter = new ACLocationAdapter(ACSettingsAct.this, i.helper.locationsListUser);
        pListview.setAdapter(pLocationAdapter);

        SwipeMenuCreator creator = setSwipeCreator();
        pListview.setMenuCreator(creator);

        pListview.setOnMenuItemClickListener(this);

        if (i.helper.isSensorServiceOn)
        {
            mSensorService = new SensorService(this);  //service
            mServiceIntent = new Intent(ACSettingsAct.this, mSensorService.getClass());

            if (!(isMyServiceRunning(mSensorService.getClass())))
            {
                startService(mServiceIntent);
            }
        }
    }

    private SwipeMenuCreator setSwipeCreator()
    {
        SwipeMenuCreator creator = new SwipeMenuCreator()
        {
            @Override
            public void create(SwipeMenu menu)
            {
                // create "details" item
                SwipeMenuItem pDetailsItem = new SwipeMenuItem(getApplicationContext());
                //pDetailsItem.setBackground(R.color.teal);
                pDetailsItem.setWidth(170);
                pDetailsItem.setTitle("Details");
                pDetailsItem.setTitleSize(18);
                pDetailsItem.setTitleColor(R.color.white);
                menu.addMenuItem(pDetailsItem);

                // create "Edit" item
                SwipeMenuItem pEditItem = new SwipeMenuItem(getApplicationContext());
                //pEditItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                pEditItem.setWidth(170);
                pEditItem.setTitle("Edit");
                pEditItem.setTitleColor(R.color.white);
                pEditItem.setTitleSize(18);
                menu.addMenuItem(pEditItem);

                // create "delete" item
                SwipeMenuItem pDeleteItem = new SwipeMenuItem(getApplicationContext());
                //pDeleteItem.setBackground(R.color.teal);
                //pDeleteItem.setBackground();
                pDeleteItem.setWidth(170);
                pDeleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(pDeleteItem);
            }
        };

        return (creator);
    }

    private void setHeader()
    {
        TextView txt_left = (TextView)findViewById(R.id.id_txtLeft);
        TextView txt_heading = (TextView)findViewById(R.id.id_txtHeading);
        TextView txt_right = (TextView)findViewById(R.id.id_txtRight);

        txt_heading.setText(i.helper.appTitle);

        txt_left.setVisibility(View.INVISIBLE);
        txt_right.setVisibility(View.INVISIBLE);
    }

    private void setPermissionToLocation()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private void setPermissionsToCreateDirectories()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
    }

    private void setPermisionsToCalender()
    {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, REQUEST_Calender);
        }
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.btn_addLocation)
        {
            if (i.helper.setPermissionsForLocation(this, ACSettingsAct.this))
            {
                Intent pIntent = new Intent(ACSettingsAct.this, ACSetLocationAct.class);
                pIntent.putExtra("AddOrEdit", "Add");
                startActivity(pIntent);
            }
        }
        else if (id == R.id.btn_temp)
        {
            /*Intent pIntent = new Intent(ACSettingsAct.this, MainActivity.class);
            startActivity(pIntent);*/

            Intent pIntent = new Intent(ACSettingsAct.this, ACGoogleCalenderEvents.class);
            startActivity(pIntent);
        }
    }

    @Override
    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index)
    {
        ACLocationInfo pLocationInfo = i.helper.locationsListUser.get(position);

        switch (index)
        {
            case 0: //Details
                Log.e("click", "details item " + position);

                android.support.v7.app.AlertDialog.Builder pAlert1 = new android.support.v7.app.AlertDialog.Builder(this);
                pAlert1.setTitle("Address of Location");
                pAlert1.setMessage(pLocationInfo.completeAddress);

                pAlert1.setPositiveButton("Ok", null);

                pAlert1.show();

                break;

            case 1: //Edit
                Log.e("click", "click item " + position);

                i.helper.editLocationIndex = position;

                Intent pIntent = new Intent(this, ACSetLocationAct.class);

                if (pLocationInfo != null)
                {
                    Location pLocation = pLocationInfo.location;

                    pIntent.putExtra("Name", i.helper.getLocationName(pLocationInfo.completeAddress));
                    pIntent.putExtra("Category", pLocationInfo.category);
                    pIntent.putExtra("Volume", pLocationInfo.volume);
                    pIntent.putExtra("Lat", pLocation == null ? 0.0 : pLocationInfo.location.getLatitude());
                    pIntent.putExtra("Long", pLocation == null ? 0.0 : pLocationInfo.location.getLongitude());
                    //pIntent.putExtra("Remarks", pLocationInfo.remarks);
                    pIntent.putExtra("AddOrEdit", "Edit");
                }

                startActivity(pIntent);
                pLocationAdapter.notifyDataSetChanged();

                break;

            case 2: //Delete
                Log.e("click", "delete item " + position);

                android.support.v7.app.AlertDialog.Builder pAlert2 = new android.support.v7.app.AlertDialog.Builder(this);
                pAlert2.setTitle("Delete Location");
                pAlert2.setMessage("Are you sure to delete " + pLocationInfo.category + " location?");

                pAlert2.setNegativeButton("Cancel", null);

                pAlert2.setPositiveButton("Ok", new android.support.v7.app.AlertDialog.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        i.helper.locationsListUser.remove(position);
                        i.helper.allData.writePlist();
                        pLocationAdapter.notifyDataSetChanged();
                    }
                });

                pAlert2.show();
        }

        return false;
    }

    private Boolean isMyServiceRunning(Class serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                Log.i ("isMyServiceRunning?", true+"");

                return true;
            }
        }

        Log.i ("isMyServiceRunning?", false+"");

        return (false);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (i.helper.isSensorServiceOn)
        {
            stopService(mServiceIntent);
            Log.e("ACSettingsAct", "onDestroy!");
        }
    }
}
