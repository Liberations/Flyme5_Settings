package com.android.wenxiangli.adsettings;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.android.wenxiangli.adsettings.Utils.ShellUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MyTask mTask;//创建异步任务
    ToggleButton tg_one, tg_two, tg_three, tg_four, tg_five;
    Spinner sp_Dpi;
    TextView weibo, eye;
    ToggleStatus status = new ToggleStatus();
    Boolean flag = false; //判断是否第一次进入界面
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean first;
    List<String> commnandList = new ArrayList<String>();//命令集合
    AlertDialog.Builder builder; //对话框
    String dpiValue = ""; //默认Dpi
    String sp_arr[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 取消状态栏
        //ms.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
         setContentView(R.layout.mainlayout);
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
        getSpinner();
        init();
       if(ShellUtils.checkRootPermission()){

           setData();
           setListener();
       } else{
           Toast.makeText(this,"似乎没有ROOT权限",Toast.LENGTH_SHORT).show();
       }


    }

    private void getSpinner() {
        sp_arr = getResources().getStringArray(R.array.dip_array);
    }

    /*
     * 初始化
     */
    private void init() {
        tg_one = (ToggleButton) findViewById(R.id.one);
        tg_two = (ToggleButton) findViewById(R.id.two);
        tg_three = (ToggleButton) findViewById(R.id.three);
        tg_four = (ToggleButton) findViewById(R.id.four);
        tg_five = (ToggleButton) findViewById(R.id.five);
        sp_Dpi = (Spinner) findViewById(R.id.sp_DPI);
        eye = (TextView) findViewById(R.id.tv_eye);
        weibo = (TextView) findViewById(R.id.weibo);


        String webLinkText = " <font color='#333333'><a href='http://weibo.com/fjdxdy' style='text-decoration:none; color:#0000FF'>\n" +
                "点击关注我的微博</a>";
        weibo.setText(Html.fromHtml(webLinkText));
        weibo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*
     * 设置ToggleButton的状态
     */
    private void setData() {
        preferences = getSharedPreferences("togglebuttonstatus", Context.MODE_PRIVATE);
        /*
         * 判断是不是第一次运行该程序
		 * （因为第一次运行时，SharedPreferences是没有保存"first"的，
		 * "first"不存在即为null，默认返回自己设置的参数true）
		 *
		 */
        first = preferences.getBoolean("first", true);
        editor = preferences.edit();
        if (first) {
            commnandList.add("mount -o rw,remount /system");
            commnandList.add("cp /system/build.prop /system/build.prop.bak");
            commnandList.add("chmod 644 /system/build.prop.bak");
            commnandList.add("sed -i  's/ro.sf.lcd_density.*/###Redy for DPI Change by Liberation/g' /system/build.prop");
            ShellUtils.execCommand(commnandList, true); //第一次执行进行备份工作
            getStatus();

        } else {

            status.one = preferences.getBoolean("s_one", false);
            status.two = preferences.getBoolean("s_two", false);
            status.three = preferences.getBoolean("s_three", false);
            status.four = preferences.getBoolean("s_four", false);
            status.five = preferences.getBoolean("s_five", false);

            setToggButonStatus(status);
        }
    }

    /*
     * 根据保存的参数设置每个ToggleButton的状态
     */
    private void setToggButonStatus(ToggleStatus data) {
        tg_one.setChecked(data.one);
        tg_two.setChecked(data.two);
        tg_three.setChecked(data.three);
        tg_four.setChecked(data.four);
        tg_five.setChecked(data.five);
    }

    /*
     * 获取每个ToggleButton的状态，并保存在status里面
     */
    private void getStatus() {
        status.one = tg_one.isChecked();
        status.two = tg_two.isChecked();
        status.three = tg_three.isChecked();
        status.four = tg_four.isChecked();
        status.five = tg_five.isChecked();
    }

    /*
     * 设置监听器
     */
    private void setListener() {

        tg_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                status.setOne(arg1);
                tg_one.setEnabled(false);
                mTask = new MyTask();
                Log.e("dt2w", "监听到:准备mTask ");
                mTask.execute("dt2w" + arg1);


                // Toast.makeText(MainActivity.this,"监听到了",Toast.LENGTH_LONG).show();


            }
        });
        tg_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                status.setTwo(arg1);
                tg_two.setEnabled(false);
                mTask = new MyTask();
                mTask.execute("edg" + arg1);

            }
        });
        tg_three.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                status.setThree(arg1);
                tg_three.setEnabled(false);
                mTask = new MyTask();
                mTask.execute("key" + arg1);

            }
        });


        tg_four.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                status.setFour(arg1);
                tg_four.setEnabled(false);
                mTask = new MyTask();
                mTask.execute("wifi" + arg1);

            }
        });
        tg_five.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                status.setFive(arg1);
                tg_five.setEnabled(false);
                mTask = new MyTask();
                mTask.execute("light" + arg1);

            }
        });
        sp_Dpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag) {
                    dpiValue = sp_arr[position];
                    sp_Dpi.setEnabled(false);
                    builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("修改DPI需要重启手机是否继续？");
                    // 相当于确定
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mTask = new MyTask();
                                    mTask.execute("dpiValue", dpiValue);

                                }
                            });
                    // 相当于取消
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sp_Dpi.setEnabled(true);


                                }
                            });


                    builder.show();
                }
                flag = true; //第一次不弹窗

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dpiValue = sp_arr[0];

            }
        });


    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (first) {
            editor.putBoolean("first", false);
        }
        //关闭之前把数据写进去
        editor.putBoolean("s_one", status.one);
        editor.putBoolean("s_two", status.two);
        editor.putBoolean("s_three", status.three);
        editor.putBoolean("s_four", status.four);
        editor.putBoolean("s_five", status.five);
        editor.commit();
    }

    public void join(View view) {
        joinQQGroup("kf9v_GPTygr_8Mbs5t7QizLubltgCed3") ;
    }


    private class MyTask extends AsyncTask<String, Integer, String> {
        List<String> commnandList = new ArrayList<String>();//命令集合

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("dt2w", " doInBackground");
            commnandList.clear();
            switch (params[0]) {

                case "dt2wtrue": {


                    commnandList.clear();
                    commnandList.add("mount -o rw,remount /sys");
                    commnandList.add("mount -o rw,remount /system");
                 /*   String a = "\"echo 1 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/wake_gesture\"";
                    commnandList.add("echo 1 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/wake_gesture");
                    commnandList.add("echo  "+a+" >> /system/etc/init.qcom.post_boot.sh");*/
                    commnandList.add("sed -i  's/echo 1 > /sys/devices/soc.0/f9924000.i2c.*/echo 0 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/wake_gesture/g' /system/etc/init.qcom.post_boot.sh");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("dt2w", "doInBackground:开启了双击唤醒 ");
                    break;

                }
                case "dt2wfalse": {
                    Log.e("dt2w", "doInBackground:没有执行事务 ");
                    commnandList.clear();
                    commnandList.add("mount -o rw,remount /sys");
                    commnandList.add("mount -o rw,remount /system");
                    commnandList.add("echo 0 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/wake_gesture");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("dt2w", "doInBackground:关闭了了双击唤醒 ");
                    break;
                }
                case "edgtrue": {
                    commnandList.add("mount -o rw,remount /sys");
                    commnandList.add("echo 2 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/edge_mode");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("edg", "doInBackground:开启了边缘触控 ");
                    break;

                }
                case "edgfalse": {
                    commnandList.add("mount -o rw,remount /sys");
                    commnandList.add("echo 0 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/edge_mode");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("edg", "doInBackground:关闭了边缘触控 ");
                    break;


                }

                case "keytrue": {
                    commnandList.add("mount -o rw,remount /sys");
                    commnandList.add("echo 0 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/0dbutton");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("key", "doInBackground:关闭了键盘 ");
                    break;

                }
                case "keyfalse": {
                    commnandList.add("mount -o rw,remount /sys");
                    commnandList.add("echo 1 > /sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input1/0dbutton");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("key", "doInBackground:开启了键盘 ");
                    break;

                }
                case "wifitrue": {
                    commnandList.add("mount -o rw,remount /system");
                    commnandList.add("settings put global captive_portal_detection_enabled 0");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("wifi", "doInBackground:去除了感叹号 ");
                    break;
                    //   Toast.makeText(MainActivity.this, "请手动开关一次飞行模式！", Toast.LENGTH_SHORT).show();

                }
                case "wififalse": {
                    commnandList.add("mount -o rw,remount /system");
                    commnandList.add("settings put global captive_portal_detection_enabled 1");
                    ShellUtils.execCommand(commnandList, true);
                    Log.w("wifi", "doInBackground:将会出现感叹号 ");
                    break;
                    //   Toast.makeText(MainActivity.this, "重启后可能出现\n感叹号导致不能正常上网\n建议打开开关！", Toast.LENGTH_SHORT).show();

                }
                case "dpiValue": {
                    Log.e("dpi", "doInBackground:进入DPI設置");
                    commnandList.add("wm density  " + params[1]);
                    ShellUtils.execCommand(commnandList, true); //执行命令
                    Log.e("dpi", "doInBackground:设置DPI");
                    break;
                }
                case "lighttrue": {
                    commnandList.add("chmod 666 /sys/class/leds/button-backlight/brightness ");
                    commnandList.add("chmod 666 /sys/class/leds/button-backlight1/brightness ");
                    commnandList.add("echo 0 > /sys/class/leds/button-backlight/brightness ");
                    commnandList.add("echo 0 > /sys/class/leds/button-backlight1/brightness ");
                    commnandList.add("chmod 755 /sys/class/leds/button-backlight/brightness ");
                    commnandList.add("chmod 755 /sys/class/leds/button-backlight1/brightness");

                    ShellUtils.execCommand(commnandList, true); //执行命令
                    Log.e("light", "doInBackground:关闭键盘灯");
                    break;


                }
                case "lightfalse": {
                    commnandList.add("chmod 666 /sys/class/leds/button-backlight/brightness ");
                    commnandList.add("chmod 666 /sys/class/leds/button-backlight1/brightness ");
                    commnandList.add("echo 255 > /sys/class/leds/button-backlight/brightness ");
                    commnandList.add("echo 255 > /sys/class/leds/button-backlight1/brightness ");
                    commnandList.add("chmod 755 /sys/class/leds/button-backlight/brightness ");
                    commnandList.add("chmod 755 /sys/class/leds/button-backlight1/brightness");

                    ShellUtils.execCommand(commnandList, true); //执行命令
                    Log.e("light", "doInBackground:开启键盘灯");
                    break;

                }
                default: {
                    Log.e("dpi", "啥都没干。。。" + params[0]);
                    break;

                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            sp_Dpi.setEnabled(true); //容器可用
            tg_one.setEnabled(true);
            tg_two.setEnabled(true);
            tg_three.setEnabled(true);
            tg_four.setEnabled(true);
            tg_five.setEnabled(true);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public void toEye(View view) {

        Intent mIntent = new Intent("android.intent.action.MAIN");
        ComponentName comp = new ComponentName("cn.mixiaoxiao.myappscreenmask", "cn.mixiaoxiao.myappscreenmask.MainActivity");
        mIntent.setComponent(comp);
        mIntent.addCategory("android.intent.category.LAUNCHER");
        startActivity(mIntent);//启动


    }
    /****************
     *
     * 发起添加群流程。群号：小米4c Flyme5 反馈交流(526048968) 的 key 为： kf9v_GPTygr_8Mbs5t7QizLubltgCed3
     * 调用 joinQQGroup(kf9v_GPTygr_8Mbs5t7QizLubltgCed3) 即可发起手Q客户端申请加群 小米4c Flyme5 反馈交流(526048968)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }


}
