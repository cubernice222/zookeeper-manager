package com.cuber.zkweb.model;

import com.cuber.java.zkpros.model.ZooKeeperProsNode;

import java.util.List;

/**
 * Created by cuber on 2016/10/13.
 */
public class Page {
    private int count;//总数目
    private int pageCount = 10;//分页数目
    private int curPageIndex = 1;//当前页码
    private int pages;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    private List<ZooKeeperProsNode> zooKeeperProsNodes;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getCurPageIndex() {
        return curPageIndex;
    }

    public void setCurPageIndex(int curPageIndex) {
        this.curPageIndex = curPageIndex;
    }

    public List<ZooKeeperProsNode> getZooKeeperProsNodes() {
        return zooKeeperProsNodes;
    }

    public void setZooKeeperProsNodes(List<ZooKeeperProsNode> zooKeeperProsNodes) {
        this.zooKeeperProsNodes = zooKeeperProsNodes;
    }
}
