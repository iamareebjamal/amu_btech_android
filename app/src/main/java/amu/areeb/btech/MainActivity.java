package amu.areeb.btech;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import java.util.*;

import android.content.ClipboardManager;

public class MainActivity extends Activity
{

	

	Window window;
	EditText fn;
	EditText en;
	RelativeLayout bg;
	LinearLayout marksLayout;
	
	ProgressDialog pd;
	Result mResult;
	
	int RED = R.color.red, RED_DARK = R.color.red_dark, BLUE = R.color.blue, BLUE_DARK = R.color.blue_dark, GREEN = R.color.green, GREEN_DARK = R.color.green_dark, YELLOW = R.color.yellow, YELLOW_DARK = R.color.yellow_dark, ORANGE = R.color.orange, ORANGE_DARK = R.color.orange_dark, PURPLE = R.color.purple, PURPLE_DARK = R.color.purple_dark, TEAL = R.color.teal, GREY = R.color.grey;
	
	////////////// Result Saving Workaround ////////////
	
	private String saveStatus, saveResult, saveCPI, saveSPI, saveName, saveMarks[], saveSubjects[];
	
	private void setStatus(String status){
		this.saveStatus = status;
	}
	
	private void setResult(String result){
		this.saveResult = result;
	}
	
	private void setCPI(String CPI){
		this.saveCPI = CPI;
	}

	private void setSPI(String SPI){
		this.saveSPI = SPI;
	}
	
	private void setName(String name){
		this.saveName = name;
	}
	
	private void setMarks(String[] marks){
		this.saveMarks = marks;
	}
	
	private void setSubjects(String[] subjects){
		this.saveSubjects = subjects;
	}
	
	private String getStatus(){
		return saveStatus;
	}

	private String getResult(){
		return saveResult;
	}

	private String getCPI(){
		return saveCPI;
	}

	private String getSPI(){
		return saveSPI;
	}

	private String getName(){
		return saveName;
	}

	private String[] getMarks(){
		return saveMarks;
	}

	private String[] getSubjects(){
		return saveSubjects;
	}
	
	private void populate(Result result, String result_string){
		setResult(result_string);
		setCPI(result.getCPI());
		setSPI(result.getSPI());
		setName(result.getName());
		setMarks(result.getMarks());
		setSubjects(result.getSubjects());
	}
	
