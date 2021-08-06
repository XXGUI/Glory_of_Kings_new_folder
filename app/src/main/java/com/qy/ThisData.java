package com.qy;

import java.util.List;

public class ThisData {
    private int weBlueBuffcd;//我方蓝buffcd
    private int weRedBuffcd;//我方红buffcd
    private int enemyBlueBuffcd;//敌方蓝buffcd
    private int enemyRedBuffcd;//敌方红buffcd
    private List<Hero> hl;

    public int getWeBlueBuffcd() {
        return weBlueBuffcd;
    }

    public void setWeBlueBuffcd(int weBlueBuffcd) {
        this.weBlueBuffcd = weBlueBuffcd;
    }

    public int getWeRedBuffcd() {
        return weRedBuffcd;
    }

    public void setWeRedBuffcd(int weRedBuffcd) {
        this.weRedBuffcd = weRedBuffcd;
    }

    public int getEnemyBlueBuffcd() {
        return enemyBlueBuffcd;
    }

    public void setEnemyBlueBuffcd(int enemyBlueBuffcd) {
        this.enemyBlueBuffcd = enemyBlueBuffcd;
    }

    public int getEnemyRedBuffcd() {
        return enemyRedBuffcd;
    }

    public void setEnemyRedBuffcd(int enemyRedBuffcd) {
        this.enemyRedBuffcd = enemyRedBuffcd;
    }

    public List<Hero> getHl() {
        return hl;
    }

    public void setHl(List<Hero> hl) {
        this.hl = hl;
    }

    @Override
    public String toString() {
        return "ThisData{" +
                "weBlueBuffcd=" + weBlueBuffcd +
                ", weRedBuffcd=" + weRedBuffcd +
                ", enemyBlueBuffcd=" + enemyBlueBuffcd +
                ", enemyRedBuffcd=" + enemyRedBuffcd +
                ", hl=" + hl +
                '}';
    }
}
