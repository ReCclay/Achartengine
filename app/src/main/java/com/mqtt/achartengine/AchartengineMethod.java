package com.mqtt.achartengine;

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

public class AchartengineMethod {

    private Context context;
    private XYMultipleSeriesRenderer multipleSeriesRenderer;// 整体的渲染器容器
    public XYMultipleSeriesDataset multipleSeriesDataset;// 数据集容器
    private GraphicalView mGraphicalView;//图表的视图
    private ArrayList<XYSeriesRenderer> xySeriesRenderers =
            new ArrayList<XYSeriesRenderer>();//储存每一条线的渲染器
    private ArrayList<XYSeries> xySeries = new ArrayList<XYSeries>();//储存每一条线的数据集

    public AchartengineMethod(Context context)
    {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();//设置图表的各种颜色啦大小啦等等参数
        multipleSeriesDataset = new XYMultipleSeriesDataset();//图表的数据集,因为只创建一次所以放在这里
        this.context = context;
    }

    /**
     * @return 获取这个图形化的视图,加入到别的View就显示出来了
     */
    public GraphicalView getGraphicalView()
    {                                                              //数据                                                      渲染器                                     平滑度
        mGraphicalView = ChartFactory.getCubeLineChartView(context,multipleSeriesDataset, multipleSeriesRenderer, 0.1f);
        return mGraphicalView;
    }

    /**
     * 设置整体的渲染器,也就是设置底下的网格等一些参数
     * @param LabelsColor      所有标签的颜色
     * @param MarginsColor	 网格框以外的颜色
     * @param AxesColor        设置X轴的颜色.
     * @param BackgroundColor  网格框背景色
     * @param GridColor        网格颜色
     * @param LegendTextSize   图例文字大小
     * @param LabelsTextSize   设置刻度显示文字的大小(XY轴都会被设置)
     * @param PanLimits        设置滑动范围
     * @param XLabelsPadding   设置标签的间距
     * @param XLabels          设置X轴标签的个数
     * @param YLabels          设置Y轴标签的个数
     * @param XAxisMin         设置 X 轴起点
     * @param XAxisMax         设置 X 轴终点
     */
    public void setXYMultipleSeriesRenderer(int LabelsColor,int MarginsColor,int AxesColor,int BackgroundColor
            ,int GridColor,int LegendTextSize,int LabelsTextSize,double[] PanLimits,int XLabelsPadding,int XLabels
            ,int YLabels,int XAxisMin,int XAxisMax)
    {
        multipleSeriesRenderer.setLabelsColor(LabelsColor);//所有的标题的颜色
        multipleSeriesRenderer.setMarginsColor(MarginsColor);//网格框以外的颜色
        multipleSeriesRenderer.setAxesColor(AxesColor);//设置X轴的颜色.
        multipleSeriesRenderer.setBackgroundColor(BackgroundColor);//网格框背景色
        multipleSeriesRenderer.setGridColor(GridColor);//网格颜色
        multipleSeriesRenderer.setXLabelsColor(Color.GREEN);

        multipleSeriesRenderer.setLegendTextSize(LegendTextSize);// 图例文字大小 --左下角的
        multipleSeriesRenderer.setLabelsTextSize(LabelsTextSize);// 设置刻度显示文字的大小(XY轴都会被设置)
        multipleSeriesRenderer.setPanLimits(PanLimits);//设置滑动范围上下左右

        multipleSeriesRenderer.setMargins(new int[] {0, 20, 50, 20});//上,左,下,右

        multipleSeriesRenderer.setXLabelsPadding(XLabelsPadding);//设置标签的间距
        multipleSeriesRenderer.setXLabels(XLabels);//设置X轴标签的个数--初始化可见的标签数
        multipleSeriesRenderer.setYLabels(YLabels);//设置Y轴标签的个数--初始化可见的标签数

        multipleSeriesRenderer.setXAxisMin(XAxisMin);//设置X轴的最小值为
        multipleSeriesRenderer.setXAxisMax(XAxisMax);//设置X轴的最大值---屏幕总共XAxisMax-XAxisMin个点

        multipleSeriesRenderer.setFitLegend(true);//图例大小自适应.
        multipleSeriesRenderer.setApplyBackgroundColor(true); //允许设置网格框背景色
        multipleSeriesRenderer.setShowGrid(true);//显示网格
        multipleSeriesRenderer.setShowLabels(true, true);//是否显示X轴和Y轴刻度
        multipleSeriesRenderer.setShowLegend(true);//是否显示图例
        multipleSeriesRenderer.setZoomEnabled(true, true);//设置坐标轴是否能够缩放
        multipleSeriesRenderer.setPanEnabled(true,true);////设置滑动,这边是横向可以滑动,竖向不可滑动
        multipleSeriesRenderer.setInScroll(true); //允许自适应

//		multipleSeriesRenderer.setXLabelsAlign(Align.CENTER);// 刻度线与刻度标注之间的相对位置关系
//		multipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);// 刻度线与刻度标注之间的相对位置关系

        //multipleSeriesRenderer.setZoomButtonsVisible(false);//是否显示放大缩小按钮
        //multipleSeriesRenderer.setChartTitle("数据显示");//设置图表标题
    }

