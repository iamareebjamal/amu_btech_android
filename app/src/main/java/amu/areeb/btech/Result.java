package amu.areeb.btech;
import org.jsoup.Connection;
import org.jsoup.nodes.*;
import org.jsoup.*;
import org.jsoup.select.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import android.os.*;
import android.text.*;

public class Result implements Parcelable
{

	@Override
	public int describeContents()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		// TODO: Implement this method
	}
	
	
	private int timeout_count;
	private String name, CPI, SPI, subjects[], marks[];
	public static String WRONG_INFO = "Faculty_No or En_No is incorrect!", NO_RESULT = "This Result has not been declared yet!", SUCCESS="Result downloaded successfully!", UNKNOWN_ERROR="Couldn't get result due to unknown Error!", TIMEOUT = "Timeout while connecting to website\nPlease check your connection and try again", EXCEPTION="iaj.Exception:";
	
	public Elements getTable(Document doc, int index){
		return doc.select("table").get(index).select("tr");
	}

	public String getText(Element ele, String tag, int index){
		return ele.select(tag).get(index).text();
	}

	private void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	private void setCPI(String cpi){
		this.CPI = cpi;
	}

	private void setSPI(String spi){
		this.SPI = spi;
	}

	public String getCPI(){
		return this.CPI;
	}

	public String getSPI(){
		return this.SPI;
	}

	private void setSubjects(String[] subs){
		this.subjects = subs;
	}

	private void setMarks(String[] marks){
		this.marks = marks;
	}

	public String[] getSubjects(){
		return this.subjects;
	}

	public String[] getMarks(){
		return this.marks;
	}
	
	private String toTitleCase(String givenString) {
		
		String[] arr = givenString.toLowerCase().split(" ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
		}
		return sb.toString().trim();
	}
	
	private String fixMarks(String mark){
		return mark.length()<2 ? TextUtils.isDigitsOnly(mark) ? "0" + mark : mark + "b" : mark;
	}

	public String getResultString(String url, String enNo, String facNo){
		StringBuffer buffer = new StringBuffer();
		try {
			timeout_count = 0;
			Document doc  = Jsoup.connect(url).data("EN", enNo, "FN", facNo,"submit","submit").method(Connection.Method.POST).timeout(2000).post();
			String page = doc.toString();
			if (doc.toString().contains(WRONG_INFO))
			{
				buffer.append(WRONG_INFO);
			}
			else if (page.contains(NO_RESULT)){
				buffer.append(NO_RESULT);
			}
			else if (page.contains("CPI"))
			{
				Elements subTable = getTable(doc, 1);
				Element infTable = getTable(doc, 2).get(1);
				String name = getText(infTable, "th", 2);
				String spi = getText(infTable, "th", 4);
				String cpi = getText(infTable, "th", 5);
				
				setName(toTitleCase(name));
				setSPI(spi);
				setCPI(cpi);
				List<String> mSub = new ArrayList<String>();
				List<String> mMarks = new ArrayList<String>();
				for (Element subs : subTable)
				{
					try
					{
						String sub = getText(subs, "td", 0);
						String mark  = String.format("%s%11s%9s%10s  ", fixMarks(getText(subs, "td", 1)), fixMarks(getText(subs, "td", 2)), fixMarks(getText(subs, "td", 3)), getText(subs, "td", 5));
						mSub.add(sub);
						mMarks.add(mark);
					}
					catch (IndexOutOfBoundsException e)
					{
						e.printStackTrace();
						continue;
					}
				}
				if(mSub!=null&&mMarks!=null){
					setMarks(mMarks.toArray(new String[mMarks.size()]));
					setSubjects(mSub.toArray(new String[mSub.size()]));
					buffer.append(SUCCESS);
				}
				else{
					buffer.append(UNKNOWN_ERROR);
				}
			}
			else
			{
				buffer.append(UNKNOWN_ERROR);
			}

		}
		catch (SocketTimeoutException e){
			String site_down = "";
			if(timeout_count > 3){
				site_down = "\n\nSite appears to be down or inaccessible due to connection error. Please try after some time";
			}
			buffer.append(TIMEOUT+site_down);
			timeout_count++;
		}
		catch (Throwable t)
		{
			buffer.append(EXCEPTION+t.toString());
			t.printStackTrace();
		}
		return buffer.toString();
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>(){
		@Override
		public Result createFromParcel(Parcel in) {
			return new Result();
		}
		@Override
		public Result[] newArray (int size) {
			return new Result[size];
		}
	};
}
