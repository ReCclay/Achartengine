package com.mqtt.achartengine;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static String MqttUserString = "yang";//用户名
    public static String MqttPwdString = "11223344";//密码
    public static String MqttIPString = "47.93.19.134";//IP地址
    public static int MqttPort = 1883;//端口号
    public static String SubscribeString = "/sub";//订阅的主题
    public static String PublishString = "/pub";//发布的主题

    private SharedPreferences sharedPreferences;//存储数据
    private SharedPreferences.Editor editor;//存储数据

    private MyHandler myHandler;

    private ImageView ImageViewSwitch,ImageViewLed;
    boolean BooleanSwitch = true;
    private TextView TextViewTemperature,TextViewHumidity;

    private AchartengineMethod mAchartengineMethod;//显示波形图表的那个类
    private GraphicalView mGraphicalView;//显示波形的图表
    private ConstraintLayout constraintLayout;//把图表放在这个布局(View)里面
    long time = System.currentTimeMillis();
    Date data = new Date(time);
    SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEEE");
    double MaxValue = 0;
    double MinValue = 200;//数据的最大值和最小值--设置显示的可视范围

    long XCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 获取存储的MQTT配置数据
         */
        sharedPreferences = MainActivity.this.getSharedPreferences("mqttconfig",MODE_PRIVATE );
        MqttUserString = sharedPreferences.getString("MqttUser", "yang");
        MqttPwdString = sharedPreferences.getString("MqttPwd", "11223344");
        MqttIPString = sharedPreferences.getString("MqttIP", "47.93.19.134");
        MqttPort = sharedPreferences.getInt("MqttPort", 1883);
        SubscribeString = sharedPreferences.getString("MqttSub", "/sub");
        PublishString = sharedPreferences.getString("MqttPub", "/pub");

        myHandler = new MyHandler();

        ImageViewSwitch = findViewById(R.id.imageView4);
        ImageViewSwitch.setOnClickListener(ImageViewSwitchClient);
        ImageViewLed = findViewById(R.id.imageView3);
        TextViewTemperature = findViewById(R.id.textView2);
        TextViewHumidity = findViewById(R.id.textView4);

        constraintLayout = findViewById(R.id.constraintLayout2);
        mAchartengineMethod = new AchartengineMethod(MainActivity.this);//获取画图的那个类
        mAchartengineMethod.setXYMultipleSeriesRenderer(Color.RED, Color.BLACK, Color.RED, Color.BLACK,
                Color.argb(100, 0, 255, 0), 30, 30, new double[] {0,60,-200,200}, 1, 0, 10, 0, 60);//配置界面
        //初始化界面显示60个点

        mAchartengineMethod.setXYSeriesRenderer(Color.RED, 10.0f);//设置一条线加入渲染器
        mAchartengineMethod.setXYMultipleSeriesDataset("温度");//设置一条曲线的数据集名称

        mAchartengineMethod.setXYSeriesRenderer(Color.BLUE, 10.0f);//设置一条线加入渲染器
        mAchartengineMethod.setXYMultipleSeriesDataset("湿度");//设置一条曲线的数据集

        mGraphicalView = mAchartengineMethod.getGraphicalView();//获取那个图表
        constraintLayout.addView(mGraphicalView);//把图表加在布局里面
        mGraphicalView.repaint();//刷新

        double[] initdouble = new double[2];//添加两个数据,为了显示出来界面
        UpdataChart(initdouble);

//        mAchartengineMethod.getXYSeries(0).add(1,20);
//        mAchartengineMethod.getXYSeries(0).add(15,30);
//        mAchartengineMethod.getXYSeries(0).add(20,40);
//        countDownTimer.start();

