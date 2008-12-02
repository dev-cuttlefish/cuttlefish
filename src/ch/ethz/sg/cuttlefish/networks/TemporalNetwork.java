/*
    
    Copyright (C) 2008  Markus Michael Geipel

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.ethz.sg.cuttlefish.networks;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class TemporalNetwork extends BrowsableNetwork {
	 private ArrayList<Date> dates;
	 //private int dateIndex= -1;
	 private int dateIndex= 0;
	private String dateCondition = " 1 ";
	private String dateIntervalCondition = " 1 ";
	 
		public Date getDate() {
			return getDate(dateIndex);
		}
		
		public void setLatestDate() {
			setDate(getDates().size()-1);
		}
		
		public Date getDate(int index) {
			if(dateIndex>=0){
				return dates.get(index);
			}else{
				return null;
			}
		}
		
		public ArrayList<Date> getDates(){
			return dates;
		}
		
		protected int getDateIndex(){
			return dateIndex;
		}
		
		protected void setDates(ArrayList<Date> dates){
			this.dates = dates;
			//dateIndex = -1;
		}
		
		public void setDate(int index){
			dateIndex= index;
			
			
			if(getDate()==null){
				dateCondition=" 1 ";
				dateIntervalCondition = " 1 ";
			}else{
				dateCondition=" date='" + getDate() + "' ";
				if(getDateIndex()<1){
					dateIntervalCondition = " date <= '" + getDate() + "' ";
				}else{
					dateIntervalCondition = " date > '" + getDate(getDateIndex()-1) + "' AND " + 
										 " date <= '" + getDate(getDateIndex()) + "' ";
				}
			}
			
			onDateChange();
			//System.out.println("date="+dateIndex);
			
		}
		
		public void setDate(Date date) {
			
			if(date==null || !dates.contains(date)){
					setDate(-1);
			}else{
					setDate(dates.indexOf(date));
			}
	
		}
		
		protected abstract void onDateChange();
		
		
		public ArrayList<Date> generateDates(Date start, Date end, int stepType, int steps) {
			//DateFormat format= new SimpleDateFormat("yyyy-MM-dd");
		//	System.out.println("generating dates from " + format.format(start) + " to " + format.format(end));
			
			
			ArrayList<Date> dates = new ArrayList<Date>();
			
			try{
				GregorianCalendar now = new GregorianCalendar();
				now.setTime(start);
			
				
				
			//	System.out.println(format.format(now.getTime()));
				dates.add(new java.sql.Date(now.getTimeInMillis()));
				while(now.getTime().before(end)){
					
					now.add(stepType, steps);
					dates.add(new java.sql.Date(now.getTimeInMillis()));
				//	System.out.println(format.format(now.getTime()));
				}
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return dates;
		}

		protected String getDateCondition() {
			return dateCondition;
		}

		protected String getDateIntervalCondition() {
			return dateIntervalCondition;
		}
		
		/*
		public static final ArrayList<String> generateDates(String start, String end, String dateFormat, int stepType, int steps){
			
					
			try{
		        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				        
		        GregorianCalendar cStart = new GregorianCalendar();
		        GregorianCalendar cNow = new GregorianCalendar();
		        Date temp = format.parse(start);
		        cStart.setTime(temp);
		        cStart.setTime(temp);
		        GregorianCalendar cEnd = new GregorianCalendar();
		        cStart.setTime(format.parse(end));
		        
		        
		        
			}catch (Exception e) {
				e.printStackTrace();
				dates.clear();
			}

			
			
			
			return dates;
		}*/
}
