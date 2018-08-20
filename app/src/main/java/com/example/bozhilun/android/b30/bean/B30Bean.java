package com.example.bozhilun.android.b30.bean;

import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.SportData;
import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/17.
 */

public class B30Bean extends DataSupport {
    private int id;
    private Date date;
    //步数的汇总
    private String sportDataStr;
    //步数的详情
    private String originDataStr;

    //半小时的数据
    private String halfHourDataStr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSportDataStr() {
        return sportDataStr;
    }

    public void setSportDataStr(String sportDataStr) {
        this.sportDataStr = sportDataStr;
    }

    public String getOriginDataStr() {
        return originDataStr;
    }

    public void setOriginDataStr(String originDataStr) {
        this.originDataStr = originDataStr;
    }

    public String getHalfHourDataStr() {
        return halfHourDataStr;
    }

    public void setHalfHourDataStr(String halfHourDataStr) {
        this.halfHourDataStr = halfHourDataStr;
    }
}
