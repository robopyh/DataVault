package com.mobilki.datavault;

import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.util.Map;


class Files{
    private static final String CRYPTED_DIR = Environment.getExternalStorageDirectory() + "/crypted/";
    private static final String DECRYPTED_DIR = Environment.getExternalStorageDirectory() + "/decrypted/";
    @SuppressWarnings("JniMissingFunction")
    public native int encrypt(String fName);
    @SuppressWarnings("JniMissingFunction")
    public native int decrypt(String fName, String ofName);

    static
    {
        System.loadLibrary("native");
    }

    void decryptFiles(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager){
        for (int i = 0; i < adapter.getItemCount(); i++){
            View v = layoutManager.findViewByPosition(i);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
            if (checkBox.isChecked()){
                TextView filename = (TextView) v.findViewById(R.id.filename);
                decrypt(CRYPTED_DIR + filename.getText().toString(), DECRYPTED_DIR + filename.getText().toString());
                return;
            }
        }
    }

    void encryptFiles(SharedPreferences prefs){
        Map<String, ?> encryptedFiles = prefs.getAll();
        File[] files = new File(CRYPTED_DIR).listFiles();
        for (File file : files) {
            if (!encryptedFiles.containsKey(file.getName())) {
                encrypt(CRYPTED_DIR + file.getName());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(file.getName(), 1);
                editor.apply();
            }
        }
    }

    void checkDir(){
        File cryptDirectory = new File(CRYPTED_DIR);
        File decryptDirectory = new File(DECRYPTED_DIR);
        if (!cryptDirectory.exists())
            cryptDirectory.mkdir();
        if (!decryptDirectory.exists())
            decryptDirectory.mkdir();
    }

    long getLength(){
        return new File(CRYPTED_DIR).listFiles().length;
    }

    File[] getListFiles(){
        return new File(CRYPTED_DIR).listFiles();
    }
}
