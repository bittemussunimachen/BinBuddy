package com.example.binbuddy.domain.repository;

/**
 * Repository interface for user progress operations (coins, XP, level).
 */
public interface UserProgressRepository {
    
    /**
     * Get current coin balance.
     * @return Current number of coins
     */
    int getCoins();
    
    /**
     * Add coins to the user's balance.
     * @param amount Number of coins to add
     */
    void addCoins(int amount);
    
    /**
     * Get current XP.
     * @return Current XP value
     */
    int getXp();
    
    /**
     * Add XP to the user's total.
     * @param amount Amount of XP to add
     */
    void addXp(int amount);
    
    /**
     * Get current level.
     * @return Current level
     */
    int getLevel();
    
    /**
     * Get XP target for current level.
     * @return XP required to reach next level
     */
    int getXpTarget();
    
    /**
     * Get streak days.
     * @return Current streak in days
     */
    int getStreakDays();
    
    /**
     * Set streak days.
     * @param days Number of streak days
     */
    void setStreakDays(int days);
    
    /**
     * Calculate level from total XP.
     * @param totalXp Total XP accumulated
     * @return Level number
     */
    int calculateLevel(int totalXp);
    
    /**
     * Calculate XP target for a given level.
     * @param level Level number
     * @return XP required to reach next level
     */
    int calculateXpTarget(int level);
}
