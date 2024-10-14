package com.osfans.trime;

import android.os.Bundle;

import com.androlua.LuaActivity;

public class BackupActivity extends LuaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doAsset("backup.lua");
    }
}
