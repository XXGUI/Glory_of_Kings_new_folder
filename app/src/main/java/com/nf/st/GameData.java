package com.nf.st;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GameData {
    public static int cdDeviation = 5;

    static ThisData parseEasyJson(String json) {
        if (null == json || "".equals(json) || 0 >= json.length() || !"{".equals(json.substring(0, 1)) || "".equals("对局加载中")) {
            return null;
        }
        ThisData td = new ThisData();
        try {
            json = json.replaceAll("\\}\\,\\]\\}", "\\}\\]\\}").replaceAll(",]", "]");
            JSONObject jsonObject = new JSONObject(json);
            List<Hero> hl = new ArrayList<>();
            JSONArray jsonArray2 = new JSONArray(jsonObject.getString("hero"));
            for (int j = 0; j < jsonArray2.length(); j++) {
                JSONObject jsonObject2 = (JSONObject) jsonArray2.get(j);
                Hero hero = new Hero();
                hero.setHeroId(Integer.valueOf(jsonObject2.getString("heroId")));
                hero.setHpPercentage(Double.valueOf(jsonObject2.getString("hpPercentage")));
                hero.setMapXY(jsonObject2.getString("mapXY"));
                hero.setEntityXY(jsonObject2.getString("entityXY"));
                hero.setSummonercdSkill(Integer.valueOf(jsonObject2.getString("summonercdSkill")));
                hero.setSummonercdSkillId(Integer.valueOf(jsonObject2.getString("summonercdSkillId")));
                hero.setBigMoveCd(Integer.valueOf(jsonObject2.getString("bigMoveCd")));
                hero.setGoHome(Integer.valueOf(jsonObject2.getString("goHome")));
                hl.add(hero);
            }
            td.setHl(hl);
            td.setWeRedBuffcd(cdDeviation(Integer.valueOf(jsonObject.getString("weRedBuffcd"))));
            td.setWeBlueBuffcd(cdDeviation(Integer.valueOf(jsonObject.getString("weBlueBuffcd"))));
//            td.setWeLizard(Integer.valueOf(jsonObject.getString("weLizard")));
//            td.setWeWolf(Integer.valueOf(jsonObject.getString("weWolf")));
//            td.setWePid(Integer.valueOf(jsonObject.getString("wePid")));
//            td.setWeBird(Integer.valueOf(jsonObject.getString("weBird")));

            td.setEnemyRedBuffcd(cdDeviation(Integer.valueOf(jsonObject.getString("enemyRedBuffcd"))));
            td.setEnemyBlueBuffcd(cdDeviation(Integer.valueOf(jsonObject.getString("enemyBlueBuffcd"))));
//            td.setEnemyLizard(Integer.valueOf(jsonObject.getString("enemyLizard")));
//            td.setEnemyWolf(Integer.valueOf(jsonObject.getString("enemyWolf")));
//            td.setEnemyPid(Integer.valueOf(jsonObject.getString("enemyPid")));
//            td.setEnemyBird(Integer.valueOf(jsonObject.getString("enemyBird")));

            jsonArray2 = new JSONArray(jsonObject.getString("soldier"));
            List<String> soldierList = new ArrayList<>();
            for (int j = 0; j < jsonArray2.length(); j++) {
                String s = String.valueOf(jsonArray2.get(j));
                soldierList.add(s);
            }
            td.setSoldier(soldierList);
        } catch (JSONException e) {
            System.out.println("解析数据异常");
            return null;
        } catch (Exception e) {
            return null;
        }
        return td;
    }

    public static int cdDeviation(int cd) {
        cd += 4;
        return cd;
    }


    /**
     * 执行命令并且输出结果
     */
    public static void execRootCmd(String cmd) {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            closeIo(dos, dis);
        }
    }

    public static void closeIo(DataOutputStream dos, DataInputStream dis) {
        try {
            if (null != dos) {
                dos.close();
            }
            if (null != dis) {
                dis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReadTxtFile(String strFilePath) {
        String content = "";
        File file = new File(strFilePath);
        if (file.isDirectory()) {
            System.out.println("文件不存在");
        } else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(strFilePath));
                String str;
                while ((str = in.readLine()) != null) {
                    content += str;
                }
            } catch (Exception e) {
            }
        }
        return content;
    }

    static H parseHJson(String json) {
        if (null == json || "".equals(json) || 0 >= json.length() || !"{".equals(json.substring(0, 1))) {
            return null;
        }
        H td = new H();
        try {
            json = json.replaceAll("\\}\\,\\]\\}", "\\}\\]\\}").replaceAll(",]", "]");
            JSONObject jsonObject = new JSONObject(json);
            td.setData(String.valueOf(jsonObject.getString("data")));
            td.setName(String.valueOf(jsonObject.getString("name")));
            td.setStatus(Boolean.valueOf(jsonObject.getString("status")));
            td.setX(Integer.valueOf(jsonObject.getString("x")));
            td.setY(Integer.valueOf(jsonObject.getString("y")));
        } catch (JSONException e) {
            System.out.println("解析数据异常");
            return null;
        } catch (Exception e) {
            return null;
        }
        return td;
    }
}
