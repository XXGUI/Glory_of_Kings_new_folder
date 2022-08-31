package com.nf.st;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 用于处理Runtime.getRuntime().exec产生的错误流及输出流
 *
 * @author shaojing
 */
public class StreamGobbler extends Thread {
    InputStream is;
    String cmd;
    DataOutputStream os;

    StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    StreamGobbler(InputStream is, String cmd, DataOutputStream redirect) {
        this.is = is;
        this.cmd = cmd;
        this.os = redirect;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
//            if (os != null)
//                pw = new PrintWriter(os);
            if(null!=os){
                os.write(cmd.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null)
                    pw.println(line);
            }

            if (pw != null)
                pw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (null != pw) {
                    pw.close();
                }
                if (null != br) {
                    br.close();
                }
                if (null != isr) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}     