//        是可以直接加其他的线的！
//        mAchartengineMethod.setXYSeriesRenderer(Color.BLUE, 10.0f);//设置一条线加入渲染器
//        mAchartengineMethod.setXYMultipleSeriesDataset("湿度");//设置一条曲线的数据集
//
//        mAchartengineMethod.getXYSeries(1).add(10,20);
//        mAchartengineMethod.getXYSeries(1).add(15,30);
//        mAchartengineMethod.getXYSeries(1).add(20,40);

    }

    private View.OnClickListener ImageViewSwitchClient = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String SendData = "";
            if (BooleanSwitch == false)
            {
                SendData = "switch;relay=0";
                BooleanSwitch = true;
                ImageViewLed.setImageResource(R.mipmap.ledoff);
                ImageViewSwitch.setImageResource(R.mipmap.switchoff);
            }
            else
            {
                SendData = "switch;relay=1";
                BooleanSwitch = false;
                ImageViewLed.setImageResource(R.mipmap.ledon);
                ImageViewSwitch.setImageResource(R.mipmap.switchon);
            }
            Intent intent = new Intent();
            intent.setAction("ActivitySendMqttService");
            intent.putExtra("OtherActivitySend","SendData;;"+SendData);
            sendBroadcast(intent);
        }
    };

    /**配置MQTT对话框*/
    private void MqttConfigAlertDialog(String Title)
    {
        AlertDialog.Builder MqttConfigAlertDialog = new AlertDialog.Builder(MainActivity.this);
        View MqttConfigView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_mqtt, null);

        final EditText editTextMqttUser = (EditText) MqttConfigView.findViewById(R.id.editText21);//用户名
        final EditText editTextMqttPwd = (EditText) MqttConfigView.findViewById(R.id.editText22);//密码
        final EditText editTextMqttIP = (EditText) MqttConfigView.findViewById(R.id.editText23);//IP地址
        final EditText editTextMqttPort = (EditText) MqttConfigView.findViewById(R.id.editText24);//端口号
        final EditText editTextMqttSub = (EditText) MqttConfigView.findViewById(R.id.editText25);//订阅的主题
        final EditText editTextMqttPub = (EditText) MqttConfigView.findViewById(R.id.editText26);//发布的主题


        editTextMqttUser.setFocusable(true);
        editTextMqttUser.setFocusableInTouchMode(true);
        editTextMqttUser.requestFocus();//获取焦点 光标出现

        MqttConfigAlertDialog.setTitle(Title);

        MqttConfigAlertDialog.setPositiveButton("确定",null);//实现方法在下面,目的是点击按钮不关闭

        MqttConfigAlertDialog.setNegativeButton("默认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MqttUserString = "yang";//用户名
                MqttPwdString = "11223344";//密码
                MqttIPString = "47.93.19.134";//IP地址
                MqttPort = 1883;//端口号
                SubscribeString = "/pub";//订阅的主题
                PublishString = "/sub";//发布的主题


                editor = sharedPreferences.edit();
                editor.putString("MqttUser", "yang");//用户名
                editor.putString("MqttPwd", "11223344");//密码
                editor.putString("MqttIP", "47.93.19.134");//IP地址
                editor.putInt("MqttPort",1883);//端口号
                editor.putString("MqttSub","/sub");//订阅的主题
                editor.putString("MqttPub","/pub");//发布的主题
                editor.commit();


                Intent intent = new Intent();
                intent.setAction("ActivitySendMqttService");
                intent.putExtra("OtherActivitySend","ResetMqtt;;");
                sendBroadcast(intent);
            }
        });

        MqttConfigAlertDialog.setView(MqttConfigView);//对话框加载视图
//        MqttConfigAlertDialog.show();

        final AlertDialog mqttConfigAlertDialog  = MqttConfigAlertDialog.create();
