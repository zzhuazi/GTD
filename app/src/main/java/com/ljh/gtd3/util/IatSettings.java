package com.ljh.gtd3.util;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.ljh.gtd3.R;

/**
 * Created by Administrator on 2018/3/19.
 */
//听写页面设置
public class IatSettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    public static final String PREFER_NAME = "com.iflytek.setting";
    private EditTextPreference mVadbosPreference;
    private EditTextPreference mVadeosPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
        addPreferencesFromResource(R.xml.iat_setting);
        mVadbosPreference = (EditTextPreference)findPreference("iat_vadbos_preference");
        mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(IatSettings.this,mVadbosPreference,0,10000));

        mVadeosPreference = (EditTextPreference)findPreference("iat_vadeos_preference");
        mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(IatSettings.this,mVadeosPreference,0,10000));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValues) {
        return true;
    }
}
