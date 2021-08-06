package com.qy;

public class Hero {
    private int heroId;//英雄ID
    private double hpPercentage;//英雄血量百分比
    private String mapXY;//地图坐标
    private int summonercdSkill;//召唤师技能cd
    private int bigMoveCd;//召唤师大招cd
    private String entityXY;//实体坐标

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

    @Override
    public String toString() {
        return "Hero{" +
                "heroId=" + heroId +
                ", hpPercentage=" + hpPercentage +
                ", mapXY='" + mapXY + '\'' +
                ", summonercdSkill=" + summonercdSkill +
                ", bigMoveCd=" + bigMoveCd +
                ", entityXY='" + entityXY + '\'' +
                '}';
    }
}
