package com.event;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetEventTask {
    private static final String TAG = GetEventTask.class.getSimpleName();
    /**
     * 监听输入事件shell命令
     */
//    private static final String COMMAND_GET_EVENT = "getevent -rl";
    private static final String COMMAND_GET_EVENT = "getevent -c 1";

    private volatile static GetEventTask mGetEventTask = null;
    private LogDumper mLogDumper = null;
    private String mCurrentCommand = null;
    private boolean isRunning = false;

    public static GetEventTask getInstance() {
        if (mGetEventTask == null) {
            synchronized (GetEventTask.class) {
                if (mGetEventTask == null) {
                    mGetEventTask = new GetEventTask();
                }
            }
        }
        return mGetEventTask;
    }


    public GetEventTask() {

    }

    /**
     * 开始监听事件
     */
    public void startMonitorEvent() {
        start(COMMAND_GET_EVENT);
    }

    /**
     * 启动shell命令
     *
     * @param command
     */
    public void start(String command) {
        if (TextUtils.isEmpty(command) == true) {
            Log.e(TAG, "start --- command is null! ");
            return;
        }

        Log.d(TAG, "command = " + command);

        if (isRunning == true) {
            // 同一个命令不重复执行
            if (mCurrentCommand.equals(command) == true) {
                Log.d(TAG, "start --- command is already run!");
                return;
            } else {
                stop();
            }
        }

        if (mLogDumper == null) {
            mLogDumper = new LogDumper(command);
        }
        mLogDumper.start();
        mCurrentCommand = command;
        isRunning = true;
    }


    /**
     * 停止
     */
    public void stop() {
        Log.d(TAG, "--- stop ---");
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
            isRunning = false;
        } else {
            Log.d(TAG, "stop --- mLogDumper is null!");
        }
    }


    /**
     * shell命令执行
     */
    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String command = null;

        int count = 0;

        public LogDumper(String command) {
            this.command = command;
        }

        public void stopLogs() {
            Log.d(TAG, "--- LogDumper stopLogs ---");
            mRunning = false;
        }

        @Override
        public void run() {
            Process process = null;
            DataOutputStream os = null;

            while (true) {
                try {
                    process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.write(command.getBytes());
                    os.writeBytes("\n");
                    os.flush();
                    os.writeBytes("exit\n");
                    os.flush();

                    // 保存执行结果
                    mReader = new BufferedReader(new InputStreamReader(
                            process.getInputStream()), 1024);
                    String line = null;
                    while (mRunning && (line = mReader.readLine()) != null) {
                        if (!mRunning) {
                            break;
                        }


                        if (line.length() == 0) {
                            continue;
                        }

                        // 事件记录
//                        EventManage.getInstance().onTouchEvent();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (logcatProc != null) {
                        logcatProc.destroy();
                        logcatProc = null;
                    }
                    if (mReader != null) {
                        try {
                            mReader.close();
                            mReader = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }


}


