package me.omico.currentactivity.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Collections;

import me.omico.currentactivity.R;
import me.omico.currentactivity.provider.Settings;
import me.omico.currentactivity.service.FloatViewService;
import me.omico.util.ActivityUtils;
import me.omico.util.ServiceUtils;

import static me.omico.currentactivity.CurrentActivity.ACTION_FLOAT_VIEW_SERVICE_START;
import static me.omico.currentactivity.CurrentActivity.ACTION_QUICK_START;
import static me.omico.currentactivity.CurrentActivity.EXTRA_COME_FROM_SHORTCUT;
import static me.omico.currentactivity.CurrentActivity.EXTRA_FIRST_OPEN;

/**
 * @author Omico 2017/8/18
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) createShortcut();

        if (Settings.getBoolean(EXTRA_FIRST_OPEN, true)) {
            ActivityUtils.startActivity(this, GuideActivity.class);
        } else {
            ActivityUtils.startActivity(this, MainActivity.class);
            if (isQuickStartOrShortCutEnable())
                ServiceUtils.startService(this, FloatViewService.class, ACTION_FLOAT_VIEW_SERVICE_START);
        }
        SplashActivity.this.finish();
    }

    private boolean isQuickStartOrShortCutEnable() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) return action.equals(ACTION_QUICK_START);
            return intent.getBooleanExtra(EXTRA_COME_FROM_SHORTCUT, false);
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void createShortcut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent intent = new Intent(this, SplashActivity.class);
        intent.setAction(ACTION_QUICK_START);
        intent.putExtra(EXTRA_COME_FROM_SHORTCUT, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "shortcut_open")
                .setShortLabel(getString(R.string.quick_enable))
                .setLongLabel(getString(R.string.enable_float_window))
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(intent)
                .build();

        if (shortcutManager != null) {
            shortcutManager.setDynamicShortcuts(Collections.singletonList(shortcut));
        }
    }
}
