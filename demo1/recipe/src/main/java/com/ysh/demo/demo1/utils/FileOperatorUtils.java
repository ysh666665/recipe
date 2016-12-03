package com.ysh.demo.demo1.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by hasee on 2016/11/26.
 */

public class FileOperatorUtils {
    private BufferedWriter writer;
    private BufferedReader reader;
    private Context mContext;

    public FileOperatorUtils(Context context) {
        mContext = context;
    }

    public void saveData(@NonNull String name, @NonNull String data) {
        try {
            FileOutputStream saveStream = mContext.openFileOutput(name, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(saveStream));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String readData(@NonNull String name) {
        try {
            FileInputStream readStream = mContext.openFileInput(name);
            reader = new BufferedReader(new InputStreamReader(readStream));
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
