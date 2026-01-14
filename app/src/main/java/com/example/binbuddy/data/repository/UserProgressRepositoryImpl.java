package com.example.binbuddy.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.binbuddy.domain.repository.UserProgressRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of UserProgressRepository using SharedPreferences for persistence.
 */
@Singleton
public class UserProgressRepositoryImpl implements UserProgressRepository {
    
    private static final String PREFS_NAME = "user_progress";
    private static final String KEY_COINS = "coins";
    private static final String KEY_XP = "xp";
    private static final String KEY_STREAK_DAYS = "streak_days";
    
    private static final int DEFAULT_COINS = 0;
    private static final int DEFAULT_XP = 0;
    private static final int DEFAULT_STREAK_DAYS = 0;
    
    // XP formula: level * 200 (so level 1 needs 200 XP, level 2 needs 400 XP, etc.)
    private static final int XP_PER_LEVEL = 200;
    
    private final SharedPreferences prefs;
    
    @Inject
    public UserProgressRepositoryImpl(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    @Override
    public int getCoins() {
        return prefs.getInt(KEY_COINS, DEFAULT_COINS);
    }
    
    @Override
    public void addCoins(int amount) {
        if (amount > 0) {
            int currentCoins = getCoins();
            prefs.edit().putInt(KEY_COINS, currentCoins + amount).apply();
        }
    }
    
    @Override
    public int getXp() {
        return prefs.getInt(KEY_XP, DEFAULT_XP);
    }
    
    @Override
    public void addXp(int amount) {
        if (amount > 0) {
            int currentXp = getXp();
            int newXp = currentXp + amount;
            prefs.edit().putInt(KEY_XP, newXp).apply();
        }
    }
    
    @Override
    public int getLevel() {
        int totalXp = getXp();
        return calculateLevel(totalXp);
    }
    
    @Override
    public int getXpTarget() {
        int level = getLevel();
        return calculateXpTarget(level);
    }
    
    @Override
    public int getStreakDays() {
        return prefs.getInt(KEY_STREAK_DAYS, DEFAULT_STREAK_DAYS);
    }
    
    @Override
    public void setStreakDays(int days) {
        prefs.edit().putInt(KEY_STREAK_DAYS, days).apply();
    }
    
    @Override
    public int calculateLevel(int totalXp) {
        // Level 1 starts at 0 XP, level 2 at 200 XP, level 3 at 400 XP, etc.
        // Formula: level = (totalXp / XP_PER_LEVEL) + 1
        // But we want level 1 to be the minimum
        if (totalXp <= 0) {
            return 1;
        }
        return (totalXp / XP_PER_LEVEL) + 1;
    }
    
    @Override
    public int calculateXpTarget(int level) {
        // XP target for current level is the XP needed to reach the next level
        // Level 1: need 200 XP to reach level 2
        // Level 2: need 400 XP to reach level 3
        return level * XP_PER_LEVEL;
    }
}
