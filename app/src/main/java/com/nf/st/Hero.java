package com.nf.st;

public class Hero {
    private int heroId;//英雄ID
    private double hpPercentage;//英雄血量百分比
    private String mapXY;//地图坐标
    private int summonercdSkillId;//召唤师技能cd
    private int summonercdSkill;//召唤师技能cd
    private int bigMoveCd;//召唤师大招cd
    private String entityXY;//实体坐标
    private int goHome;//回城

    Hero(){}

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public double getHpPercentage() {
        return hpPercentage;
    }

    public void setHpPercentage(double hpPercentage) {
        this.hpPercentage = hpPercentage;
    }

    public String getMapXY() {
        return mapXY;
    }

    public void setMapXY(String mapXY) {
        this.mapXY = mapXY;
    }

    public int getSummonercdSkillId() {
        return summonercdSkillId;
    }

    public void setSummonercdSkillId(int summonercdSkillId) {
        this.summonercdSkillId = summonercdSkillId;
    }

    public int getSummonercdSkill() {
        return summonercdSkill;
    }

    public void setSummonercdSkill(int summonercdSkill) {
        this.summonercdSkill = summonercdSkill;
    }

    public int getBigMoveCd() {
        return bigMoveCd;
    }

    public void setBigMoveCd(int bigMoveCd) {
        this.bigMoveCd = bigMoveCd;
    }

    public String getEntityXY() {
        return entityXY;
    }

    public void setEntityXY(String entityXY) {
        this.entityXY = entityXY;
    }

    public int getGoHome() {
        return goHome;
    }

    public void setGoHome(int goHome) {
        this.goHome = goHome;
    }
}
