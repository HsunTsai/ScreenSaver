package com.hsun.screensaver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.service.dreams.DreamService;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MyDreamService extends DreamService {

    private ImageView img_background;
    private TextView txt_time, txt_date, txt_battery;
    private ImageView img_charging_type;
    private Handler handler;
    private IntentFilter intentFilter;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm.ss", Locale.getDefault()),
            dateFormat = new SimpleDateFormat("YYYY/MM/dd (E)", Locale.getDefault());

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true); // 是否全螢幕?
//        setInteractive(true); // 觸屏不要退出?
        setScreenBright(false); // 是否高亮度?
        setContentView(R.layout.dream_page);

        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        txt_time = findViewById(R.id.txt_time);
        txt_date = findViewById(R.id.txt_date);
        txt_battery = findViewById(R.id.txt_battery);
        img_charging_type = findViewById(R.id.img_charging_type);

//        img_background = findViewById(R.id.img_background);
//
//        Glide.with(this)
//                .load(R.drawable.fullstack)
//                .into(img_background);

        Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                HandleMessage.set(handler, "updateTime");
                HandleMessage.set(handler, "updateBattery");
            }
        }, 0, 1000);
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
    }

    @SuppressLint("HandlerLeak")
    private void Handler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                Intent batteryStatus = registerReceiver(null, intentFilter);
                switch (msg.getData().getString("title", "")) {
                    case "updateTime":
                        txt_time.setText(timeFormat.format(new Date()));
                        txt_date.setText(dateFormat.format(new Date()));
                        break;
                    case "updateBattery":
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        txt_battery.setText(Math.round(level * 100 / scale) + "%");
                        break;
                    case "updateChargeType":
                        switch (batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                            case BatteryManager.BATTERY_PLUGGED_AC:
                                img_charging_type.setImageResource(R.drawable.charging_line);
                                break;
                            case BatteryManager.BATTERY_PLUGGED_USB:
                                img_charging_type.setImageResource(R.drawable.charging_usb);
                                break;
                            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                                img_charging_type.setImageResource(R.drawable.charging_wireless);
                                break;
                        }
                        break;
                }
            }
        };
    }
}
