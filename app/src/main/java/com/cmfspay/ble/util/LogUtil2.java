package com.cmfspay.ble.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/28.
 */

public class LogUtil2 {

    private static LogUtil2 INSTANCE = null;
    private static String PATH_LOGCAT;
    private static String    tagPrefix  = "";
    private        LogDumper mLogDumper = null;
    private int mPId;

    public static LogUtil2 getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogUtil2.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogUtil2(context);
                }
            }
        }
        return INSTANCE;
    }

    private LogUtil2(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    private void init(Context context) {
        PATH_LOGCAT = context.getExternalFilesDir("") + File.separator;
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private BufferedReader   mReader  = null;
        private FileOutputStream out      = null;
        private boolean          mRunning = true;
        private String           cmds     = null;
        private Process logcatProc;
        private String  mPID;

        LogDumper(String pid, String dir) {
            mPID = pid;

            try {
                out = new FileOutputStream(new File(dir, "runtimelog.log"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /**
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             * 显示当前mPID程序的 E和W等级的日志.
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
        }

        void stopLogs() {
            mRunning = false;
        }

        void save(String data) {

        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((getFormatTime() + "/" + generateTag() + ": " + line + "\n").getBytes());
                    }
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
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }

    }

    //上传到日志log
    private void upload(Context context) {
        try {
            URL url = new URL("https://rb0uqhjp.api.lncld.net/1.1/files/"
                    + context.getPackageName()
                    + "_" + Build.MODEL
                    + "_" + Build.MANUFACTURER
                    + "_" + Build.VERSION.RELEASE
                    + "_" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
                    + "_" + ".txt");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);//允许写出
            httpURLConnection.setDoInput(true);//允许读入
            httpURLConnection.setUseCaches(false);//不使用缓存
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("X-LC-Id", "DpvmCqLHqt4agjV6GPVS0wBM-gzGzoHsz");
            httpURLConnection.setRequestProperty("X-LC-Key", "qXf8aiXtL8kAlJUiyVqVxCfo");
            httpURLConnection.setRequestProperty("Content-Type", "text/plain");
            httpURLConnection.connect();

            File file = new File(PATH_LOGCAT, "runtimelog.txt");
            if (!file.exists()) {
                Log.e("Joker", "file not exist");
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            StringBuffer    lostringBuffer  = new StringBuffer();
            byte[]          logbuff         = new byte[1024 * 4];
            int             lognum;
            while ((lognum = fileInputStream.read(logbuff)) != -1) {
                lostringBuffer.append(new String(logbuff, 0, lognum));
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(lostringBuffer.toString());
            writer.close();
            Log.e("Joker", "getResponseCode " + httpURLConnection.getResponseCode());

            if (httpURLConnection.getResponseCode() == 200 || httpURLConnection.getResponseCode() == 201) {
                deleteLogFile(context);
                String       string       = httpURLConnection.getResponseMessage();
                InputStream  inputStream  = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                byte[] buff = new byte[1024 * 4];
                int    num;
                while ((num = inputStream.read(buff)) != -1) {
                    stringBuffer.append(new String(buff, 0, num));
                    //LogUtil.e(stringBuffer.toString());
                }
                System.out.print(httpURLConnection.getResponseCode());
                System.out.print(stringBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getClassName(int frame) {
        StackTraceElement[] frames = (new Throwable()).getStackTrace();
        return parseClassName(frames[frame].getClassName());
    }

    private static String parseClassName(String fullName) {
        int    lastDot    = fullName.lastIndexOf('.');
        String simpleName = fullName;
        if (lastDot != -1) {
            simpleName = fullName.substring(lastDot + 1);
        }
        // handle inner class names
        int lastDollar = simpleName.lastIndexOf('$');
        if (lastDollar != -1) {
            simpleName = simpleName.substring(0, lastDollar);
        }
        return simpleName;
    }

    private static String generateTag() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String            callerClazzName   = stackTraceElement.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String tag = "%s.%s(L:%d)";
        tag = String.format(tag, new Object[]{callerClazzName, stackTraceElement.getMethodName(), Integer.valueOf(stackTraceElement.getLineNumber())});
        //给tag设置前缀
        tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
        return tag;
    }

    public static String getFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String           date   = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012年10月03日 23:41:31
    }

    /**
     * 删除log文件
     *
     * @param context
     */
    public static void deleteLogFile(final Context context) {
        File file = new File(PATH_LOGCAT + "/runtimelog.txt");
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    /**
     * 获取本地log文件的大小
     *
     * @param context
     * @return
     */
    public static long getLogFileLength(final Context context) {
        long length = 0L;
        File file   = new File(PATH_LOGCAT, "runtimelog.txt");
        if (file.exists()) {
            length = file.length();
        }
        return length;
    }
}