//        mqttConfigAlertDialog.setCanceledOnTouchOutside(false);//点击外围不消失

        //初始化显示
        mqttConfigAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                editTextMqttUser.setText(MqttUserString);
                editTextMqttPwd.setText(MqttPwdString);
                editTextMqttIP.setText(MqttIPString);
                editTextMqttPort.setText(MqttPort+"");
                editTextMqttSub.setText(SubscribeString);
                editTextMqttPub.setText(PublishString);


                editTextMqttUser.setSelection(editTextMqttUser.getText().length());//将光标移至文字末尾
            }
        });

        mqttConfigAlertDialog.show();//必须先显示.....
        /*点击了确定按钮*/
        mqttConfigAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String str1 = editTextMqttUser.getText().toString();
                    String str2 = editTextMqttPwd.getText().toString();
                    String str3 = editTextMqttIP.getText().toString();
                    String str4 = editTextMqttPort.getText().toString();
                    String str5 = editTextMqttSub.getText().toString();
                    String str6 = editTextMqttPub.getText().toString();

                    if (str1.length() == 0 || str2.length() == 0 ||str3.length() == 0 ||str4.length() == 0 ||
                            str5.length() == 0 ||str6.length() == 0) {
                        Toast.makeText(getApplicationContext(), "请检查输入",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MqttUserString = str1;//用户名
                    MqttPwdString = str2;//密码
                    MqttIPString = str3;//IP地址
                    MqttPort = Integer.parseInt(str4);//端口号
                    SubscribeString = str5;//订阅的主题
                    PublishString = str6;//发布的主题


                    editor = sharedPreferences.edit();
                    editor.putString("MqttUser", MqttUserString);//用户名
                    editor.putString("MqttPwd", MqttPwdString);//密码
                    editor.putString("MqttIP", MqttIPString);//IP地址
                    editor.putInt("MqttPort",MqttPort);//端口号
                    editor.putString("MqttSub",SubscribeString);//订阅的主题
                    editor.putString("MqttPub",PublishString);//发布的主题
                    editor.commit();

                    Intent intent = new Intent();
                    intent.setAction("ActivitySendMqttService");
                    intent.putExtra("OtherActivitySend","ResetMqtt;;");
                    sendBroadcast(intent);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "存储失败,请检查输入",Toast.LENGTH_SHORT).show();
                }
                mqttConfigAlertDialog.dismiss();
            }
        });
    }

    /*该类的广播接收程序*/
    private BroadcastReceiver MainActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                String msgString = intent.getStringExtra("MqttServiceSend");

                if(msgString != null)
                {
                    Message msg = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("MqttServiceSend",msgString);
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                }
                msgString = intent.getStringExtra("MqttServiceSendToast");
                if (msgString != null)
                {
                    Toast.makeText(MainActivity.this,msgString,Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
        }
    };

    /**
     * 接受消息，处理消息 ，此Handl线程一块运行
     * */
    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String StringData = bundle.getString("MqttServiceSend");//后台发过来的数据

            if (StringData != null)
            {
                String tempsString[] = StringData.split(";;");//分离主题和数据
                if (tempsString.length>1)
                {
                    String tempsDataString[] = tempsString[1].split(";");//分离数据
                    if (tempsDataString.length>0)
                    {
                        try
                        {
                            if (tempsDataString[0].equals("data"))//数据的第一个是data
                            {
                                double[] mdouble = new double[tempsDataString.length-1];
                                for (int i = 1; i < tempsDataString.length; i++)
                                {
                                    String[] tempStrings = tempsDataString[i].split("=");//分离"="数据
                                    try
                                    {
                                        mdouble[i-1] =  Double.valueOf(tempStrings[1]);
                                    }
                                    catch (Exception e)
                                    {
                                    }
                                }
                                UpdataChart(mdouble);
                                TextViewTemperature.setText(Double.toString(mdouble[0])+"℃");
                                TextViewHumidity.setText(Double.toString(mdouble[1])+"RH");
                            }
                        }
                        catch (Exception e)
                        {

                        }

                    }
                }
            }
        }
    }

    /**
     * 更新图表
     * @param aa
     */
    private void UpdataChart(double[] aa)
    {
        if (mAchartengineMethod.getCount()>0)
        {
            try //防止删除意外的冲突
            {
                for (int j = 0; j < mAchartengineMethod.getCount(); j++)
                {
                    double mdouble = aa[j];
                    mAchartengineMethod.getXYSeries(j).add(XCnt, mdouble);
                    MaxValue = MaxValue < mdouble ? mdouble:MaxValue;
                    MinValue = MinValue > mdouble ? mdouble:MinValue;
                }

                if (XCnt%20 == 0) {
                    format=new SimpleDateFormat("HH:mm:ss");
                    mAchartengineMethod.getXYMultipleSeriesRenderer().addXTextLabel(XCnt,
                            format.format(data));
                }

                if (mAchartengineMethod.getXYMultipleSeriesRenderer().getYAxisMax() < MaxValue)
                {
                    mAchartengineMethod.getXYMultipleSeriesRenderer().setYAxisMax(MaxValue+10);
                }
                if (mAchartengineMethod.getXYMultipleSeriesRenderer().getYAxisMin() > MinValue)
                {
                    mAchartengineMethod.getXYMultipleSeriesRenderer().setYAxisMin(MinValue-10);
                }
                XCnt++;
                long cnt = XCnt;
                long zheng = cnt/30;
                long yu = cnt%30;
                if (zheng>1 && yu == 0) //2,60;  3,90;
                {
                    mAchartengineMethod.getXYMultipleSeriesRenderer().setXAxisMin((zheng-1)*30);//设置X轴的最小值为
                    mAchartengineMethod.getXYMultipleSeriesRenderer().setXAxisMax((zheng-1)*30+60);//设置X轴的最大值
                    mAchartengineMethod.getXYMultipleSeriesRenderer().setPanLimits(new double[] {0,(zheng-1)*30+60,-200,200});//设置滑动范围,这边我很好奇他的单位
                }
                mGraphicalView.repaint();
            } catch (Exception e) {
                Log.e("eee", e.toString());
            }
        }
    }

