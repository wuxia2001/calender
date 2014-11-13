package com.ecology.calenderproj.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ecology.calenderproj.R;
import com.ecology.calenderproj.base.BorderText;
import com.ecology.calenderproj.base.BorderTextView;
import com.ecology.calenderproj.bean.ScheduleDAO;
import com.ecology.calenderproj.calender.LunarCalendar;
import com.ecology.calenderproj.vo.ScheduleVO;

/**
 * 日历显示activity
 * @author xiang
 *
 */
public class CalendarActivity extends Activity implements OnGestureListener,OnClickListener,OnLongClickListener {

	private static final String Tag="CalendarActivity";
	private LunarCalendar lcCalendar = null;
	private LunarCalendar calendar;
	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private CalendarView calV = null;
	private GridView gridView = null;
	private BorderText topText = null;
//	private TextView foot_tv = null;
	private Drawable draw = null;
	private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO;
	private String[] scheduleIDs;
	private  ArrayList<String> scheduleDate;
	private Dialog builder;
	private ScheduleVO scheduleVO_del;
	private String scheduleitems[];
	//小布局item的控件
	private BorderTextView schdule_tip;
	private Button add;
	private Button quit;
	private TextView day_tv;
	private TextView launarDay;
	private ListView listView;
	private TextView weekday;
	private TextView lunarTime;
	private ListView list;
	private String dateInfo;//点击gridview的日期信息
	private LayoutInflater inflater;
	

	public CalendarActivity() {

		Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    	currentDate = sdf.format(date);  //当期日期
    	year_c = Integer.parseInt(currentDate.split("-")[0]);
    	month_c = Integer.parseInt(currentDate.split("-")[1]);
    	day_c = Integer.parseInt(currentDate.split("-")[2]);
    	
    	
    	
    	dao = new ScheduleDAO(this);
	}

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calender_main);
		gestureDetector = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.removeAllViews();
        calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        
        addGridView();
        gridView.setAdapter(calV);
        //flipper.addView(gridView);
        flipper.addView(gridView,0);
        
		topText = (BorderText) findViewById(R.id.schedule_toptext);
		addTextToTopTextView(topText);