	private void populate(String result, String name, String cpi, String spi, String marks[], String subjects[]){
		setResult(result);
		setCPI(cpi);
		setSPI(spi);
		setName(name);
		setMarks(marks);
		setSubjects(subjects);
	}
	
	
	/////////////            End           ////////////
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);
		
		window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		statusBarColor(PURPLE_DARK);
		marksLayout = (LinearLayout) findViewById(R.id.marksLayout);
		
		fn = (EditText) findViewById(R.id.fnEdt);
		en = (EditText) findViewById(R.id.enEdt);
		
		fn.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(8)});
		en.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(6)});
		
		fn.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event){
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
						//if the enter key was pressed, then hide the keyboard and do whatever needs doing.
						final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						en.requestFocus();
						en.postDelayed(new Runnable(){

							@Override
							public void run()
							{
								// TODO: Implement this method
								imm.showSoftInput(en, InputMethodManager.SHOW_FORCED);
								
							}
							
							
						}, 1);
						return true;
						
				} /*else if (keyCode == KeyEvent.KEYCODE_BACK) {
					MainActivity.this.finish();
				}*/
					return false;
			}
				
		});
		en.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event){
					if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
						//if the enter key was pressed, then hide the keyboard and do whatever needs doing.
						getResult(v);
						return true;
					} /*else if (keyCode == KeyEvent.KEYCODE_BACK) {
						MainActivity.this.finish();
					}*/
					return false;
			}

		});
		
		
		bg = (RelativeLayout) findViewById(R.id.bg);
		
		pd = new ProgressDialog(this);
		pd.setMessage("Getting Result");
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);
    }
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		outState.putString("result", getResult());
		if(getSubjects()!=null){
        	outState.putString("name", getName());
			outState.putString("cpi", getCPI());
			outState.putString("spi", getSPI());
			outState.putStringArray("marks", getMarks());
			outState.putStringArray("subjects", getSubjects());
		}
    }
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		try{
			String result = savedInstanceState.getString("result");
			String name = savedInstanceState.getString("name");
			String cpi = savedInstanceState.getString("cpi");
			String spi = savedInstanceState.getString("spi");
			String[] marks = savedInstanceState.getStringArray("marks");
			String[] subjects = savedInstanceState.getStringArray("subjects");
			if(subjects!=null){
				try{
					populate(result, name, cpi, spi, marks, subjects);
					manageResult();
				} catch (NullPointerException e){
					e.printStackTrace();
				}
			}
			
		} catch (BadParcelableException e){
			e.printStackTrace();
		}
	}

	/*@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		Toast.makeText(this, "Asta La Vista Baby", Toast.LENGTH_SHORT).show();
	}*/
	
	
	public void celebrateCPI(String CPI){
		int BG = 0, ST = 0;
		try{
			float cpi = Float.parseFloat(CPI);
			if (cpi >= 8.5){
				BG = BLUE;
				ST = BLUE_DARK;
			} else if (cpi >= 5.259){
				BG = GREEN;
				ST = GREEN_DARK;
			} else if (cpi >= 3.185){
				BG = YELLOW;
				ST = YELLOW_DARK;
			} else if (cpi >= 2.402){
				BG = ORANGE;
				ST = ORANGE_DARK;
			} else {
				BG = RED;
				ST = RED_DARK;
			}
		} catch (NumberFormatException e){
			BG = RED;
			ST = RED_DARK;
		}
		changeColor(BG);
		statusBarColor(ST);
	}
	
	public void changeColor(int color){
		int colorFrom = ((ColorDrawable) bg.getBackground()).getColor();
		int colorTo = getResources().getColor(color);
		ValueAnimator bgAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
		bgAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator animator){
				bg.setBackgroundColor((int)animator.getAnimatedValue());
			}
		});
		
		
		bgAnimation.setDuration(2000).start();
	}
	
	public void statusBarColor(int color){
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			int colorStatusFrom = window.getStatusBarColor();
			int colorStatusTo = getResources().getColor(color);
			ValueAnimator stAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusFrom, colorStatusTo);
			stAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
					@Override
					public void onAnimationUpdate(ValueAnimator animator){
						window.setStatusBarColor((int)animator.getAnimatedValue());
						window.setNavigationBarColor((int)animator.getAnimatedValue());
					}
				});

			stAnimation.setDuration(2000).start();
		}
	}
	
	public void removeCards(){
		marksLayout.removeAllViews();
	}
	
	public RelativeLayout addCard(String info, String desc){
		RelativeLayout card = (RelativeLayout) getLayoutInflater().from(this).inflate(R.layout.card_layout, null);
		TextView inf = (TextView) card.findViewById(R.id.infTv);
		TextView des = (TextView) card.findViewById(R.id.desTv);
		inf.setText(info);
		des.setText(desc);
		marksLayout.addView(card);
		return card;
	}
	
	public RelativeLayout addSubjectCard(String info, String desc, int color){
		RelativeLayout card = (RelativeLayout) getLayoutInflater().from(this).inflate(R.layout.card_layout, null);
		TextView inf = (TextView) card.findViewById(R.id.infTv);
		TextView des = (TextView) card.findViewById(R.id.desTv);
		card.setAlpha(0.8f);
		inf.setText(info);
		inf.setTextColor(getResources().getColor(color));
		des.setTypeface(null, Typeface.ITALIC);
		des.setText(desc, BufferType.SPANNABLE);
		Spannable s = (Spannable) des.getText();
		s.setSpan(new ForegroundColorSpan(getResources().getColor(getGradeColor(desc))), desc.length()-4, desc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		marksLayout.addView(card);
		copyText(card, info, desc);
		return card;
	}
	
	public void modify(RelativeLayout rl){
		TextView tv = (TextView) rl.findViewById(R.id.desTv);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTypeface(null, Typeface.ITALIC);
	}
	
	public int getGradeColor(String mark){
		String grade = mark.substring(mark.length()-4, mark.length()-2);
		if (grade.contains("A")){
			return BLUE;
		} else if(grade.contains("B")){
			return GREEN;
		} else if(grade.contains("C")){
			return YELLOW;
		} else if(grade.contains("D")){
			return ORANGE;
		} else if(grade.contains("E")){
			return RED;
		}
		return RED;
	}
	
	public boolean isFacultyNumber(String fcNo){
		if(fcNo.length()!=8){
			return false;
		}
		String fYear = fcNo.substring(0,2);
		String fBranch = fcNo.substring(2, 5);
		String fRNo = fcNo.substring(5);
		
		if(TextUtils.isDigitsOnly(fYear)&&TextUtils.isDigitsOnly(fRNo)&&fBranch.matches("^[ACEKLMP][EKR][B]$+")&&Integer.parseInt(fYear)<=Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2))){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isEnrolmentNumber(String enNo){
		if(enNo.length()!=6){
			return false;
		}
		String rgNo = enNo.substring(0,2);
		String RNo = enNo.substring(2);

		if(TextUtils.isDigitsOnly(RNo)&&rgNo.matches("^[FG][B-Z]$+")){
			return true;
		}else{
			return false;
		}
	}
	
	public void getResult(View view){
		mResult = new Result();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		String enNo = en.getText().toString();
		String facNo = fn.getText().toString();
		if(!isFacultyNumber(facNo)){
			fn.setError("Invalid Faculty Number");
			return;
		}
		if(!isEnrolmentNumber(enNo)){
			en.setError("Invalid Enrolment Number");
			return;
		}
		pd.show();
		//bg.setBackgroundColor(Color.parseColor("#33b5e5"));
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = conn.getActiveNetworkInfo();
		if (net != null){
			new GetResult().execute("http://ctengg.amu.ac.in/result_btech.php", enNo, facNo);
		} else {
			pd.dismiss();
			Toast.makeText(this, "No Connection", Toast.LENGTH_SHORT).show();
		}
	}
	
	static int timeout=0;
	public void manageResult(){
		String result = getResult();
		removeCards();
		String cpi = "fail";
		if (result.equals(Result.SUCCESS)){
			timeout=0;
			cpi = getCPI();
			String spi = getSPI();
			String[] subs = getSubjects();
			String[] marks = getMarks();
			String name = getName();
			addCard("Name : ", name);
			String row = String.format("%s%8s%8s%8s", "Sessional", "Final", "Total", "Grade");
			modify(addCard("Subject:", row));
			int[] colors = {BLUE, RED, GREEN, ORANGE, YELLOW, PURPLE, TEAL, RED_DARK, ORANGE_DARK, GREEN_DARK, YELLOW_DARK, BLUE_DARK};
			for(int i = 0; i < subs.length; i++){
				addSubjectCard(subs[i], marks[i], colors[i]); 
			}
			copyDialog(addCard("SPI : " + spi, "CPI : " + cpi), cpi, spi);
			
		} else if (result.equals(Result.WRONG_INFO)){
			addCard("The Enrolment Number or Faculty Number you entered is either wrong or doesn't match the database.\nPlease try again.", "");
		} else if (result.contains(Result.NO_RESULT)){
			addCard(result, "");
		} else if (result.contains(Result.TIMEOUT)){
			String error = result;
			if(timeout>3)
				error+="\n\nEither connection is weak or busy!\nOr the site appears to be down. Please try after some time";
			addCard(result, "");
			timeout++;
		} else if (result.contains(Result.UNKNOWN_ERROR)){
			addCard(result, "");
		} else if (result.contains(Result.EXCEPTION)){
			Toast.makeText(this, result.substring(Result.EXCEPTION.length()), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Unhandled Error: " + result, Toast.LENGTH_SHORT).show();
		}
		celebrateCPI(cpi);
	}
	
	
	public void copyText(View view, final String subject, final String marks){
		
		view.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					String items[] ={"Simple", "Advance"};
					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
					alert.setTitle("Copy Style");
					alert.setItems(items, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								String label = subject, text = "", splitMark[] = splitMarks(marks);
								switch(p2){
									case 0:
										text = "I got " + splitMark[2] + " ("  + splitMark[3] + ")" +" marks in " + subject + "!";
										break;
									case 1:
										text = subject + "\n\nSessional Marks:   " + splitMark[0] +"\nFinal Marks:            " + splitMark[1] + "\nTotal Marks:            " + splitMark[2] + "\nGrade:                       " + splitMark[3];
								}
								ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
								ClipData cdat = ClipData.newPlainText(label, text);
								clip.setPrimaryClip(cdat);
								Toast.makeText(getApplicationContext(), "Marks Copied", Toast.LENGTH_SHORT).show();
							}
						});
					alert.show();
				}

			});
		view.setOnLongClickListener(new View.OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					// TODO: Implement this method
					Toast.makeText(getApplicationContext(), "Copy " + subject + " Marks", Toast.LENGTH_SHORT).show();
					return true;
				}
			});
	}
	
	public void copyDialog(final View view, final String cpi, final String spi){
		
		view.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					String items[] = {"CPI : " + cpi, "SPI : " + spi};

					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
					alert.setTitle("What to Copy?");
					alert.setItems(items, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								String label = "", text = "";
								switch(p2){
									case 0:
										label = "CPI";
										text = "My CPI is " + cpi;
										break;
									case 1:
										label = "SPI";
										text = "My SPI is " + cpi;
										break;
								}
								ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
								ClipData cdat = ClipData.newPlainText(label, text);
								clip.setPrimaryClip(cdat);
								Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
							}
						});
					alert.show();
				}
			});
		view.setOnLongClickListener(new View.OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					// TODO: Implement this method
					Toast.makeText(getApplicationContext(), "Copy CPI/SPI", Toast.LENGTH_SHORT).show();
					return true;
				}
			});
	}
	
	public static String[] splitMarks(String marks){
		String splitted[] = marks.trim().replaceAll(" +", " ").split(" ");
		return splitted;
	}
	
	private class GetResult extends AsyncTask<String, Void, String>
	{
		
		
        @Override
        protected String doInBackground(String... strings) {
            return mResult.getResultString(strings[0], strings[1], strings[2]);
        }
		
        @Override
        protected void onPostExecute(String s)
		{
            super.onPostExecute(s);
			//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
			pd.dismiss();
			populate(mResult, s);
			manageResult();
        }
	}
}