//    private CountDownTimer countDownTimer = new CountDownTimer(36000,200) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            Random rand = new Random();
//            int i = rand.nextInt(100);
//            UpdataChart(i);//更新图表
//        }
//
//        @Override
//        public void onFinish() {
//
//        }
//    };
//
//    /**
//     * 更新图表
//     * @param aa
//     */
//    private void UpdataChart(double aa)
//    {
//        try
//        {
//            mAchartengineMethod.getXYSeries(0).add(XCnt,aa);
//
//            if (XCnt%20 == 0) {//获取时间标签
//                format=new SimpleDateFormat("HH:mm:ss");
//                mAchartengineMethod.getXYMultipleSeriesRenderer().addXTextLabel(XCnt,
//                        format.format(data));
//            }
//
////            if (MaxValue < aa)
////            {
////                MaxValue = aa;
////            }
////            if (MinValue>aa)
////            {
////                MinValue = aa;
////            }
//            MaxValue = MaxValue < aa ? aa : MaxValue;
//            MinValue = MinValue > aa ? aa : MinValue;
//
//            if (mAchartengineMethod.getXYMultipleSeriesRenderer().getYAxisMax() < MaxValue)
//            {
//                mAchartengineMethod.getXYMultipleSeriesRenderer().setYAxisMax(MaxValue+20);
//            }
//            if (mAchartengineMethod.getXYMultipleSeriesRenderer().getYAxisMin() > MinValue)
//            {
//                mAchartengineMethod.getXYMultipleSeriesRenderer().setYAxisMin(MinValue-20);
//            }
//
//            XCnt++;
//            long cnt = XCnt;
//            long zheng = cnt/30;
//            long yu = cnt%30;
//            if (zheng>1 && yu == 0) //2,60;  3,90;
//            {
//                mAchartengineMethod.getXYMultipleSeriesRenderer().setXAxisMin((zheng-1)*30);//设置X轴的最小值为
//                mAchartengineMethod.getXYMultipleSeriesRenderer().setXAxisMax((zheng-1)*30+60);//设置X轴的最大值
//                mAchartengineMethod.getXYMultipleSeriesRenderer().setPanLimits(new double[] {0,(zheng-1)*30+60,-200,200});//设置滑动范围,这边我很好奇他的单位
//            }
//            mGraphicalView.repaint();
//        }
//        catch (Exception e) {
//            Log.e("eee", e.toString());
//        }
//    }

    /** 当活动即将可见时调用 */
    @Override
    protected void onStart()
    {
        Intent startIntent = new Intent(getApplicationContext(), ServiceMqtt.class);
        startService(startIntent); //启动后台服务

        IntentFilter filter = new IntentFilter();//监听的广播
        filter.addAction("Broadcast.MqttServiceSend");
        registerReceiver(MainActivityReceiver, filter);
//        Log.e("err","onStart");
        super.onStart();
    }
    /** 当活动不再可见时调用 */
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    /** 当活动注销时调用 */
    @Override
    protected void onDestroy()
    {
        unregisterReceiver(MainActivityReceiver);
        super.onDestroy();
    }


    //首页右上角菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //新建的xml文件
        setIconsVisible(menu, true);//设置菜单添加图标有效
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //根据不同的id点击不同按钮控制activity需要做的事件
        switch (item.getItemId()) {
            case R.id.action_settings:
                //事件
                MqttConfigAlertDialog("MQTT设置");
                break;
        }
        return true;
    }

    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