//		foot_tv =(TextView) findViewById(R.id.foot_tv);
	}
	
	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0;         //每次添加gridview到viewflipper中时给的标记
		if (e1.getX() - e2.getX() > 50) {
            //像左滑动
			addGridView();   //添加一个gridView
			jumpMonth++;     //下一个月
			
			calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        //flipper.addView(gridView);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView, gvFlag);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -50) {
            //向右滑动
			addGridView();   //添加一个gridView
			jumpMonth--;     //上一个月
			
			calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        gvFlag++;
	        addTextToTopTextView(topText);
	        //flipper.addView(gridView);
	        flipper.addView(gridView,gvFlag);
	        
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}
	
	/**
	 * 创建menu菜单
	 */
	
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, menu.FIRST, menu.FIRST, "今天");
		menu.add(0, menu.FIRST+1, menu.FIRST+1, "跳转");
		menu.add(0, menu.FIRST+2, menu.FIRST+2, "日期转换");
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 选择菜单
	 */
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
        case Menu.FIRST:
        	//跳转到今天
        	int xMonth = jumpMonth;
        	int xYear = jumpYear;
        	int gvFlag =0;
        	jumpMonth = 0;
        	jumpYear = 0;
        	addGridView();   //添加一个gridView
        	year_c = Integer.parseInt(currentDate.split("-")[0]);
        	month_c = Integer.parseInt(currentDate.split("-")[1]);
        	day_c = Integer.parseInt(currentDate.split("-")[2]);
        	calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView,gvFlag);
	        if(xMonth == 0 && xYear == 0){
	        	//nothing to do
	        }else if((xYear == 0 && xMonth >0) || xYear >0){
	        	this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
				this.flipper.showNext();
	        }else{
	        	this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
				this.flipper.showPrevious();
	        }
			flipper.removeViewAt(0);
        	break;
        case Menu.FIRST+1:
        	
        	new DatePickerDialog(this, new OnDateSetListener() {
				
				
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					//1901-1-1 ----> 2049-12-31
					if(year < 1901 || year > 2049){
						//不在查询范围内
						new AlertDialog.Builder(CalendarActivity.this).setTitle("错误日期").setMessage("跳转日期范围(1901/1/1-2049/12/31)").setPositiveButton("确认", null).show();
					}else{
						int gvFlag = 0;
						addGridView();   //添加一个gridView
			        	calV = new CalendarView(CalendarActivity.this, CalendarActivity.this.getResources(),year,monthOfYear+1,dayOfMonth);
				        gridView.setAdapter(calV);
				        addTextToTopTextView(topText);
				        gvFlag++;
				        flipper.addView(gridView,gvFlag);
				        if(year == year_c && monthOfYear+1 == month_c){
				        	//nothing to do
				        }
				        if((year == year_c && monthOfYear+1 > month_c) || year > year_c ){
				        	CalendarActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_left_in));
				        	CalendarActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_left_out));
				        	CalendarActivity.this.flipper.showNext();
				        }else{
				        	CalendarActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_right_in));
				        	CalendarActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_right_out));
				        	CalendarActivity.this.flipper.showPrevious();
				        }
				        flipper.removeViewAt(0);
				        //跳转之后将跳转之后的日期设置为当期日期
				        year_c = year;
						month_c = monthOfYear+1;
						day_c = dayOfMonth;
						jumpMonth = 0;
						jumpYear = 0;
					}
				}
			},year_c, month_c-1, day_c).show();
        	break;
        	
        case Menu.FIRST+2:
        	Intent mIntent=new Intent(CalendarActivity.this, CalendarConvertTrans.class);
        startActivity(mIntent);
        	
        	break;
        	
        }
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	public boolean onTouchEvent(MotionEvent event) {

		return this.gestureDetector.onTouchEvent(event);
	}

	
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 添加画板头部的年份 闰哪月等信息
	 * */
	public void addTextToTopTextView(TextView view){
		StringBuffer textDate = new StringBuffer();
		draw = getResources().getDrawable(R.drawable.schedule_title_bg);
		view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("年").append(
				calV.getShowMonth()).append("月").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("闰").append(calV.getLeapMonth()).append("月")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("年").append("(").append(
				calV.getCyclical()).append("年)");
		view.setText(textDate);
		view.setTextColor(Color.WHITE);
		view.setTextSize(15.0f);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	
	//添加农历信息
	public void addLunarDayInfo(TextView text){
		StringBuffer textDate = new StringBuffer();
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("闰").append(calV.getLeapMonth()).append("月")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("年").append("(").append(
				calV.getCyclical()).append("年)");
		text.setText(textDate);
	}
	
	//添加gridview,显示具体的日期
	@SuppressLint("ResourceAsColor")
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth(); 
        int Height = display.getHeight();
        
        Log.d(Tag, "屏幕分辨率=="+"height*weight"+Height+Width);
        
		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(46);
	//	gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if(Width == 480 && Height == 800){
			gridView.setColumnWidth(69);
		}else if(Width==800&&Height==1280){
			gridView.setColumnWidth(69);
		}
		
		
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
        gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setOnTouchListener(new OnTouchListener() {
            //将gridview中的触摸事件回传给gestureDetector
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return CalendarActivity.this.gestureDetector
						.onTouchEvent(event);
			}
		});

		
		gridView.setOnItemClickListener(new OnItemClickListener() {
            //gridView中的每一个item的点击事件
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				  //点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				  int startPosition = calV.getStartPositon();
				  int endPosition = calV.getEndPosition();
				  if(startPosition <= position  && position <= endPosition){
					  String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
					  //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
	                  String scheduleYear = calV.getShowYear();
	                  String scheduleMonth = calV.getShowMonth();
	                  String week = "";
	                 
	                  Log.i("日程历史浏览", scheduleDay);
	                  
	                  //通过日期查询这一天是否被标记，如果标记了日程就查询出这天的所有日程信息
	                  scheduleIDs = dao.getScheduleByTagDate(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
	                  
	                  //得到这一天是星期几
	                  switch(position%7){
	                  case 0:
	                	  week = "星期日";
	                	  break;
	                  case 1:
	                	  week = "星期一";
	                	  break;
	                  case 2:
	                	  week = "星期二";
	                	  break;
	                  case 3:
	                	  week = "星期三";
	                	  break;
	                  case 4:
	                	  week = "星期四";
	                	  break;
	                  case 5:
	                	  week = "星期五";
	                	  break;
	                  case 6:
	                	  week = "星期六";
	                	  break;
	                  }
					 
	                  scheduleDate = new ArrayList<String>();
	                  scheduleDate.add(scheduleYear);
	                  scheduleDate.add(scheduleMonth);
	                  scheduleDate.add(scheduleDay);
	                  scheduleDate.add(week);
	                  
	                  /**
	                   * 
	                   * 通过scheduleIDs是否被标记，标记在通过listview显示出来 
	                   */
	                 
	                  
//	            	  Intent mIntent=new Intent(CalendarActivity.this, ScheduleDetailsActivity.class);
////                	  startActivity(mIntent);
//                	  
                	   LayoutInflater inflater=getLayoutInflater();
	              		View linearlayout= inflater.inflate(R.layout.schedule_details, null);
	              		 add=(Button)linearlayout.findViewById(R.id.btn_add);
	              		 quit=(Button) linearlayout.findViewById(R.id.btn_back);
	              	 day_tv=(TextView) linearlayout.findViewById(R.id.todayDate);
	              		launarDay=(TextView)linearlayout.findViewById(R.id.tv_launar);
	                  schdule_tip=(com.ecology.calenderproj.base.BorderTextView)linearlayout.findViewById(R.id.schdule_tip);
	              	 listView=(ListView)linearlayout.findViewById(R.id.schedulelist);
	              		//星期
	              		 weekday=(TextView)linearlayout.findViewById(R.id.dayofweek);
	              		//农历日期
	              		 lunarTime=(TextView)linearlayout.findViewById(R.id.lunarTime);
	              		list=(ListView)linearlayout.findViewById(R.id.schedulelist);
	              	
	              	 dateInfo=scheduleYear+"年"+scheduleMonth+"月"+scheduleDay+"日";
	              	//添加农历信息	
	              	String scheduleLunarDay = getLunarDay(Integer.parseInt(scheduleYear),
	        				Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
	              	
	              	Log.i("LunarDay", scheduleLunarDay);
	              	//设置选中的日期的阳历,星期和农历信息
	              		day_tv.setText(dateInfo);
	              		weekday.setText(week);
	              		addLunarDayInfo(lunarTime);
	              		launarDay.setText( scheduleLunarDay);
	              		
	              		Log.i("scheduleDate", "scheduleDate的所有信息："+scheduleDate);
	              		//添加日程按钮
	              		//TableLayout dialog_tab=(TableLayout) linearlayout.findViewById(R.id.dialog_tab);
	              		add.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(builder!=null&&builder.isShowing()){
									builder.dismiss();
									Intent intent = new Intent();
					                  intent.putStringArrayListExtra("scheduleDate", scheduleDate);
					                  intent.setClass(CalendarActivity.this, ScheduleViewAddActivity.class);
					                  startActivity(intent);
								}
							}
						});
	              		//返回按钮
	              		quit.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(builder!=null&&builder.isShowing()){
									builder.dismiss();
								}
							}
						});
	                  
	                  //如果被标记，则加载相应的日程信息列表
                  if(scheduleIDs != null && scheduleIDs.length > 0){
                	  
                	  
		              		//list.setAdapter(new MyAdapter());
		              		View inflate=getLayoutInflater().inflate(R.layout.schedule_detail_item, null);
                        //通过arraylist绑定数据导listview中去
		              		ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String, String>>();  
							ScheduleDAO dao=new ScheduleDAO(CalendarActivity.this);
							 String time="";
		                	  String content="";
	                	  for(int i=0;i<scheduleIDs.length;i++){
	                	  scheduleVO=dao.getScheduleByID(CalendarActivity.this,Integer.parseInt(scheduleIDs[i]));
	                	 time="";
	                	 content="";
	                	  
	                	  time=dateInfo+" "+scheduleVO.getTime();
	                	  content=scheduleVO.getScheduleContent();
	                		
	                	 
	                	 
	                		  HashMap<String, String> map=new HashMap<String, String>();
	                		  map.put("date", time);
	                		  map.put("content", content);
          	              	  Data.add(map);
          	              	  
	                	  }
	                	 String  from[]={"date","content"};
	                	  int to[]={R.id.itemTime,R.id.itemContent};
	                	  
	                	  SimpleAdapter adapter=new SimpleAdapter(CalendarActivity.this, Data, R.layout.schedule_detail_item, from, to);
	                	  
	                	  list.setAdapter(adapter);
	                	  
	                	  //点击list的item相应事件
//	                	  list.setOnClickListener(CalendarActivity.this);
//	                	  list.setOnLongClickListener(CalendarActivity.this);
	        
	                	  
	                  }else{ //如果没有标记位直接则跟换为“暂无安排”
	                 
	                	  
	                	  schdule_tip.setText("暂无安排");
	                	  listView.setVisibility(View.INVISIBLE);
	                	  
//		                  Intent intent = new Intent();
//		                  intent.putExtra("top_Time", dateInfo);
//		                  Log.i("calendar", "calendar ifo-->"+dateInfo);
//		                  intent.putStringArrayListExtra("scheduleDate", scheduleDate);
//		                  intent.setClass(CalendarActivity.this,ScheduleDetailsNoDataActivity.class);
//		                  startActivity(intent);
	                  }
	                  
           	   //以dialog的形式显示到windows上
            	  builder =	new Dialog(CalendarActivity.this,R.style.FullScreenDialog);
            	  builder.setContentView(linearlayout);
            	  WindowManager windowManager = getWindowManager();
            	  Display display = windowManager.getDefaultDisplay();
            	  WindowManager.LayoutParams lp = builder.getWindow().getAttributes();
            	  lp.width = (int)(display.getWidth()); //设置宽度
            	  lp.height=display.getHeight();
            	  builder.getWindow().setAttributes(lp); 
            	  builder.setCanceledOnTouchOutside(true);
            	  builder.show();
	                  
	                  
	                  //点击查看详情
