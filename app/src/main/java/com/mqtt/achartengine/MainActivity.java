package com.mqtt.achartengine;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

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
        constraintLayout = findViewById(R.id.constraintLayout1);

        mAchartengineMethod = new AchartengineMethod(MainActivity.this);//获取画图的那个类
        mAchartengineMethod.setXYMultipleSeriesRenderer(Color.RED, Color.BLACK, Color.RED, Color.BLACK,
                Color.argb(100, 0, 255, 0), 30, 30, new double[] {0,60,-200,200}, 1, 0, 10, 0, 60);//配置界面
        //初始化界面显示60个点

        mAchartengineMethod.setXYSeriesRenderer(Color.RED, 10.0f);//设置一条线加入渲染器
        mAchartengineMethod.setXYMultipleSeriesDataset("温度");//设置一条曲线的数据集名称

        mGraphicalView = mAchartengineMethod.getGraphicalView();//获取那个图表
        constraintLayout.addView(mGraphicalView);//把图表加在布局里面

        mAchartengineMethod.getXYSeries(0).add(1,20);
        mAchartengineMethod.getXYSeries(0).add(15,30);
        mAchartengineMethod.getXYSeries(0).add(20,40);
        mGraphicalView.repaint();//刷新
        countDownTimer.start();

//        是可以直接加其他的线的！
//        mAchartengineMethod.setXYSeriesRenderer(Color.BLUE, 10.0f);//设置一条线加入渲染器
//        mAchartengineMethod.setXYMultipleSeriesDataset("湿度");//设置一条曲线的数据集
//
//        mAchartengineMethod.getXYSeries(1).add(10,20);
//        mAchartengineMethod.getXYSeries(1).add(15,30);
//        mAchartengineMethod.getXYSeries(1).add(20,40);

    }

    private CountDownTimer countDownTimer = new CountDownTimer(36000,200) {
        @Override
        public void onTick(long millisUntilFinished) {
            Random rand = new Random();
            int i = rand.nextInt(100);
            UpdataChart(i);//更新图表
        }

        @Override
        public void onFinish() {

        }
    };

    /**
     * 更新图表
     * @param aa
     */
    private void UpdataChart(double aa)
    {
        try
        {
            mAchartengineMethod.getXYSeries(0).add(XCnt,aa);

            if (XCnt%20 == 0) {//获取时间标签
                format=new SimpleDateFormat("HH:mm:ss");
                mAchartengineMethod.getXYMultipleSeriesRenderer().addXTextLabel(XCnt,
                        format.format(data));
            }

//            if (MaxValue < aa)
//            {
//                MaxValue = aa;
//            }
//            if (MinValue>aa)
//            {
//                MinValue = aa;
//            }
            MaxValue = MaxValue < aa ? aa : MaxValue;
            MinValue = MinValue > aa ? aa : MinValue;

            if (mAchartengineMethod.getXYMultipleSeriesRenderer().getYAxisMax() < MaxValue)
            {
                mAchartengineMethod.getXYMultipleSeriesRenderer().setYAxisMax(MaxValue+20);
            }
            if (mAchartengineMethod.getXYMultipleSeriesRenderer().getYAxisMin() > MinValue)
            {
                mAchartengineMethod.getXYMultipleSeriesRenderer().setYAxisMin(MinValue-20);
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
        }
        catch (Exception e) {
            Log.e("eee", e.toString());
        }
    }

}
