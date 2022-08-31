package com.nf.st;

import java.util.List;

public class ThisData {
    private int weBlueBuffcd;//我方蓝buffcd
    private int weRedBuffcd;//我方红buffcd
    private int weLizard;//我方蜥蜴
    private int weWolf;//我方双狼
    private int wePid;//我方三猪
    private int weBird;//我方小鸟

    private int enemyBlueBuffcd;//敌方蓝buffcd
    private int enemyRedBuffcd;//敌方红buffcd
    private int enemyLizard;//敌方蜥蜴
    private int enemyWolf;//敌方双狼
    private int enemyPid;//敌方三猪
    private int enemyBird;//敌方小鸟

    private List<String> soldier;//小兵

    private List<Hero> hl;//英雄集合


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

    public int getWeLizard() {
        return weLizard;
    }

    public void setWeLizard(int weLizard) {
        this.weLizard = weLizard;
    }

    public int getWeWolf() {
        return weWolf;
    }

    public void setWeWolf(int weWolf) {
        this.weWolf = weWolf;
    }

    public int getWePid() {
        return wePid;
    }

    public void setWePid(int wePid) {
        this.wePid = wePid;
    }

    public int getWeBird() {
        return weBird;
    }

    public void setWeBird(int weBird) {
        this.weBird = weBird;
    }

    public int getEnemyLizard() {
        return enemyLizard;
    }

    public void setEnemyLizard(int enemyLizard) {
        this.enemyLizard = enemyLizard;
    }

    public int getEnemyWolf() {
        return enemyWolf;
    }

    public void setEnemyWolf(int enemyWolf) {
        this.enemyWolf = enemyWolf;
    }

    public int getEnemyPid() {
        return enemyPid;
    }

    public void setEnemyPid(int enemyPid) {
        this.enemyPid = enemyPid;
    }

    public int getEnemyBird() {
        return enemyBird;
    }

    public void setEnemyBird(int enemyBird) {
        this.enemyBird = enemyBird;
    }

    public List<String> getSoldier() {
        return soldier;
    }

    public void setSoldier(List<String> soldier) {
        this.soldier = soldier;
    }

    @Override
    public String toString() {
        return "ThisData{" +
                "weBlueBuffcd=" + weBlueBuffcd +
                ", weRedBuffcd=" + weRedBuffcd +
                ", weLizard=" + weLizard +
                ", weWolf=" + weWolf +
                ", wePid=" + wePid +
                ", weBird=" + weBird +
                ", enemyBlueBuffcd=" + enemyBlueBuffcd +
                ", enemyRedBuffcd=" + enemyRedBuffcd +
                ", enemyLizard=" + enemyLizard +
                ", enemyWolf=" + enemyWolf +
                ", enemyPid=" + enemyPid +
                ", enemyBird=" + enemyBird +
                ", hl=" + hl +
                '}';
    }
}
