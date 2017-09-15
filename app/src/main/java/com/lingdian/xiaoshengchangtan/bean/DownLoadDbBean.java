package com.lingdian.xiaoshengchangtan.bean;

import java.io.Serializable;

/**
 * Created by lingdian on 17/9/11.
 *
 */

public class DownLoadDbBean implements Serializable{
    public String link;//跳转连接
    public String  title;//标题
    public String date;//日期
    public int downStatus=0;//下载状态 （0 ：未下载）  （1：下载中） (2暂停中)（3：已经下载）
    public boolean isEditStatus=false;//是否进入编辑状态
    public boolean isSelected=false;//是否选中
}