    /**
     * 配置曲线的参数
     * @param setColor     曲线的颜色
     * @param setLineWidth 曲线的宽度
     */
    public void setXYSeriesRenderer(int setColor,float setLineWidth)
    {
        XYSeriesRenderer mXySeriesRenderer = new XYSeriesRenderer();//获取曲线渲染的一些方法(也是创建了一条曲线)

        mXySeriesRenderer.setColor(setColor);//曲线颜色
        mXySeriesRenderer.setLineWidth(setLineWidth);//折线宽度
        mXySeriesRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
        //mRenderer2.setDisplayChartValuesDistance(10);///折线点的值距离折线点的距离
        multipleSeriesRenderer.addSeriesRenderer(mXySeriesRenderer);//把曲线加入整个的渲染器中

        xySeriesRenderers.add(mXySeriesRenderer);
    }

    /**
     * 设置曲线的数据集
     * @param curveTitle  曲线的名字,在最下面显示
     */
    public void setXYMultipleSeriesDataset(String curveTitle)
    {
        XYSeries mSeries = new XYSeries(curveTitle);//一条曲线的数据集
        multipleSeriesDataset.addSeries(mSeries);//曲线的数据集加入总数据集

        xySeries.add(mSeries);
    }

    /**
     *
     * @return 整体的渲染器容器
     */
    public XYMultipleSeriesRenderer getXYMultipleSeriesRenderer()
    {
        return multipleSeriesRenderer;
    }

    /**
     *
     * @return 整体的数据集容器
     */
    public XYMultipleSeriesDataset getXYMultipleSeriesDataset()
    {
        return multipleSeriesDataset;
    }

    /**
     *
     * @param index  返回哪一个
     * @return  返回某一条线的渲染器
     */
    public XYSeriesRenderer getXYSeriesRenderer(int index)
    {
        return xySeriesRenderers.get(index);
    }

    /**
     *
     * @param index  返回哪一个
     * @return 返回某一条线的数据集
     */
    public XYSeries getXYSeries(int index)
    {
        return xySeries.get(index);
    }

    /**
     *
     * @return 返回当前线的个数
     */
    public int getCount() {
        return (xySeriesRenderers.size() > xySeries.size()) ? xySeries.size():xySeriesRenderers.size();
    }

    /**
     * 删除某一条线的渲染器和数据集
     * @param index
     */
    public void removeXYSeriesXYSeriesRenderer(int index)
    {
        multipleSeriesRenderer.removeSeriesRenderer(xySeriesRenderers.get(index));
        multipleSeriesDataset.removeSeries(index);
        xySeriesRenderers.remove(index);
        xySeries.remove(index);
    }

    /**
	 * 删除某一条线的渲染器
	 * @param xySeriesRenderer  线的渲染器
	 */
	public void removeXYSeriesRenderer(XYSeriesRenderer xySeriesRenderer)
	{
		xySeriesRenderers.remove(xySeriesRenderer);
		multipleSeriesRenderer.removeSeriesRenderer(xySeriesRenderer);
	}

	/**
	 * 删除某一条线的数据集
	 * @param xySeries  线的数据集
	 */
	public void removeXYSeries(XYSeries xySeries)
	{

		multipleSeriesDataset.removeSeries(xySeries);
	}
}