//	                  list.setOnItemClickListener(new OnItemClickListener() {
//
//						@Override
//						public void onItemClick(AdapterView<?> adapterview,
//								View view, int position, long id) {
//
//							
//							Log.i("日程item点击", "第"+position+"个item");
//							Intent intent=new Intent();
//							
//							if(view!=null){
//								
//								HashMap<String, String> map=(HashMap<String, String>) adapterview.getItemAtPosition(position);
//								
//								ScheduleVO scheduleVO=  (ScheduleVO) view.getTag();
//								
//								Log.i("scheduleVo", "scheduleVO的值="+scheduleVO);
//								
//								if(scheduleDate!=null){
//									//intent.putStringArrayListExtra("scheduleDate", scheduleDate);
//									intent.setClass(CalendarActivity.this,ScheduleInfoDetailActivity.class);
//									intent.putStringArrayListExtra("scheduleDate", scheduleDate);
//							        intent.putExtra("scheduleVO", scheduleVO);
//							        
//							        Log.i("scheduleVo", "往intent存放的值"+scheduleVO);
//										  startActivity(intent);
//									
//								}
//							}
//							
//						}
//					}); 
	                  
	                  
	                  //长按删除
	        
	                  
				  }
			}
		});
		gridView.setLayoutParams(params);
	}
	
	
	
	

