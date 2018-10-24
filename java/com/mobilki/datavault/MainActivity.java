package com.mobilki.datavault;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


public class MainActivity extends AppCompatActivity {
    private static final String VAULT_NAME = "vault_prefs";
    private static final int NEW_PASS = 1;
    private static final int CHANGE_PASS = 2;
    private SharedPreferences prefs;
    private AlertDialog dialog;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Files mFiles;

    class Encrypt extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Encryption in progress...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mFiles.encryptFiles(prefs); // encrypt unencrypted files
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    private AlertDialog getDialog(Activity activity, int mode){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.settings, null); // Получаем layout по его ID
        builder.setView(view);

        Button saveBtn = (Button) view.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePassword(); // Переход в сохранение настроек MainActivity
            }
        });
        if (mode == NEW_PASS)
            builder.setCancelable(false);
        else
            builder.setCancelable(true);
        return builder.create();
    }

    private void savePassword(){
        EditText _passwordText = (EditText)  dialog.findViewById(R.id.setting_password);
        String password = _passwordText.getText().toString();
        if (password.equals("")){
            Toast.makeText(this, "Please set password", Toast.LENGTH_LONG).show();
            super.recreate();
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pass", new String(Hex.encodeHex(DigestUtils.sha256(password))));
        editor.apply();
        dialog.cancel();
        Toast.makeText(this, "Password successfully saved", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(VAULT_NAME, Context.MODE_PRIVATE);

        //check current pass or set new
        if (prefs.getString("pass", null) != null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            dialog = getDialog(this, NEW_PASS);
            dialog.show();
        }

        mFiles = new Files();
        mFiles.checkDir(); //check directories

        Encrypt encrypt = new Encrypt();
        encrypt.execute();

        //list files
        if (mFiles.getLength() < 1) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            Log.d("TAG", "VISIBLE");
        }
        else {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new MyAdapter(mFiles.getListFiles());
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.decrypt_settings:
                mFiles.decryptFiles(mAdapter, mLayoutManager);
                return true;
            case R.id.action_settings:
                dialog = getDialog(this, CHANGE_PASS);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
