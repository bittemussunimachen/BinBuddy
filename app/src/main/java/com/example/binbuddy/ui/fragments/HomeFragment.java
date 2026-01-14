package com.example.binbuddy.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.FragmentHomeBinding;
import com.example.binbuddy.ui.BinGuideActivity;
import com.example.binbuddy.ui.ManualEntryActivity;
import com.example.binbuddy.ui.ProductDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private ActivityResultLauncher<Intent> barcodeLauncher;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        navController = Navigation.findNavController(view);
        setupActivityResultLauncher();
        setupClickListeners();
        loadDashboard();
    }

    private void setupClickListeners() {
        binding.btnScan.setOnClickListener(v -> openScanner());
        binding.btnSearch.setOnClickListener(v -> openSearch());
        binding.btnManualEntry.setOnClickListener(v -> openManualEntry());
        binding.btnReportIssue.setOnClickListener(v -> showToast(R.string.dashboard_report_issue));
        binding.btnOpenCalendar.setOnClickListener(v -> showToast(R.string.dashboard_open_calendar));
        binding.btnOpenLeaderboard.setOnClickListener(v -> showToast(R.string.dashboard_open_leaderboard));
        binding.btnAlertAction.setOnClickListener(v -> showToast(R.string.dashboard_alert_action));
        binding.btnRefreshQuests.setOnClickListener(v -> loadDashboard());
    }

    private void setupActivityResultLauncher() {
        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        navigateToProductDetailIfValid(
                                result.getData().getStringExtra(ProductDetailActivity.EXTRA_BARCODE)
                        );
                    }
                }
        );
    }

    private void openScanner() {
        navController.navigate(R.id.scanFragment);
    }

    private void openManualEntry() {
        Intent intent = new Intent(getContext(), ManualEntryActivity.class);
        barcodeLauncher.launch(intent);
    }

    private void openBinGuide() {
        Intent intent = new Intent(getContext(), BinGuideActivity.class);
        startActivity(intent);
    }

    private void navigateToProductDetailIfValid(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return;
        }
        navigateToProductDetail(barcode.trim());
    }

    private void navigateToProductDetail(String barcode) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        startActivity(intent);
    }

    private void openSearch() {
        try {
            navController.navigate(R.id.searchFragment);
        } catch (Exception e) {
            Log.e(TAG, "Error opening search", e);
            showToast(R.string.main_error_open_search);
        }
    }

    private void showToast(int resId) {
        Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT).show();
    }

    private void loadDashboard() {
        setQuestState(ContentState.LOADING, binding.questDailyLoading, binding.questDailyEmpty, binding.questDailyError, binding.containerDailyQuests);
        setQuestState(ContentState.LOADING, binding.questWeeklyLoading, binding.questWeeklyEmpty, binding.questWeeklyError, binding.containerWeeklyQuests);

        DashboardProgress progress = new DashboardProgress(
                4,
                240,
                480,
                800,
                68,
                6,
                3,
                getString(R.string.dashboard_tip_placeholder)
        );

        DashboardData data = new DashboardData(
                progress,
                sampleDailyQuests(),
                sampleWeeklyQuests(),
                new PickupInfo(
                        getString(R.string.dashboard_pickup_window),
                        getString(R.string.dashboard_pickup_type)
                ),
                new CommunityState(
                        getString(R.string.dashboard_community_impact),
                        sampleCommunityEntries()
                ),
                new ContaminationAlert(
                        getString(R.string.dashboard_alert_title),
                        getString(R.string.dashboard_alert_body),
                        getString(R.string.dashboard_alert_action)
                )
        );

        bindDashboard(data);
    }

    private void bindDashboard(DashboardData data) {
        renderProgress(data.progress);
        renderQuests(data.dailyQuests, data.weeklyQuests);
        renderPickup(data.pickupInfo);
        renderCommunity(data.communityState);
        renderAlert(data.alert);
    }

    private void renderProgress(DashboardProgress progress) {
        binding.tvLevel.setText(String.format(Locale.getDefault(), "%d", progress.level));
        binding.tvCoins.setText(getString(R.string.dashboard_coins_format, progress.coins));

        int cappedXp = Math.min(progress.xpCurrent, progress.xpTarget);
        binding.progressXp.setMax(progress.xpTarget);
        binding.progressXp.setProgress(cappedXp);
        int gap = Math.max(0, progress.xpTarget - progress.xpCurrent);
        binding.tvXpGap.setText(getString(R.string.dashboard_next_level_hint, gap));

        binding.progressDiversion.setProgress(progress.diversionPercent);
        binding.tvDiversionPercent.setText(String.format(Locale.getDefault(), "%d%%", progress.diversionPercent));

        binding.chipStreak.setText(getString(R.string.dashboard_streak_value, progress.streakDays));
        int cleanPercent = Math.max(0, 100 - progress.contaminationPercent);
        binding.chipContamination.setText(getString(R.string.dashboard_contamination_value, cleanPercent));
        binding.tvProgressTip.setText(selectProgressTip(progress));
    }

    private void renderQuests(List<Quest> dailyQuests, List<Quest> weeklyQuests) {
        if (dailyQuests.isEmpty()) {
            setQuestState(ContentState.EMPTY, binding.questDailyLoading, binding.questDailyEmpty, binding.questDailyError, binding.containerDailyQuests);
        } else {
            setQuestState(ContentState.CONTENT, binding.questDailyLoading, binding.questDailyEmpty, binding.questDailyError, binding.containerDailyQuests);
            renderQuestList(dailyQuests, binding.containerDailyQuests);
        }

        if (weeklyQuests.isEmpty()) {
            setQuestState(ContentState.EMPTY, binding.questWeeklyLoading, binding.questWeeklyEmpty, binding.questWeeklyError, binding.containerWeeklyQuests);
        } else {
            setQuestState(ContentState.CONTENT, binding.questWeeklyLoading, binding.questWeeklyEmpty, binding.questWeeklyError, binding.containerWeeklyQuests);
            renderQuestList(weeklyQuests, binding.containerWeeklyQuests);
        }
    }

    private void renderQuestList(List<Quest> quests, LinearLayout container) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Quest quest : quests) {
            View card = inflater.inflate(R.layout.item_quest, container, false);
            bindQuestCard(quest, card);
            container.addView(card);
        }
    }

    private void bindQuestCard(Quest quest, View card) {
        TextView title = card.findViewById(R.id.tvQuestTitle);
        TextView reward = card.findViewById(R.id.tvQuestReward);
        TextView progressText = card.findViewById(R.id.tvQuestProgress);
        LinearProgressIndicator progressIndicator = card.findViewById(R.id.progressQuest);
        MaterialButton action = card.findViewById(R.id.btnQuestAction);

        title.setText(quest.title);
        reward.setText(formatReward(quest));
        updateQuestVisuals(quest, progressText, progressIndicator, action);

        action.setOnClickListener(v -> handleQuestAction(quest, progressText, progressIndicator, action));
    }

    private void handleQuestAction(Quest quest,
                                   TextView progressText,
                                   LinearProgressIndicator progressIndicator,
                                   MaterialButton action) {
        if (quest.status == QuestStatus.CLAIMED) {
            return;
        }

        if (quest.status == QuestStatus.READY_TO_CLAIM) {
            quest.status = QuestStatus.CLAIMED;
            showToast(R.string.dashboard_quest_claimed);
        } else {
            quest.progress = Math.min(quest.target, quest.progress + 1);
            if (quest.progress >= quest.target) {
                quest.status = QuestStatus.READY_TO_CLAIM;
            }
        }
        updateQuestVisuals(quest, progressText, progressIndicator, action);
    }

    private void updateQuestVisuals(Quest quest,
                                    TextView progressText,
                                    LinearProgressIndicator progressIndicator,
                                    MaterialButton action) {
        progressIndicator.setMax(quest.target);
        progressIndicator.setProgress(quest.progress);
        progressText.setText(String.format(Locale.getDefault(), "%d/%d · %s", quest.progress, quest.target, formatReward(quest)));

        switch (quest.status) {
            case READY_TO_CLAIM:
                action.setText(R.string.dashboard_quest_claim_cta);
                action.setEnabled(true);
                break;
            case CLAIMED:
                action.setText(R.string.dashboard_quest_claimed);
                action.setEnabled(false);
                break;
            default:
                action.setText(R.string.dashboard_quest_resume_cta);
                action.setEnabled(true);
        }
    }

    private String formatReward(Quest quest) {
        return String.format(Locale.getDefault(), "+%d XP · +%d coins", quest.xpReward, quest.coinsReward);
    }

    private void renderPickup(PickupInfo pickupInfo) {
        binding.tvPickupWindow.setText(pickupInfo.window);
        binding.tvPickupType.setText(pickupInfo.type);
    }

    private void renderCommunity(CommunityState state) {
        binding.tvCommunityImpact.setText(state.impactCopy);
        binding.containerLeaderboard.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (CommunityEntry entry : state.entries) {
            TextView view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, binding.containerLeaderboard, false);
            view.setText(String.format(Locale.getDefault(), "%d. %s — %s", entry.rank, entry.name, entry.impact));
            binding.containerLeaderboard.addView(view);
        }
    }

    private void renderAlert(ContaminationAlert alert) {
        binding.tvAlertTitle.setText(alert.title);
        binding.tvAlertMessage.setText(alert.message);
        binding.btnAlertAction.setText(alert.actionLabel);
    }

    private List<Quest> sampleDailyQuests() {
        List<Quest> quests = new ArrayList<>();
        quests.add(new Quest("daily_scan", getString(R.string.main_scan_headline), QuestType.DAILY, 1, 2, 30, 15, QuestStatus.IN_PROGRESS));
        quests.add(new Quest("daily_sort", "Sort 3 items correctly", QuestType.DAILY, 3, 3, 50, 20, QuestStatus.READY_TO_CLAIM));
        quests.add(new Quest("daily_share", "Share a tip with a neighbor", QuestType.DAILY, 0, 1, 40, 10, QuestStatus.IN_PROGRESS));
        return quests;
    }

    private List<Quest> sampleWeeklyQuests() {
        List<Quest> quests = new ArrayList<>();
        quests.add(new Quest("weekly_clean", "Keep contamination under 5%", QuestType.WEEKLY, 4, 7, 120, 60, QuestStatus.IN_PROGRESS));
        quests.add(new Quest("weekly_invite", "Invite a friend to Bin Buddy", QuestType.WEEKLY, 1, 1, 90, 40, QuestStatus.READY_TO_CLAIM));
        return quests;
    }

    private List<CommunityEntry> sampleCommunityEntries() {
        List<CommunityEntry> entries = new ArrayList<>();
        entries.add(new CommunityEntry(1, "Team Nord", "320 kg"));
        entries.add(new CommunityEntry(2, "Haus 14", "280 kg"));
        entries.add(new CommunityEntry(3, "Block C", "250 kg"));
        return entries;
    }

    private void setQuestState(ContentState state,
                               LinearProgressIndicator loading,
                               TextView empty,
                               TextView error,
                               LinearLayout list) {
        loading.setVisibility(state == ContentState.LOADING ? View.VISIBLE : View.GONE);
        empty.setVisibility(state == ContentState.EMPTY ? View.VISIBLE : View.GONE);
        error.setVisibility(state == ContentState.ERROR ? View.VISIBLE : View.GONE);
        list.setVisibility(state == ContentState.CONTENT ? View.VISIBLE : View.GONE);
    }

    private String selectProgressTip(DashboardProgress progress) {
        if (progress.contaminationPercent > 10) {
            return getString(R.string.dashboard_tip_high_contamination);
        }
        if (progress.diversionPercent >= 80) {
            return getString(R.string.dashboard_tip_great_job);
        }
        if (progress.diversionPercent >= 60) {
            return getString(R.string.dashboard_tip_push_diversion);
        }
        return progress.tip;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private enum QuestStatus {
        IN_PROGRESS,
        READY_TO_CLAIM,
        CLAIMED
    }

    private enum QuestType {
        DAILY,
        WEEKLY
    }

    private enum ContentState {
        LOADING,
        CONTENT,
        EMPTY,
        ERROR
    }

    private static class DashboardProgress {
        final int level;
        final int coins;
        final int xpCurrent;
        final int xpTarget;
        final int diversionPercent;
        final int streakDays;
        final int contaminationPercent;
        final String tip;

        DashboardProgress(int level,
                          int coins,
                          int xpCurrent,
                          int xpTarget,
                          int diversionPercent,
                          int streakDays,
                          int contaminationPercent,
                          String tip) {
            this.level = level;
            this.coins = coins;
            this.xpCurrent = xpCurrent;
            this.xpTarget = xpTarget;
            this.diversionPercent = diversionPercent;
            this.streakDays = streakDays;
            this.contaminationPercent = contaminationPercent;
            this.tip = tip;
        }
    }

    private static class DashboardData {
        final DashboardProgress progress;
        final List<Quest> dailyQuests;
        final List<Quest> weeklyQuests;
        final PickupInfo pickupInfo;
        final CommunityState communityState;
        final ContaminationAlert alert;

        DashboardData(DashboardProgress progress,
                      List<Quest> dailyQuests,
                      List<Quest> weeklyQuests,
                      PickupInfo pickupInfo,
                      CommunityState communityState,
                      ContaminationAlert alert) {
            this.progress = progress;
            this.dailyQuests = dailyQuests;
            this.weeklyQuests = weeklyQuests;
            this.pickupInfo = pickupInfo;
            this.communityState = communityState;
            this.alert = alert;
        }
    }

    private static class Quest {
        final String id;
        final String title;
        final QuestType type;
        int progress;
        final int target;
        final int xpReward;
        final int coinsReward;
        QuestStatus status;

        Quest(String id,
              String title,
              QuestType type,
              int progress,
              int target,
              int xpReward,
              int coinsReward,
              QuestStatus status) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.progress = progress;
            this.target = target;
            this.xpReward = xpReward;
            this.coinsReward = coinsReward;
            this.status = status;
        }
    }

    private static class PickupInfo {
        final String window;
        final String type;

        PickupInfo(String window, String type) {
            this.window = window;
            this.type = type;
        }
    }

    private static class CommunityState {
        final String impactCopy;
        final List<CommunityEntry> entries;

        CommunityState(String impactCopy, List<CommunityEntry> entries) {
            this.impactCopy = impactCopy;
            this.entries = entries;
        }
    }

    private static class CommunityEntry {
        final int rank;
        final String name;
        final String impact;

        CommunityEntry(int rank, String name, String impact) {
            this.rank = rank;
            this.name = name;
            this.impact = impact;
        }
    }

    private static class ContaminationAlert {
        final String title;
        final String message;
        final String actionLabel;

        ContaminationAlert(String title, String message, String actionLabel) {
            this.title = title;
            this.message = message;
            this.actionLabel = actionLabel;
        }
    }
}