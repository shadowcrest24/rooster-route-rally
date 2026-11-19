package com.Mohcasi3.Xurgox.data.sample

import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.domain.model.Mission
import com.Mohcasi3.Xurgox.domain.model.MissionKind
import com.Mohcasi3.Xurgox.domain.model.RewardTier
import com.Mohcasi3.Xurgox.domain.model.RouteBadge
import com.Mohcasi3.Xurgox.domain.model.Stage

object SampleData {

    val stages: List<Stage> = listOf(
        Stage(1, R.string.stage_dawn, activeLanes = 4, speedScale = 0.82f, spacingScale = 1.22f),
        Stage(2, R.string.stage_market, activeLanes = 5, speedScale = 0.95f, spacingScale = 1.08f),
        Stage(3, R.string.stage_harbor, activeLanes = 6, speedScale = 1.08f, spacingScale = 0.98f),
        Stage(4, R.string.stage_express, activeLanes = 7, speedScale = 1.22f, spacingScale = 0.90f)
    )

    val badges: List<RouteBadge> = listOf(
        RouteBadge("b_dawn_bell", R.string.badge_dawn_bell, 1, 2),
        RouteBadge("b_mill_gate", R.string.badge_mill_gate, 1, 4),
        RouteBadge("b_barn_lantern", R.string.badge_barn_lantern, 1, 6),
        RouteBadge("b_cart_wheel", R.string.badge_cart_wheel, 1, 2),
        RouteBadge("b_field_post", R.string.badge_field_post, 1, 4),
        RouteBadge("b_sunrise_flag", R.string.badge_sunrise_flag, 1, 6),
        RouteBadge("b_market_gate", R.string.badge_market_gate, 2, 2),
        RouteBadge("b_spice_crate", R.string.badge_spice_crate, 2, 4),
        RouteBadge("b_brass_scale", R.string.badge_brass_scale, 2, 6),
        RouteBadge("b_awning_stripe", R.string.badge_awning_stripe, 2, 2),
        RouteBadge("b_coin_basket", R.string.badge_coin_basket, 2, 4),
        RouteBadge("b_clock_tower", R.string.badge_clock_tower, 2, 6),
        RouteBadge("b_harbor_light", R.string.badge_harbor_light, 3, 2),
        RouteBadge("b_rope_coil", R.string.badge_rope_coil, 3, 4),
        RouteBadge("b_anchor_pin", R.string.badge_anchor_pin, 3, 6),
        RouteBadge("b_gull_feather", R.string.badge_gull_feather, 3, 2),
        RouteBadge("b_tide_marker", R.string.badge_tide_marker, 3, 4),
        RouteBadge("b_crane_arm", R.string.badge_crane_arm, 3, 6),
        RouteBadge("b_neon_arrow", R.string.badge_neon_arrow, 4, 2),
        RouteBadge("b_lane_seven", R.string.badge_lane_seven, 4, 4),
        RouteBadge("b_signal_core", R.string.badge_signal_core, 4, 6),
        RouteBadge("b_chrome_grille", R.string.badge_chrome_grille, 4, 2),
        RouteBadge("b_pulse_ring", R.string.badge_pulse_ring, 4, 4),
        RouteBadge("b_night_crest", R.string.badge_night_crest, 4, 6)
    )

    val missions: List<Mission> = listOf(
        Mission("m_badges", R.string.mission_badges, R.string.mission_badges_hint, MissionKind.BADGES, 10, 120),
        Mission("m_lanes", R.string.mission_lanes, R.string.mission_lanes_hint, MissionKind.LANES_IN_RUN, 20, 180),
        Mission("m_time", R.string.mission_time, R.string.mission_time_hint, MissionKind.TIME_LEFT, 20, 180),
        Mission("m_streak", R.string.mission_streak, R.string.mission_streak_hint, MissionKind.STREAK, 3, 120),
        Mission("m_dash", R.string.mission_dash, R.string.mission_dash_hint, MissionKind.DASH, 3, 120),
        Mission("m_stage", R.string.mission_stage, R.string.mission_stage_hint, MissionKind.CLEAR_STAGE, 2, 240)
    )

    val rewardTiers: List<RewardTier> = listOf(
        RewardTier("t_bronze", R.string.tier_bronze, 200, R.drawable.ic_trophy),
        RewardTier("t_dawn", R.string.tier_dawn, 400, R.drawable.ic_flag),
        RewardTier("t_silver", R.string.tier_silver, 600, R.drawable.ic_trophy),
        RewardTier("t_harbor", R.string.tier_harbor, 900, R.drawable.ic_flag),
        RewardTier("t_gold", R.string.tier_gold, 1200, R.drawable.ic_trophy),
        RewardTier("t_express", R.string.tier_express, 1600, R.drawable.ic_flag)
    )

    fun badgesForStage(stage: Int): List<RouteBadge> = badges.filter { it.stage == stage }

    fun stageOf(index: Int): Stage = stages.getOrElse(index - 1) { stages.first() }
}
