/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;

import java.util.*;

/**
 *
 * @author yangchun
 */
public class ETA {

    int month, day, hour, minute;

    public void setMonth(int i_month) {
        this.month = i_month;
    }

    public void setDay(int i_day) {
        this.day = i_day;

    }

    public void setHour(int i_hour) {
        this.hour = i_hour;
    }

    public void setMinute(int i_minute) {
        this.minute = i_minute;
    }

    public String getETA() {
        if (month == 0 || day == 0 || hour == 24 || minute == 60) {
            return "未知";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        String str_mon = "", str_day = "", str_hour = "", str_min = "";
        //System.out.println("hour: " + hour);
        if (month < 10) {
            str_mon = "0" + month;
        } else {
            str_mon = "" + month;
        }
        if (day < 10) {
            str_day = "0" + day;
        } else {
            str_day = "" + day;
        }
        if (hour < 10) {
            str_hour = "0" + hour;
        } else {
            str_hour = "" + hour;
        }
        if (minute < 10) {
            str_min = "0" + minute;
        } else {
            str_min = "" + minute;
        }
        return year + "-" + str_mon + "-" + str_day + " " + str_hour + ":" + str_min;

    }
}
