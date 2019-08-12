package com.bontet.kcgscanner;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.device.scanner.configuration.Triggering;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action
    private ImageView scanButton;
    private TextView barcode_result,decode_symbology, res_artikel, res_color, res_size, res_qty;
    private CheckBox continuousScan;
    private RadioGroup mRadioGroup;
    private ScanManager mScanManager;
    int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
    int[] idmodebuf = new int[]{PropertyID.WEDGE_KEYBOARD_ENABLE, PropertyID.TRIGGERING_MODES};
    String[] action_value_buf = new String[]{ScanManager.ACTION_DECODE, ScanManager.BARCODE_STRING_TAG};
    int[] idmode;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            String result = intent.getStringExtra(action_value_buf[1]);
            /*if(barcodelen != 0)
                barcodeStr = new String(barcode, 0, barcodelen);
            else
                barcodeStr = intent.getStringExtra("barcode_string");*/
            if(result != null) {
                barcode_result.setText("" + result);
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                String a = barcode_result.getText().toString();

                String art=null,col=null,siz=null,qty=null;
                Cursor artikels = databaseAccess.getArtikel(a);
                while (artikels.moveToNext()){
                    art = artikels.getString(artikels.getColumnIndex("artikel"));
                    col = artikels.getString(artikels.getColumnIndex("color"));
                    siz = artikels.getString(artikels.getColumnIndex("size"));

                }

                res_artikel.setText(art);
                res_color.setText(col);
                res_size.setText(siz);
//                    databaseAccess.insert();

                databaseAccess.close();
//                decode_length.setText("" + result.length());
            }
        }

    };
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScanManager = new ScanManager();
        mScanManager.openScanner();

        res_artikel = findViewById(R.id.res_artikel);
        res_color =findViewById(R.id.res_color);
        res_size = findViewById(R.id.res_size);
        res_size = findViewById(R.id.res_size);
        res_qty = findViewById(R.id.res_size);
        action_value_buf = mScanManager.getParameterString(idbuf);
        idmode = mScanManager.getParameterInts(idmodebuf);

        decode_symbology = findViewById(R.id.symbology_result);
        barcode_result =  findViewById(R.id.barcode_result);
        barcode_result.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP)
                {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();

                    String a = barcode_result.getText().toString();

                    String art=null,col=null,siz=null;
                    Cursor artikels = databaseAccess.getArtikel(a);
                    while (artikels.moveToNext()){
                        art = artikels.getString(artikels.getColumnIndex("artikel"));
                        col = artikels.getString(artikels.getColumnIndex("color"));
                        siz = artikels.getString(artikels.getColumnIndex("size"));

                    }

                    res_artikel.setText(art);
                    res_color.setText(col);
                    res_size.setText(siz);
//                    databaseAccess.insert();

                    databaseAccess.close();

                    //barcode_result.setText("" + scanResult.getText());
//                  decode_length.setText("" + scanResult.getText().length());
                    barcode_result.setText("");
                    barcode_result.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 138 || keyCode == 120 || keyCode == 520 || keyCode == 521 || keyCode == 522) {
            barcode_result.requestFocus();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter();
        action_value_buf = mScanManager.getParameterString(idbuf);
        filter.addAction(action_value_buf[0]);
        registerReceiver(mScanReceiver, filter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.activity_main, menu);
//        MenuItem settings = menu.add(0, 1, 0, R.string.menu_settings).setIcon(R.drawable.ic_action_settings);
//        MenuItem version = menu.add(0, 2, 0, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);;
//        settings.setShowAsAction(1);
//        version.setShowAsAction(0);
//        return super.onCreateOptionsMenu(menu);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 1:
                try{
                    Intent intent = new Intent("android.intent.action.SCANNER_SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                break;
            case 2:
                PackageManager pk = getPackageManager();
                PackageInfo pi;
                try {
                    pi = pk.getPackageInfo(getPackageName(), 0);
                    Toast.makeText(this, "V" +pi.versionName , Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
