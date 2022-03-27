package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ACSplashScreenAct extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        i.helper.hideTitleAndTitleBar(this);

        setContentView(R.layout.activity_acsplash_screen);

        TextView txt_tired = (TextView)findViewById(R.id.txt_tired);
        TextView txt_setup = (TextView)findViewById(R.id.txt_setup);

        String pText1 = "Tired of changing modes manually?";
        String pText2 = "Set up your preferences right now and relax!";

        txt_tired.setText(pText1);
        txt_setup.setText(pText2);

        Button btn_started = (Button)findViewById(R.id.btn_started);
        btn_started.setOnClickListener(this);

        Button btn_know = (Button)findViewById(R.id.btn_know);
        btn_know.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_know)
        {
            Intent pIntent = new Intent(getApplicationContext(), ACKnowMoreAct.class);
            startActivity(pIntent);
        }
        else if (id == R.id.btn_started)
        {
            Intent pIntent = new Intent(getApplicationContext(), ACSettingsAct.class);
            startActivity(pIntent);
        }
    }
}
