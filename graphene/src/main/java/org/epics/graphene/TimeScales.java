/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;

/**
 *
 * @author carcassi
 */
public class TimeScales {
    
    final public static int HOUR_FIELD_ID = GregorianCalendar.HOUR_OF_DAY;
    final public static int FIRST_HOUR = 0;
    final public static int DAY_FIELD_ID = GregorianCalendar.DAY_OF_WEEK;
    final public static int FIRST_DAY = 1;
    final public static int WEEK_FIELD_ID = GregorianCalendar.WEEK_OF_MONTH;
    final public static int FIRST_WEEK = 1;
    
    public static TimeScale linearAbsoluteScale() {
        return new LinearAbsoluteTimeScale();
    }
    
    static class TimePeriod {
        public int fieldId;
        public double amount;

        public TimePeriod(int fieldId, double amount) {
            this.fieldId = fieldId;
            this.amount = amount;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + this.fieldId;
            hash = 59 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TimePeriod other = (TimePeriod) obj;
            if (this.fieldId != other.fieldId) {
                return false;
            }
            if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "TimePeriod{" + "fieldId=" + fieldId + ", amount=" + amount + '}';
        }
        
    }

    static TimePeriod nextUp(TimePeriod period) {
	//TODO nanoseconds rounding up
	switch(period.fieldId) {
            case GregorianCalendar.MILLISECOND:
                if (period.amount < 2) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 2);
                }
                if (period.amount < 5) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 5);
                }
                if (period.amount < 10) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 10);
                }
                if (period.amount < 20) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 20);
                }
                if (period.amount < 50) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 50);
                }
                if (period.amount < 100) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 100);
                }
                if (period.amount < 200) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 200);
                }
                if (period.amount < 500) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 500);
                }
                return new TimePeriod(GregorianCalendar.SECOND, 1);
            case GregorianCalendar.SECOND:
                if (period.amount < 2) {
                    return new TimePeriod(GregorianCalendar.SECOND, 2);
                }
                if (period.amount < 5) {
                    return new TimePeriod(GregorianCalendar.SECOND, 5);
                }
                if (period.amount < 10) {
                    return new TimePeriod(GregorianCalendar.SECOND, 10);
                }
                if (period.amount < 15) {
                    return new TimePeriod(GregorianCalendar.SECOND, 15);
                }
                if (period.amount < 30) {
                    return new TimePeriod(GregorianCalendar.SECOND, 30);
                }
                return new TimePeriod(GregorianCalendar.MINUTE, 1);
	    case GregorianCalendar.MINUTE:
		if ( period.amount < 2 ) {
		    return new TimePeriod( GregorianCalendar.MINUTE , 2 );
		}
		if ( period.amount < 5 ) {
		    return new TimePeriod( GregorianCalendar.MINUTE , 5 );
		}
		if ( period.amount < 10 ) {
		    return new TimePeriod( GregorianCalendar.MINUTE , 10 );
		}
		if ( period.amount < 15 ) {
		    return new TimePeriod( GregorianCalendar.MINUTE , 15 );
		}
		if ( period.amount < 30 ) {
		    return new TimePeriod( GregorianCalendar.MINUTE , 30 );
		}
		return new TimePeriod( HOUR_FIELD_ID, 1 );
	    case HOUR_FIELD_ID:
		if ( period.amount < 2 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 2 );
		}
		if ( period.amount < 3 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 3 );
		}
		if ( period.amount < 6 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 6 );
		}
		if ( period.amount < 12 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 12 );
		}
		
		//*MC why is this necessary? otherwise, we get error
		if ( period.amount < 24 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 24 );
		}
		return new TimePeriod( DAY_FIELD_ID , 1 );
	    case DAY_FIELD_ID:
		if ( period.amount < 2 ) {
		    return new TimePeriod( DAY_FIELD_ID , 2 );
		}
		if ( period.amount < 4 ) {
		    return new TimePeriod( DAY_FIELD_ID , 4 );
		}
		return new TimePeriod( WEEK_FIELD_ID , 1 );
	    case WEEK_FIELD_ID:
		if ( period.amount < 2 ) {
		    return new TimePeriod( WEEK_FIELD_ID , 2 );
		}
		return new TimePeriod( GregorianCalendar.MONTH , 1 );
	    case GregorianCalendar.MONTH:
		if ( period.amount < 2 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 2 );
		}
		if ( period.amount < 4 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 4 );
		}
		if ( period.amount < 8 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 8 );
		}
		return new TimePeriod( GregorianCalendar.YEAR , 1 );
	    case GregorianCalendar.YEAR:
		return new TimePeriod( GregorianCalendar.YEAR , period.amount/4+1 );
        }
        return null;
    }
    
    /**
     * Determines the time(s) that will be represented by reference lines on
     * a time graph.
     * 
     * @param timeInterval the interval of time spanning the duration of the
     * time graph
     * @param period the interval of time between each reference line
     * @return a list of times evenly spaced by the duration of <code>period</code>
     * and encompassing the duration of <code>timeInterval</code>
     */
    static List<Timestamp> createReferences(TimeInterval timeInterval, TimePeriod period) {
        Date start = timeInterval.getStart().toDate();
        Date end = timeInterval.getEnd().toDate();
        GregorianCalendar endCal = new GregorianCalendar();
        endCal.setTime(end);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(start);
        round(cal, period.fieldId);
        cal.set(period.fieldId, (cal.get(period.fieldId) / (int) period.amount) * (int) period.amount);
        List<Timestamp> references = new ArrayList<>();
        while (endCal.compareTo(cal) >= 0) {
            Timestamp newTime = Timestamp.of(cal.getTime());
            if (timeInterval.contains(newTime)) {
                references.add(newTime);
            }
            cal.add(period.fieldId, (int) period.amount);
        }
        return references;
    }

    static void round(GregorianCalendar cal, int field) {
        
        if (GregorianCalendar.MILLISECOND == field) {
            return;
        }
        cal.set(GregorianCalendar.MILLISECOND, 0);
        
        if (GregorianCalendar.SECOND == field) {
            return;
        }
        cal.set(GregorianCalendar.SECOND, 0);

        if (GregorianCalendar.MINUTE == field) {
            return;
        }
        cal.set(GregorianCalendar.MINUTE, 0);
	
	if ( HOUR_FIELD_ID == field ) {
	    return;
	}
	cal.set( HOUR_FIELD_ID , FIRST_HOUR );
	
	if ( DAY_FIELD_ID == field ) {
	    return;
	}
	cal.set(DAY_FIELD_ID , FIRST_DAY );
	
	if ( WEEK_FIELD_ID == field ) {
	    return;
	}
	
	//here, we are rounding down to the first week (i.e. the first day of
	//the month), so the day of the week and the week of the month no 
	//longer matter - we just set the day to be the first day of the month
	cal.set( GregorianCalendar.DAY_OF_MONTH , 1 );
	
	if ( GregorianCalendar.MONTH == field ) {
	    return;
	}
	
	cal.set( GregorianCalendar.MONTH , 0 );
	
	if ( GregorianCalendar.YEAR == field ) {
	    return;
	}
	cal.set( GregorianCalendar.YEAR , 0 );
	
        return;
    }
    
    static TimePeriod nextDown(TimePeriod period) {
        switch(period.fieldId) {
	    case GregorianCalendar.YEAR:
		return new TimePeriod( GregorianCalendar.YEAR , period.amount/4+1 );
	    case GregorianCalendar.MONTH:
		if ( period.amount > 8 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 8 );
		}
		if ( period.amount > 4 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 4 );
		}
		if ( period.amount > 2 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 2 );
		}
		if ( period.amount > 1 ) {
		    return new TimePeriod( GregorianCalendar.MONTH , 1 );
		}
		return new TimePeriod( WEEK_FIELD_ID , 2 );
	    case WEEK_FIELD_ID:
		if ( period.amount > 2 ) {
		    return new TimePeriod( WEEK_FIELD_ID , 2 );
		}
		if ( period.amount > 1 ) {
		    return new TimePeriod( WEEK_FIELD_ID , 1 );
		}
		return new TimePeriod( DAY_FIELD_ID , 3 );
	    case DAY_FIELD_ID:
		if ( period.amount > 3 ) {
		    return new TimePeriod( DAY_FIELD_ID , 3 );
		}
		if ( period.amount > 1 ) {
		    return new TimePeriod( DAY_FIELD_ID , 1 );
		}
		return new TimePeriod( HOUR_FIELD_ID , 12 );
	    case HOUR_FIELD_ID:
		if ( period.amount > 12 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 12 );
		}
		if ( period.amount > 8 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 8 );
		}
		if ( period.amount > 4 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 4 );
		}
		if ( period.amount > 2 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 2 );
		}
		if ( period.amount > 1 ) {
		    return new TimePeriod( HOUR_FIELD_ID , 1 );
		}
		return new TimePeriod( GregorianCalendar.MINUTE , 30 );
            case GregorianCalendar.MINUTE:
                return new TimePeriod(GregorianCalendar.SECOND, 30);
            case GregorianCalendar.SECOND:
                if (period.amount > 30) {
                    return new TimePeriod(GregorianCalendar.SECOND, 30);
                }
                if (period.amount > 15) {
                    return new TimePeriod(GregorianCalendar.SECOND, 15);
                }
                if (period.amount > 10) {
                    return new TimePeriod(GregorianCalendar.SECOND, 10);
                }
                if (period.amount > 5) {
                    return new TimePeriod(GregorianCalendar.SECOND, 5);
                }
                if (period.amount > 2) {
                    return new TimePeriod(GregorianCalendar.SECOND, 2);
                }
                if (period.amount > 1) {
                    return new TimePeriod(GregorianCalendar.SECOND, 1);
                }
                return new TimePeriod(GregorianCalendar.MILLISECOND, 500);
            case GregorianCalendar.MILLISECOND:
                if (period.amount > 500) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 500);
                }
                if (period.amount > 200) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 200);
                }
                if (period.amount > 100) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 100);
                }
                if (period.amount > 50) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 50);
                }
                if (period.amount > 20) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 20);
                }
                if (period.amount > 10) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 10);
                }
                if (period.amount > 5) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 5);
                }
                if (period.amount > 2) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 2);
                }
                if (period.amount > 1) {
                    return new TimePeriod(GregorianCalendar.MILLISECOND, 1);
                }
        }
        return new TimePeriod( GregorianCalendar.MILLISECOND , 1 );
	
	//TODO nanoseconds rounding down
    }
    
    static TimePeriod toTimePeriod(double seconds) {
	if ( seconds >= 36288000 ) {
	    return new TimePeriod( GregorianCalendar.YEAR , seconds/3024000 );
	}
	if ( seconds >= 3024000 ) {
	    return new TimePeriod( GregorianCalendar.MONTH , seconds/3024000 );
	}
	if ( seconds >= 604800 ) {
	    return new TimePeriod( WEEK_FIELD_ID , seconds/604800.0 );
	}
	if ( seconds >= 86400 ) {
	    return new TimePeriod( DAY_FIELD_ID , seconds/86400.0 );
	}
	if ( seconds >= 3600 ) {
	    return new TimePeriod( HOUR_FIELD_ID , seconds/3600.0 );
	}
        if (seconds >= 60) {
            return new TimePeriod(GregorianCalendar.MINUTE, seconds / 60.0);
        }
        if (seconds >= 1) {
            return new TimePeriod(GregorianCalendar.SECOND, seconds);
        }
        return new TimePeriod(GregorianCalendar.MILLISECOND, 1000*seconds);
    }
    
    static double normalize(Timestamp time, TimeInterval timeInterval) {
        // XXX: if interval is more than 292 years, this will not work
        double range = timeInterval.getEnd().durationFrom(timeInterval.getStart()).toNanosLong();
        double value = time.durationBetween(timeInterval.getStart()).toNanosLong();
        return value / range;
    }
    
    private static TimestampFormat format = new TimestampFormat("yyyy/MM/dd HH:mm:ss.NNNNNNNNN");
    private static ArrayInt possibleStopFromEnd = new ArrayInt(0,1,2,3,4,5,6,7,8,10,13,19,22,25,28);
    private static ArrayInt possibleStopFromStart = new ArrayInt(0,11,19,28);
    private static String zeroFormat = "0000/01/01 00:00:00.000000000";
    
    static List<String> createLabels(List<Timestamp> timestamps) {
        if (timestamps.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> result = new ArrayList<>(timestamps.size());
        for (Timestamp timestamp : timestamps) {
            result.add(format.format(timestamp));
        }
        
        return result;
    }
    
    static int commonEnd(String a, String b) {
        int currentStopFromEnd = 0;
        while(a.charAt(b.length() - 1 - currentStopFromEnd) == b.charAt(b.length() - 1 - currentStopFromEnd)) {
            currentStopFromEnd++;
        }
        return currentStopFromEnd;
    }
    
    static int commonStart(String a, String b) {
        int commonStart = 0;
        while(a.charAt(commonStart) == b.charAt(commonStart)) {
            commonStart++;
        }
        return commonStart;
    }
    
    @Deprecated
    /**
     * Use trimLabels()
     */
    static List<String> trimLabelsRight(List<String> labels) {
        if (labels.isEmpty()) {
            return labels;
        }
        
        // Calculate the useless part common to all strings
        int currentStopFromEnd = zeroFormat.length();
        for (int i = 0; i < labels.size(); i++) {
            String otherLabel = labels.get(i);
            currentStopFromEnd = Math.min(currentStopFromEnd, commonEnd(otherLabel, zeroFormat));
        }
        
        // Round down to a possible stop
        int finalStop = 0;
        for (int i = 0; possibleStopFromEnd.getInt(i) <= currentStopFromEnd; i++) {
            finalStop = possibleStopFromEnd.getInt(i);
        }
        
        if (finalStop == 0) {
            return labels;
        }
        
        // Trim labels
        List<String> result = new ArrayList<>(labels.size());
        for (String label : labels) {
            result.add(label.substring(0, zeroFormat.length() - finalStop));
        }
        
        return result;
    }
    
    @Deprecated
    /**
     * Use trimLabels()
     */
    static List<String> trimLabelsLeft(List<String> labels) {
        if (labels.isEmpty()) {
            return labels;
        }
        
        List<String> result = new ArrayList<>(labels.size());
        String previousLabel = labels.get(0);
        result.add(previousLabel);
        
        for (int i = 1; i < labels.size(); i++) {
            String nextLabel = labels.get(i);
            int commonStart = commonStart(previousLabel, nextLabel);
            int finalStart = 0;
            for (int j = 0; possibleStopFromStart.getInt(j) <= commonStart; j++) {
                finalStart = possibleStopFromStart.getInt(j);
            }
            result.add(nextLabel.substring(finalStart, nextLabel.length()));
            previousLabel = nextLabel;
        }
        
        return result;
    }
    
    static List<String> trimLabels( List< String > labels ) {
	
	//special case: if there are 1 or fewer labels, we cannot do redundance
	//checking or precision checking because there aren't enough
	//labels to compare with each other
	if ( labels.size() <= 1 ) {
	    return labels;
	}
	
	//first, we calculate the greatest changing precision amongst all the
	//labels. This is the precision that needs to be maintained throughout
	//all the labels
	int greatestChangingPrecision = calculateGreatestChangingField( labels );
	
	ArrayList< String > rtn = new ArrayList< String >( labels.size() );
	DateFormatter firstDate = new DateFormatter( labels.get( 0 ) );
	
	//the first date will need to display all information, even if it is
	//redundant; however, we can drop some trailing 0s, up to the
	//precision that is changing
	rtn.add( firstDate.getCompactForm( -1 , greatestChangingPrecision ) );
	for ( int i=1 ; i<labels.size() ; i++ ) {
	    String prevLabel = labels.get( i-1 );
	    String nextLabel = labels.get( i );
	    int redundancePrecision = greatestRedundancePrecision( prevLabel , nextLabel );
	    DateFormatter f = new DateFormatter( nextLabel );
	    String trimmedLabel = f.getCompactForm( redundancePrecision , greatestChangingPrecision );
	    rtn.add( trimmedLabel );
	}
	return rtn;
    }
    
    /**
     * Calculates the greatest precision at which both the last label
     * and the current label are the same
     * 
     * @param lastLabel
     * @param currLabel
     * @return 
     */
    private static int greatestRedundancePrecision( String lastLabel , String currLabel ) {
	int[] lastLabelFields = DateFormatter.parseFields( lastLabel );
	int[] currLabelFields = DateFormatter.parseFields( currLabel );
	for ( int i=0 ; i<lastLabelFields.length ; i++ ) {
	    if ( lastLabelFields[ i ] != currLabelFields[ i ] ) {
		return i-1;
	    }
	}
	return lastLabelFields.length-1;
    }
    
    /**
     * Calculates the greatest precision at which the last label and the
     * current label are changing. The list of labels must be non-empty.
     * 
     * @param labels
     * @return 
     */
    private static int calculateGreatestChangingField( List< String > labels ) {
	int[][] fields = new int[ labels.size() ][];
	for ( int i=0 ; i<labels.size() ; i++ ) {
	    fields[ i ] = DateFormatter.parseFields( labels.get( i ) );
	}
	int rtn = -1;
	for ( int fieldId=fields[ 0 ].length-1 ; fieldId >= 0 ; fieldId-- ) {
	    int referenceFieldValue = fields[ 0 ][ fieldId ];
	    for ( int i=1 ; i<labels.size() ; i++ ) {
		if ( fields[ i ][ fieldId ] != referenceFieldValue ) {
		    rtn = fieldId;
		    break;
		}
	    }
	    if ( rtn != -1 ) {
		break;
	    }
	}
	if ( rtn == -1 ) {
	    return DateFormatter.NANOSECOND_PRECISION;
	}
	//there is no milliseconds field, so if we got a fieldId of 6, corresponding
	//to milliseconds, it is actually nanoseconds that are changing
	if ( rtn == DateFormatter.MILLISECOND_PRECISION ) {
	    
	    //if the amount by which nanoseconds are changing corresponds to
	    //changes in milliseconds, then identify the changing field as ms
	    for ( int i=0 ; i<labels.size() ; i++ ) {
		if ( (fields[ i ][ DateFormatter.MILLISECOND_PRECISION ] % 1000000) != 0 ) {
		    return DateFormatter.NANOSECOND_PRECISION;
		}
	    }
	    return DateFormatter.MILLISECOND_PRECISION;
	}
	return rtn;
    }
    
    static class DateFormatter {
	
	final public static int YEAR_PRECISION = 0;
	final public static int MONTH_PRECISION = 1;
	final public static int DAY_PRECISION = 2;
	final public static int HOUR_PRECISION = 3;
	final public static int MINUTE_PRECISION = 4;
	final public static int SECOND_PRECISION = 5;
	final public static int MILLISECOND_PRECISION = 6;
	final public static int NANOSECOND_PRECISION = 7;
	
	private int m_year;
	private boolean m_showYears = true;
	private int m_month;
	private boolean m_showMonths = true;
	private int m_day;
	private boolean m_showDays = true;
	private int m_hour;
	private boolean m_showHours = true;
	private int m_minute;
	private boolean m_showMinutes = true;
	private int m_second;
	private boolean m_showSeconds = true;
	private int m_millisecond;
	private boolean m_showMilliseconds = true;
	private int m_nanosecond;
	private boolean m_showNanoseconds = true;
	
	public DateFormatter( int year , int month , int day , int hour , int minute , int second , int nanosecond ) {
	    this.m_year = year;
	    this.m_month = month;
	    this.m_day = day;
	    this.m_hour = hour;
	    this.m_minute = minute;
	    this.m_second = second;
	    this.m_millisecond = nanosecond / 1000000;
	    this.m_nanosecond = nanosecond % 1000000;
	}
	
	public DateFormatter( int year , int month , int day , int hour , int minute , int second , int millisecond , int nanosecond ) {
	    this.m_year = year;
	    this.m_month = month;
	    this.m_day = day;
	    this.m_hour = hour;
	    this.m_minute = minute;
	    this.m_second = second;
	    this.m_millisecond = millisecond;
	    this.m_nanosecond = nanosecond;
	}
	
	public DateFormatter( String date ) {
	    int[] fields = parseFields( date );
	    this.m_year = fields[ 0 ];
	    this.m_month = fields[ 1 ];
	    this.m_day = fields[ 2 ];
	    this.m_hour = fields[ 3 ];
	    this.m_minute = fields[ 4 ];
	    this.m_second = fields[ 5 ];
	    this.m_millisecond = fields[ 6 ] / 1000000;
	    this.m_nanosecond = fields[ 6 ] % 1000000;
	}
	
	protected static int[] parseFields( String time ) {
	    String[] fields = time.split( "/|:| |\\." );
	    int[] rtn = new int[ fields.length ];
	    for ( int i=0 ; i<fields.length ; i++ ) {
		rtn[ i ] = Integer.parseInt( fields[ i ] );
	    }
	    return rtn;
	}
	
	/**
	 * Generates a compact form of the date by removing trailing zeroes,
	 * while maintaining a required precision and removing redundant
	 * information on the left side of the text. if the common precision
	 * exceeds the required precision, then an empty label is produced.
	 * 
	 * @param commonPrecision the parts of the date that can be left out because
	 * they are common to other dates
	 * @param requiredPrecision the minimum precision the compact form must have
	 * @return the compact form of the date
	 */
	public String getCompactForm( int commonPrecision , int requiredPrecision ) {
	    
	    //special case when the common precision exceeds the required precision
	    //this means that all the labels are identicial, so the labels would be empty
	    if ( commonPrecision >= requiredPrecision ) {
		return "";
	    }
	    removeRedundantPrecision( commonPrecision );
	    maintainRequiredPrecision( requiredPrecision );
	    
	    //handle special cases:
	    if ( this.m_showDays && !this.m_showMonths ) {
		this.m_showMonths = true;
	    }
	    
	    if ( this.m_showMonths && !this.m_showDays && !this.m_showYears ) {
		if ( requiredPrecision >= DAY_PRECISION ) {
		    this.m_showDays = true;
		}
		else {
		    this.m_showYears = true;
		}
	    }
	    
	    if ( this.m_showHours && !this.m_showMinutes ) {
		this.m_showMinutes = true;
	    }
	    return buildDateString();
	}
	
	/**
	 * Removes redundant precision from a given date string; however, hours,
	 * minutes, seconds, and nanoseconds must not be removed for redundance
	 * because that gives rise to ambiguous times such as 9:15. The time
	 * 9:15 could then be 9 hours 15 minutes or 9 minutes 15 seconds
	 * and we resolve this by defining these ambiguous times to be the
	 * least precise possible.
	 * 
	 * @param redundantPrecision the precision to which the date is redundant
	 */
	void removeRedundantPrecision( int redundantPrecision ) {
	    if ( redundantPrecision >= YEAR_PRECISION ) {
		this.m_showYears = false;
		
		if ( redundantPrecision >= MONTH_PRECISION ) { 
		    this.m_showMonths = false;
		    
		    if ( redundantPrecision >= DAY_PRECISION ) {
			this.m_showDays = false;
		    }
		}
	    }
	}
	
	void maintainRequiredPrecision( int requiredPrecision ) {
	    
	    //check if the nanoseconds need to be displayed
	    if ( (this.m_nanosecond == 0) && requiredPrecision < NANOSECOND_PRECISION ) {
		this.m_showNanoseconds = false;
		
		//check if the milliseconds need to be displayed
		if ( (this.m_millisecond == 0) && requiredPrecision < MILLISECOND_PRECISION ) {
		    this.m_showMilliseconds = false;
		    
		    if ( (this.m_second == 0) && requiredPrecision < SECOND_PRECISION ) {
			this.m_showSeconds = false;
			
			if ( (this.m_minute == 0) && requiredPrecision < MINUTE_PRECISION ) {
			    this.m_showMinutes = false;
			    
			    if ( (this.m_hour == 0) && requiredPrecision < HOUR_PRECISION ) {
				this.m_showHours = false;
				
				if ( (this.m_day == 1) && requiredPrecision < DAY_PRECISION ) {
				    this.m_showDays = false;

				    if ( (this.m_month == 1) && requiredPrecision < MONTH_PRECISION ) {
					this.m_showMonths = false;
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	
	private String buildDateString() {
	    String rtn = "";
	    if ( this.m_showYears ) {
		rtn += createNumericalString( this.m_year , 4 );
		if ( this.m_showMonths ) {
		    rtn += "/";
		}
	    }
	    if ( this.m_showMonths ) {
		rtn += createNumericalString( this.m_month , 2 );
		if ( this.m_showDays ) {
		    rtn += "/";
		}
	    }
	    if ( this.m_showDays ) {
		rtn += createNumericalString( this.m_day , 2 );
		if ( this.m_showHours ) {
		    rtn += " ";
		}
	    }
	    if ( this.m_showHours ) {
		rtn += createNumericalString( this.m_hour , 2 );
		if ( this.m_showMinutes ) {
		    rtn += ":";
		}
	    }
	    if ( this.m_showMinutes ) {
		rtn += createNumericalString( this.m_minute , 2 );
		if ( this.m_showSeconds ) {
		    rtn += ":";
		}
	    }
	    if ( this.m_showSeconds ) {
		rtn += createNumericalString( this.m_second , 2 );
		if ( this.m_showMilliseconds ) {
		    rtn += ".";
		}
	    }
	    if ( this.m_showMilliseconds ) {
		rtn += createNumericalString( this.m_millisecond , 3 );
	    }
	    if ( this.m_showNanoseconds ) {
		rtn += createNumericalString( this.m_nanosecond , 6 );
	    }
	    return rtn;
	}
	
	/**
	 * Removes trailing zeros from a string
	 * 
	 * @param value a string
	 * @return the inputed string without any trailing zeroes
	 */
	private String removeTrailingZeroes( String value ) {
	    String rtn = value;
	    int lastIdxOfNonzero = rtn.length()-1;
	    for ( ; lastIdxOfNonzero >= 0 ; lastIdxOfNonzero-- ) {
		if ( rtn.charAt( lastIdxOfNonzero ) != '0' ) {
		    break;
		}
	    }
	    return rtn.substring( 0 , lastIdxOfNonzero+1 );
	}
    }
    
    static String toDateString( int year , int month , int day , int hour , int minute , int second , int millisecond , int nanosecond ) {
	String yearText = createNumericalString( year , 4 );
	String monthText = createNumericalString( month , 2 );
	String dayText = createNumericalString( day , 2 );
	String hourText = createNumericalString( hour , 2 );
	String minuteText = createNumericalString( minute , 2 );
	String secondText = createNumericalString( second , 2 );
	String millisecondText = createNumericalString( millisecond , 3 );
	String nanosecondText = createNumericalString( nanosecond , 6 );
	return yearText + "/" + monthText + "/" + dayText + " " + hourText + ":" + minuteText + ":" + secondText + "." + millisecondText + nanosecondText;
    }
    
    /**
     * Creates a string from an integer value that has at least the
     * specified length. The integer is padded with 0s to ensure the
     * specified length.
     * 
     * @param value the value to convert to a string
     * @param minLength the minimum length the string must have
     * @return the given value as a string with length at least the specified
     * minimum
     */
    static String createNumericalString( int value , int minLength ) {
	String rtn = String.valueOf( value );
	while( rtn.length() < minLength ) {
	    rtn = "0" + rtn;
	}
	return rtn;
    }
}
