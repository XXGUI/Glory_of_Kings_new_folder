package com.qy;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class GameData extends AppCompatActivity {

    static ThisData parseEasyJson(String json) {
        if (null == json || "".equals(json) || 0 >= json.length()) {
            return null;
        }
        ThisData td = new ThisData();
        try {
            JSONObject jsonObject = new JSONObject(json.replaceAll("\\}\\,\\]\\,", "\\}\\]\\,"));
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
                hero.setBigMoveCd(Integer.valueOf(jsonObject2.getString("bigMoveCd")));
                hl.add(hero);
            }
            td.setHl(hl);
            td.setWeRedBuffcd(Integer.valueOf(jsonObject.getString("weRedBuffcd")));
            td.setWeBlueBuffcd(Integer.valueOf(jsonObject.getString("weBlueBuffcd")));
            td.setEnemyRedBuffcd(Integer.valueOf(jsonObject.getString("enemyRedBuffcd")));
            td.setEnemyBlueBuffcd(Integer.valueOf(jsonObject.getString("enemyBlueBuffcd")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return td;
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
            closeIo(dos,dis);
        }
    }

    public static void closeIo(DataOutputStream dos, DataInputStream dis) {
        try {
            dos.close();
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReadTxtFile(String strFilePath) {
        String content = "";
        File file = new File(strFilePath);
        if(file.isDirectory()){
            System.out.println("文件不存在");
        }else{
            try {
                BufferedReader in = new BufferedReader(new FileReader(strFilePath));
                String str;
                while ((str = in.readLine()) != null) {
                    content+=str;
                }
            } catch (IOException e) {

            }
        }
        return content;
    }
}