/**
 * 
 * 被标记有相应的日程安排*/
	
	
	
	
	

	private class CalendarMarkedAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			inflater=LayoutInflater.from(CalendarActivity.this);
			ViewHolder holder=new ViewHolder();
			if(convertView==null){
				convertView=getLayoutInflater().inflate(R.layout.schedule_detail_item, null);
				holder.itemTime=(TextView) convertView.findViewById(R.id.itemTime);
				holder.itemContent=(TextView) convertView.findViewById(R.id.itemContent);
				
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			//绑定数据
			  //如果被标记，则加载相应的日程信息列表
            if(scheduleIDs != null && scheduleIDs.length > 0){
          	  
          	  
	              		//list.setAdapter(new MyAdapter());
	              		View inflate=getLayoutInflater().inflate(R.layout.schedule_detail_item, null);
                  //通过arraylist绑定数据导listview中去
	              		ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String, String>>();  
						ScheduleDAO dao=new ScheduleDAO(CalendarActivity.this);
						 String time="";
	                	  String content="";
              	  for(int i=0;i<scheduleIDs.length;i++){
              	  scheduleVO=dao.getScheduleByID(CalendarActivity.this,Integer.parseInt(scheduleIDs[i]));
              	 time="";
              	 content="";
              	  
              	  time=dateInfo+" "+scheduleVO.getTime();
              	  content=scheduleVO.getScheduleContent();
              		
              	 
              	 
              		  HashMap<String, String> map=new HashMap<String, String>();
              		  map.put("date", time);
              		  map.put("content", content);
    	              	  Data.add(map);
    	              	  
              	  }
              	 String  from[]={"date","content"};
              	  int to[]={R.id.itemTime,R.id.itemContent};
              	  
              	  SimpleAdapter adapter=new SimpleAdapter(CalendarActivity.this, Data, R.layout.schedule_detail_item, from, to);
              	  
              	  list.setAdapter(adapter);
              	  
              	  //点击list的item相应事件
//              	  list.setOnClickListener(CalendarActivity.this);
//              	  list.setOnLongClickListener(CalendarActivity.this);
      
              	  
                }else{ //如果没有标记位直接则跟换为“暂无安排”
               
              	  
              	  schdule_tip.setText("暂无安排");
              	  listView.setVisibility(View.INVISIBLE);
              	  
//	                  Intent intent = new Intent();
//	                  intent.putExtra("top_Time", dateInfo);
//	                  Log.i("calendar", "calendar ifo-->"+dateInfo);
//	                  intent.putStringArrayListExtra("scheduleDate", scheduleDate);
//	                  intent.setClass(CalendarActivity.this,ScheduleDetailsNoDataActivity.class);
//	                  startActivity(intent);
                }
                
			
			return convertView;
		}
		
	}
	
	 public static class ViewHolder {
			TextView itemTime;
			TextView itemContent;
		}

	
	 @Override
		protected void onRestart() {
			int xMonth = jumpMonth;
	    	int xYear = jumpYear;
	    	int gvFlag =0;
	    	jumpMonth = 0;
	    	jumpYear = 0;
	    	addGridView();   //添加一个gridView
	    	year_c = Integer.parseInt(currentDate.split("-")[0]);
	    	month_c = Integer.parseInt(currentDate.split("-")[1]);
	    	day_c = Integer.parseInt(currentDate.split("-")[2]);
	    	calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView,gvFlag);
			flipper.removeViewAt(0);
			super.onRestart();
		}

	public boolean onLongClick(View v) {
		scheduleVO_del=  (ScheduleVO) v.getTag();
		Dialog alertDialog = new AlertDialog.Builder(CalendarActivity.this). 
        setMessage("删除日程信息？"). 
        setPositiveButton("确定", new DialogInterface.OnClickListener() { 
             
            public void onClick(DialogInterface dialog, int which) { 
            	dao.delete(scheduleVO_del.getScheduleID());
            	ScheduleViewAddActivity.setAlart(CalendarActivity.this);
//            	if(builder!=null&&builder.isShowing()){
//            		builder.dismiss();
//            	}
            }
 
        }). 
        setNegativeButton("取消", new DialogInterface.OnClickListener() { 
             
            public void onClick(DialogInterface dialog, int which) { 
           	 
            } 
        }). 
        create(); 
		alertDialog.show();

		return false;
	}
	
	
	
	/**
	 * 根据日期的年月日返回阴历日期
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String getLunarDay(int year, int month, int day) {
		lcCalendar=new LunarCalendar();
		String lunar = lcCalendar.getLunarDate(year, month, day, true);
		// {由于在取得阳历对应的阴历日期时，如果阳历日期对应的阴历日期为"初一"，就被设置成了月份(如:四月，五月。。。等)},所以在此就要判断得到的阴历日期是否为月份，如果是月份就设置为"初一"
		if (lunar.substring(1, 2).equals("月")) {
			lunar = "初一";
		}
//		
//		Log.i("lunar", lunar);
//		String lunarDay=lunar.substring(2);
		
		return lunar;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}