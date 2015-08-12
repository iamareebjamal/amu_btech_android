package amu.areeb.btech;
import android.os.*;
import org.jsoup.nodes.*;
import org.jsoup.*;
import java.io.*;
import android.content.*;
import android.app.*;
import java.util.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import android.widget.*;
import android.util.*;
import android.net.*;

public class UpdateChecker{
	String raw;
	private String versionName, appName, changelog, mVersionName, mAppName, link, size;
	private int versionCode=0, mVersionCode=0;
	private Context cntxt;
	PackageManager pm;
	
	public UpdateChecker(Context ctx){
		this.cntxt = ctx;
		pm = cntxt.getPackageManager();
	}
	
	private String fixURL(String url){
		return url.contains("http://")||url.contains("https://")?url:"http://"+url;
	}
	
	private String formUpdate(String rawString){
		raw = rawString;
		String pairs[] = rawString.trim().replaceAll("\\{|\\}","").split(",");
		//Toast.makeText(cntxt, rawString, Toast.LENGTH_SHORT).show();
		try{
			mVersionName = pm.getPackageInfo(cntxt.getPackageName(), 0).versionName;
			mVersionCode = pm.getPackageInfo(cntxt.getPackageName(), 0).versionCode;
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		
		HashMap<String, String> hm = new HashMap<String, String>();
		for (String pair : pairs){
			String key = pair.split(":")[0].trim().replace("'", "");
			String value = pair.split(":")[1].trim().replace("'", "");
			hm.put(key, value);
		}
		if (verifyHash(hm)){
			versionCode = Integer.parseInt(hm.get("app_version_code"));
			versionName = hm.get("app_version");
			changelog = hm.get("changelog");
			link = hm.get("link");
			size = hm.get("size"); 
			if(versionCode>mVersionCode)
				return "Installed Version : \t" + mVersionName + "\nUpdate Version : \t" + versionName + "\n\nSize : " + size + "\n\nWhat's New : \n\n" + changelog;
			else
				return "Error No Update";
		} else
			return "Error";
	}
	
	private boolean verifyHash(HashMap hm){
		try{
			if (hm.get("app_name")!=null&&hm.get("app_version")!=null&&hm.get("app_version_code")!=null&&hm.get("link")!=null&&hm.get("changelog")!=null&&hm.get("size")!=null)
				return true;
			else
				return false;
		} catch (Exception e){
			return false;
		}
	}
	
	
	private String getRawUpdate(){
		try
		{
			Document doc = Jsoup.connect("https://gist.githubusercontent.com/iamareebjamal/a659f74f0d09dc6038ee/raw/android_app_update_info").get();
			String g = doc.select("body").text().toString().trim();
			String[] resultArray = g.replaceAll("^[^{]*|[^}]*$","").split("(?<=\\})[^{]*");
			String selected="";
			ApplicationInfo appInfo = cntxt.getApplicationInfo();
			mAppName = appInfo.loadLabel(pm).toString();
			
			for (int i=0; i < resultArray.length; i++){
				if (resultArray[i].contains(mAppName)){
					selected = resultArray[i];
				}
			}
			return selected;
		}
		catch (IOException e)
		{
			return "Error";
		}
	}
	
	public void getUpdate(){
		new CheckUpdate().execute("");
	}
	
	private class CheckUpdate extends AsyncTask<String, Void, String>
	{

        @Override
        protected String doInBackground(String... strings) {
			try{
            	return formUpdate(getRawUpdate());
			}catch(Exception e){
				e.printStackTrace();
				return "Error: " + e.toString();
			}
		}

        @Override
        protected void onPostExecute(String s)
		{
            super.onPostExecute(s);
			AlertDialog.Builder diag = new AlertDialog.Builder(cntxt);
			diag.setTitle("New Update Found");
			diag.setMessage(s);
			diag.setPositiveButton("Update", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
						cntxt.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fixURL(link))));
					}
				});
			diag.setNegativeButton("Later", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
						p1.dismiss();
					}
				});
			diag.setNeutralButton("Copy URL", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
						MainActivity.copy(cntxt, "APK URL", fixURL(link));
					}
				});
			if(s!=null&&!s.contains("Error"))
				diag.show();
			else
				Log.e("Update Error", s);
			//Toast.makeText(cntxt, raw, Toast.LENGTH_SHORT).show();
        }
	}
	
}
