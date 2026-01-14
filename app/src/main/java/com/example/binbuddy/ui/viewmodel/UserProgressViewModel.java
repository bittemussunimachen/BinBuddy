package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.repository.UserProgressRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for user progress data (coins, XP, level).
 */
@HiltViewModel
public class UserProgressViewModel extends AndroidViewModel {
    
    private final UserProgressRepository repository;
    
    private final MutableLiveData<Integer> coins = new MutableLiveData<>();
    private final MutableLiveData<Integer> xp = new MutableLiveData<>();
    private final MutableLiveData<Integer> level = new MutableLiveData<>();
    private final MutableLiveData<Integer> xpTarget = new MutableLiveData<>();
    private final MutableLiveData<Integer> streakDays = new MutableLiveData<>();
    
    @Inject
    public UserProgressViewModel(@NonNull Application application, UserProgressRepository repository) {
        super(application);
        this.repository = repository;
        loadProgress();
    }
    
    public LiveData<Integer> getCoins() {
        return coins;
    }
    
    public LiveData<Integer> getXp() {
        return xp;
    }
    
    public LiveData<Integer> getLevel() {
        return level;
    }
    
    public LiveData<Integer> getXpTarget() {
        return xpTarget;
    }
    
    public LiveData<Integer> getStreakDays() {
        return streakDays;
    }
    
    /**
     * Load current progress from repository.
     */
    public void loadProgress() {
        coins.setValue(repository.getCoins());
        int currentXp = repository.getXp();
        xp.setValue(currentXp);
        int currentLevel = repository.getLevel();
        level.setValue(currentLevel);
        xpTarget.setValue(repository.getXpTarget());
        streakDays.setValue(repository.getStreakDays());
    }
    
    /**
     * Add coins and update LiveData.
     * @param amount Number of coins to add
     */
    public void addCoins(int amount) {
        repository.addCoins(amount);
        coins.setValue(repository.getCoins());
    }
    
    /**
     * Add XP and update LiveData. Also recalculates level.
     * @param amount Amount of XP to add
     */
    public void addXp(int amount) {
        repository.addXp(amount);
        int newXp = repository.getXp();
        xp.setValue(newXp);
        int newLevel = repository.getLevel();
        level.setValue(newLevel);
        xpTarget.setValue(repository.getXpTarget());
    }
    
    /**
     * Set streak days.
     * @param days Number of streak days
     */
    public void setStreakDays(int days) {
        repository.setStreakDays(days);
        streakDays.setValue(repository.getStreakDays());
    }
    
    /**
     * Get current XP for current level (XP progress towards next level).
     * @return XP progress within current level
     */
    public int getCurrentLevelXp() {
        int totalXp = xp.getValue() != null ? xp.getValue() : repository.getXp();
        int currentLevel = level.getValue() != null ? level.getValue() : repository.getLevel();
        
        // Calculate XP for current level
        // Level 1: 0-199 XP (0-199)
        // Level 2: 200-399 XP (0-199 within level)
        int xpForPreviousLevels = (currentLevel - 1) * 200;
        return totalXp - xpForPreviousLevels;
    }
    
    /**
     * Get XP target for current level (XP needed within current level to reach next level).
     * @return XP needed within current level
     */
    public int getCurrentLevelXpTarget() {
        // Always 200 XP per level
        return 200;
    }
}
