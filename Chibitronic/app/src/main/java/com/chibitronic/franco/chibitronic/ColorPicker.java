package com.chibitronic.franco.chibitronic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ColorPicker extends Activity implements SeekBar.OnSeekBarChangeListener{

    public static final String MIXPANEL_TOKEN = "8df466c6bae9ae3c3117de78e1af819d";

    View colorView;
    SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    TextView redToolTip, greenToolTip, blueToolTip;
    Button buttonSelector;
    ClipboardManager clipBoard;
    ClipData clip;
    Window window;
    Display display;
    int red, green, blue, seekBarLeft;
    Rect thumbRect;
    AlertDialog alertDialog;
    MixpanelAPI mixpanel;
    JSONObject props;

    //API FACADE which handles ColorPicker requests
    protected ApiFacade api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setContentView(R.layout.layout_color_picker);
        }
        else{
            setContentView(R.layout.layout_16);
        }

        api = ApiFacade.getInstance(this);
        mixpanel = MixpanelAPI.getInstance(this,MIXPANEL_TOKEN);
        props = new JSONObject();

        display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        SharedPreferences settings = getSharedPreferences("COLOR_SETTINGS", 0);
        red = settings.getInt("RED_COLOR", 0);
        green = settings.getInt("GREEN_COLOR", 0);
        blue = settings.getInt("BLUE_COLOR", 0);

        clipBoard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        colorView = findViewById(R.id.colorView);
        window = getWindow();

        redSeekBar = (SeekBar)findViewById(R.id.redSeekBar);
        greenSeekBar = (SeekBar)findViewById(R.id.greenSeekBar);
        blueSeekBar = (SeekBar)findViewById(R.id.blueSeekBar);

        seekBarLeft = redSeekBar.getPaddingLeft();

        redToolTip = (TextView)findViewById(R.id.redToolTip);
        greenToolTip = (TextView)findViewById(R.id.greenToolTip);
        blueToolTip = (TextView)findViewById(R.id.blueToolTip);

        buttonSelector = (Button)findViewById(R.id.buttonSelector);

        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        redSeekBar.setProgress(red);
        greenSeekBar.setProgress(green);
        blueSeekBar.setProgress(blue);

        //Setting View, Status bar & button color & hex codes

        colorView.setBackgroundColor(Color.rgb(red, green, blue));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (display.getRotation() != Surface.ROTATION_90 && display.getRotation() != Surface.ROTATION_270)
                window.setStatusBarColor(Color.rgb(red, green, blue));

        }

        //Set's color hex on Button
        buttonSelector.setText(String.format("#%02x%02x%02x", red, green, blue));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        thumbRect = redSeekBar.getThumb().getBounds();

        redToolTip.setX(seekBarLeft + thumbRect.left);
        if (red<10)
            redToolTip.setText("  "+red);
        else if (red<100)
            redToolTip.setText(" "+red);
        else
            redToolTip.setText(red+"");

        thumbRect = greenSeekBar.getThumb().getBounds();

        greenToolTip.setX(seekBarLeft + thumbRect.left);
        if (green<10)
            greenToolTip.setText("  "+green);
        else if (red<100)
            greenToolTip.setText(" "+green);
        else
            greenToolTip.setText(green+"");

        thumbRect = blueSeekBar.getThumb().getBounds();

        blueToolTip.setX(seekBarLeft + thumbRect.left);
        if (blue<10)
            blueToolTip.setText("  "+blue);
        else if (blue<100)
            blueToolTip.setText(" "+blue);
        else
            blueToolTip.setText(blue+"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar.getId() == R.id.redSeekBar) {

            red = progress;
            thumbRect = seekBar.getThumb().getBounds();

            redToolTip.setX(seekBarLeft + thumbRect.left);

            if (progress<10)
                redToolTip.setText("  " + red);
            else if (progress<100)
                redToolTip.setText(" "+red);
            else
                redToolTip.setText(red+"");

        }
        else if (seekBar.getId() == R.id.greenSeekBar) {

            green = progress;
            thumbRect = seekBar.getThumb().getBounds();

            greenToolTip.setX(seekBar.getPaddingLeft()+thumbRect.left);
            if (progress<10)
                greenToolTip.setText("  "+green);
            else if (progress<100)
                greenToolTip.setText(" "+green);
            else
                greenToolTip.setText(green+"");

        }
        else if (seekBar.getId() == R.id.blueSeekBar) {

            blue = progress;
            thumbRect = seekBar.getThumb().getBounds();

            blueToolTip.setX(seekBarLeft + thumbRect.left);
            if (progress<10)
                blueToolTip.setText("  "+blue);
            else if (progress<100)
                blueToolTip.setText(" "+blue);
            else
                blueToolTip.setText(blue+"");

        }

        colorView.setBackgroundColor(Color.rgb(red, green, blue));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (display.getRotation() == Surface.ROTATION_0)
                window.setStatusBarColor(Color.rgb(red, green, blue));

        }

        //Setting the button hex color
        buttonSelector.setText(String.format("#%02x%02x%02x", red, green, blue));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void colorSelect(View view) {
        //Copies color to Clipboard
        clip = ClipData.newPlainText("clip", buttonSelector.getText());
        clipBoard.setPrimaryClip(clip);

        //Mixpanel event tracker
        try {
            props.put("Color", buttonSelector.getText());
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Color Selected", props);

        Toast.makeText(this, "Color " + buttonSelector.getText() + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    /** TODO Method which handles SparkRequest  */
    public void talkToSpark(View view){
        try {
            // Call a Spark Core function.
            api.callFunction("lightRed/",red);
            api.callFunction("lightGreen/",green);
            api.callFunction("lightBlue/",blue);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Manually flushing selected colors to mixpanel server
        mixpanel.flush();

        //Storing values of red, green & blue in SharedPreferences
        SharedPreferences settings = getSharedPreferences("COLOR_SETTINGS", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("RED_COLOR", redSeekBar.getProgress());
        editor.putInt("GREEN_COLOR", greenSeekBar.getProgress());
        editor.putInt("BLUE_COLOR", blueSeekBar.getProgress());
        editor.apply();

        //Properly dismissing dialog to prevent errors while changing orientation
        try {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
        catch (NullPointerException e) {
            //do nothing
        }
    }

    public void showDetails(View view) {

            View dialogLayout = View.inflate(this, R.layout.dialog, null);

            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Chibitronic")

                    .setView(dialogLayout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            alertDialog.findViewById(R.id.website).setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://github.com/francotestori")));
                }
            });

            alertDialog.findViewById(R.id.github).setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://github.com/francotestori")));
                }
            });

            alertDialog.findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    Uri uri = Uri.parse("http://instagram.com/_u/francotestori");
                    Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                    insta.setPackage("com.instagram.android");

                    PackageManager packageManager = getBaseContext().getPackageManager();
                    List<ResolveInfo> list = packageManager.queryIntentActivities(insta, PackageManager.MATCH_DEFAULT_ONLY);

                    if (list.size()>0) {
                        startActivity(insta);
                    }
                    else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/francotestori")));
                    }

                }
            });

            alertDialog.findViewById(R.id.dribbble).setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://dribbble.com/shots/1858968-Material-Design-colorpicker?list=users&offset=2")));
                }
            });

    }
}
