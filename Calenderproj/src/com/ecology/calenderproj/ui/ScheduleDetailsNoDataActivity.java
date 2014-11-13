package com.ecology.calenderproj.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ecology.calenderproj.R;

public class ScheduleDetailsNoDataActivity extends Activity implements OnClickListener {

	private Button add,quit;
	private TextView today,dayofweek,lunarTime;
	private ListView schedulelist;
	private String[] scheduleIDs;
	private Intent mIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.schedule_details_no);
		
		initView();
		
	}


	
	
	private void initView() {
	 add=(Button)this.findViewById(R.id.addBtn);
  		 quit=(Button) this.findViewById(R.id.backBtn);
  		today=(TextView) this.findViewById(R.id.todayDate);
  		lunarTime=(TextView)this.findViewById(R.id.lunarTime);
  		dayofweek=(TextView)this.findViewById(R.id.dayofweek);
  		//schedulelist=(ListView)this.findViewById(R.id.schedulelist);
  		
  		
  		String dadaInfo=getIntent().getStringExtra("top_Time");
  		
  		today.setText(dadaInfo);
  		
  		quit.setOnClickListener(this);
  		add.setOnClickListener(this);
  		//schedulelist.setAdapter(new MyScheduleAdapter());
  		//schedulelist.setOnItemClickListener(this);
		
	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			 mIntent = new Intent(ScheduleDetailsNoDataActivity.this, ScheduleViewAddActivity.class);
           // intent.putStringArrayListExtra("scheduleDate", scheduleDate);
            startActivity(mIntent);
			break;
			
          case R.id.btn_quit:
        	  
        	  mIntent = new Intent(ScheduleDetailsNoDataActivity.this, CalendarActivity.class);
			//ScheduleDetailsNoDataActivity.this.finish();
			break;

		default:
			break;
		}
		
	}






	
	
	
}
