package com.example.safeu2.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class VaultRepository {
    private static final String PREF_NAME = "secret_vault_prefs";
    private static final String KEY_ENTRIES = "vault_entries";
    private static final String KEY_PIN = "vault_pin";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public VaultRepository(Context context) {
        gson = new Gson();
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EncryptedSharedPreferences", e);
        }
    }

    public boolean hasPin() {
        return sharedPreferences.contains(KEY_PIN);
    }

    public void setPin(String pin) {
        sharedPreferences.edit().putString(KEY_PIN, pin).apply();
    }

    public boolean verifyPin(String pin) {
        String savedPin = sharedPreferences.getString(KEY_PIN, "");
        return savedPin.equals(pin);
    }

    public List<VaultEntry> getEntries() {
        String json = sharedPreferences.getString(KEY_ENTRIES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<VaultEntry>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveEntries(List<VaultEntry> entries) {
        String json = gson.toJson(entries);
        sharedPreferences.edit().putString(KEY_ENTRIES, json).apply();
    }

    public void addEntry(VaultEntry entry) {
        List<VaultEntry> entries = getEntries();
        entries.add(entry);
        saveEntries(entries);
    }

    public void updateEntry(VaultEntry updatedEntry) {
        List<VaultEntry> entries = getEntries();
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(updatedEntry.getId())) {
                entries.set(i, updatedEntry);
                break;
            }
        }
        saveEntries(entries);
    }

    public void deleteEntry(String id) {
        List<VaultEntry> entries = getEntries();
        entries.removeIf(entry -> entry.getId().equals(id));
        saveEntries(entries);
    }
    
    public VaultEntry getEntryById(String id) {
        List<VaultEntry> entries = getEntries();
        for (VaultEntry entry : entries) {
            if (entry.getId().equals(id)) {
                return entry;
            }
        }
        return null;
    }
}
