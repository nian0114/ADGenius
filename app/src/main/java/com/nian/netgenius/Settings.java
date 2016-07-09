package com.nian.netgenius;


import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Nian on 16/7/9.
 */
public class Settings extends PreferenceActivity {
    private CheckBoxPreference mDnsCustom;
    public static InetAddress dnsServer;
    private EditTextPreference mDNSIP;
    private String summary;

    private EditTextPreference mBlockDomain;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mDnsCustom = (CheckBoxPreference) findPreference("dns_custom");
        mDNSIP = (EditTextPreference) findPreference("dns_value");

        mBlockDomain = (EditTextPreference) findPreference("block_domain");
        try {
            summary = dnsServer.getHostAddress();
        } catch (NullPointerException e) {
            summary = getString(R.string.dns_value_summary);
        }

        mDNSIP.setSummary(summary);
        mDNSIP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String dnsIP = (String) newValue;
                try {
                    dnsServer = InetAddress.getByName(dnsIP);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mDNSIP.setSummary(dnsIP);
                return true;
            }
        });

        mBlockDomain.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                save((String) newValue);
                return true;
            }
        });
    }

    private void save(String content) {

        try {
            FileOutputStream outputStream = openFileOutput("user_custom.txt",
                    Activity.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(Settings.this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferencescreen, Preference preference) {
        if (preference == mDnsCustom) {
            if (!mDnsCustom.isChecked()) {
                dnsServer = null;
            }
        }
        return false;
    }
}
