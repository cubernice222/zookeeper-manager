package com.cuber.zkweb.model;

import com.cuber.java.zkpros.model.ZooKeeperProsNode;

import java.util.List;

/**
 * Created by cuber on 2016/10/13.
 */
public class Page {
    private int totalRecords;//总数目
    private int pageCount = 10;//分页数目
    private int curPage = 1;//当前页码
    private int pages;

    private List data;

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

}
