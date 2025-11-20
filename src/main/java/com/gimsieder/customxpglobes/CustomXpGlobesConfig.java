/*
 * Copyright (c) 2017, Steve <steve.rs.dev@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gimsieder.customxpglobes;

import java.awt.Color;
import net.runelite.client.config .Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Units;
import net.runelite.client.config.Range;

@ConfigGroup("customxpglobes")
public interface CustomXpGlobesConfig extends Config
{
    /* ---------------- On Screen Orbs ---------------- */
    @ConfigSection(
            name = "On Screen Orbs",
            description = "Settings for On Screen Orbs",
            position = 0
    )
    String onScreenOrbs = "On Screen Orbs";

    @ConfigItem(
            keyName = "orbsPerLine",
            name = "Max Orbs Per Line",
            description = "Maximum number of XP orbs to display per row/column before wrapping",
            position = 1,
            section = onScreenOrbs
    )
    @Range(
            min = 1,
            max = 23
    )
    default int orbsPerLine() {
        return 10; // default wrap
    }

    @ConfigItem(
            keyName = "maximumShownGlobes",
            name = "Maximum Shown Orbs",
            description = "The maximum number of XP Orbs displayed at once",
            position = 2,
            section = onScreenOrbs
    )
    @Range(
            min = 1,
            max = 23
    )
    default int maximumShownGlobes() {
        return 5; // default value
    }

    @ConfigItem(
            keyName = "alignOrbsVertically",
            name = "Vertical Orbs",
            description = "Aligns the orbs vertically instead of horizontally.",
            position = 5,
            section = onScreenOrbs
    )
    default boolean alignOrbsVertically() {
        return false;
    }

    @ConfigItem(
            keyName = "forceOrbs",
            name = "Forced Orbs Before Normal Orbs",
            description = "If enabled, Forced XP globes display before normal orbs",
            position = 6,
            section = onScreenOrbs
    )
    default boolean forceOrbs() { return false; }

    enum MaxedSkillDisplay {
        NORMAL,           // Show all skills including 99
        HIDE_MAXED,       // Hide maxed skills
        SHOW_VIRTUAL      // Show virtual levels over 99
    }

    @ConfigItem(
            keyName = "maxedSkillDisplay",
            name = "Maxed Skill Display",
            description = "Controls how maxed skills are displayed in XP globes",
            position = 7,
            section = onScreenOrbs
    )
    default MaxedSkillDisplay maxedSkillDisplay() {
        return MaxedSkillDisplay.NORMAL;
    }

    @ConfigItem(
            keyName = "Orb duration",
            name = "Duration of orbs",
            description = "Change the duration the XP orbs are visible.",
            position = 8,
            section = onScreenOrbs
    )
    @Units(Units.SECONDS)
    default int xpOrbDuration() { return 10; }

    @ConfigItem(
            keyName = "lastLineAlignment",
            name = "Last Line Alignment",
            description = "How to align the last row/column if it is not full",
            position = 9,
            section = onScreenOrbs
    )
    default LastLineAlignment lastLineAlignment()
    {
        return LastLineAlignment.CENTER; // default
    }

    enum LastLineAlignment
    {
        LEFT("Left / Top"),
        CENTER("Center"),
        RIGHT("Right / Bottom");

        private final String displayName;

        LastLineAlignment(String displayName)
        {
            this.displayName = displayName;
        }

        @Override
        public String toString()
        {
            return displayName;
        }
    }

    /* ---------------- Customize Orbs ---------------- */
    enum LevelDisplayOption
    {
        NONE,
        SKILL_COLOR,
        CUSTOM_COLOR
    }

    @ConfigSection(
            name = "Customize Orbs",
            description = "Settings for Customizing Orbs",
            position = 12,
            closedByDefault = true
    )
    String customizeOrbs = "Customize Orbs";

    @ConfigItem(
            keyName = "Orb size",
            name = "Size of orbs",
            description = "Change the size of the XP orbs.",
            position = 13,
            section = customizeOrbs
    )
    @Units(Units.PIXELS)
    default int xpOrbSize() {
        return 40;
    }

    @ConfigItem(
            keyName = "enableCustomArcColor",
            name = "Enable custom arc color",
            description = "Enables the custom coloring of the globe's arc instead of using the skill's default color.",
            position = 14,
            section = customizeOrbs
    )
    default boolean enableCustomArcColor() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "Progress arc color",
            name = "Progress arc color",
            description = "Change the color of the progress arc in the XP orb.",
            position = 15,
            section = customizeOrbs
    )
    default Color progressArcColor() {
        return Color.ORANGE;
    }

    @Alpha
    @ConfigItem(
            keyName = "Progress orb outline color",
            name = "Progress orb outline color",
            description = "Change the color of the progress orb outline.",
            position = 16,
            section = customizeOrbs
    )
    default Color progressOrbOutLineColor() {
        return Color.BLACK;
    }

    @Alpha
    @ConfigItem(
            keyName = "Progress orb background color",
            name = "Progress orb background color",
            description = "Change the color of the progress orb background.",
            position = 17,
            section = customizeOrbs
    )
    default Color progressOrbBackgroundColor() {
        return new Color(128, 128, 128, 127);
    }

    @ConfigItem(
            keyName = "Progress arc width",
            name = "Progress arc width",
            description = "Change the stroke width of the progress arc.",
            position = 18,
            section = customizeOrbs
    )
    @Units(Units.PIXELS)
    default int progressArcStrokeWidth() {
        return 2;
    }

    @ConfigItem(
            keyName = "levelDisplayOption",
            name = "Level Display",
            description = "Choose how the level is displayed inside the orb",
            position = 19,
            section = customizeOrbs
    )
    default LevelDisplayOption levelDisplayOption()
    {
        return LevelDisplayOption.SKILL_COLOR;
    }

    // Helper for conditional visibility
    default boolean isCustomLevel()
    {
        return levelDisplayOption() == LevelDisplayOption.CUSTOM_COLOR;
    }

    @Alpha
    @ConfigItem(
            keyName = "levelColor",
            name = "Level Color",
            description = "Color of the skill level text inside the orb",
            position = 20,
            section = customizeOrbs
    )
    default Color levelColor()
    {
        return Color.YELLOW;
    }

    @ConfigItem(
            keyName = "levelSize",
            name = "Level Size",
            description = "Adjust the level text size (-100 to 100)",
            position = 21,
            section = customizeOrbs
    )
    @Range(min = 0, max = 50)
    default int levelSize()
    {
        return 0;
    }

    @Range(min = -100, max = 100) // allows -100 to +100
    @ConfigItem(
            keyName = "iconVerticalOffset",
            name = "Icon Vertical Offset",
            description = "Move the skill icon up (positive) or down (negative) inside the orb (pixels).",
            position = 24,
            section = customizeOrbs
    )
    default int iconVerticalOffset() {
        return 6;
    }

    @Range(min = -100, max = 100) // allows -100 to +100
    @ConfigItem(
            keyName = "levelVerticalOffset",
            name = "Level Vertical Offset",
            description = "Move the skill level text up (positive) or down (negative) inside the orb (pixels).",
            position = 25,
            section = customizeOrbs
    )
    default int levelVerticalOffset() {
        return -12;
    }

    /* ---------------- Orb Tooltips ---------------- */
    @ConfigSection(
            name = "Orb Tooltips",
            description = "Settings for Orb Tooltips",
            position = 30,
            closedByDefault = true
    )
    String orbTooltips = "Orb Tooltips";

    enum TooltipLine
    {
        NONE("None"),
        CURRENT_TOTAL_XP("Current Total XP"),
        XP_LEFT_FOR_LEVEL("XP Left For Level"),
        ACTIONS_LEFT_BEFORE_LEVEL("Actions Left Before Level"),
        XP_PER_HOUR("XP Per Hour"),
        TIME_TILL_LEVEL("Time Till Level"),
        BLANK("Spacer");

        private final String displayName;

        TooltipLine(String displayName)
        {
            this.displayName = displayName;
        }

        @Override
        public String toString()
        {
            return displayName;
        }
    }

    // ---------------- Enable Tooltips ----------------
    @ConfigItem(
            keyName = "enableTooltips",
            name = "Enable Tooltips",
            description = "Show or hide XP tooltips on the orbs",
            position = 31,
            section = orbTooltips
    )
    default boolean enableTooltips()
    {
        return true;
    }

    // ---------------- Tooltip Lines ----------------
    @ConfigItem(keyName = "tooltipLine1", name = "Line 1", description = "Select the first line", position = 32, section = orbTooltips)
    default TooltipLine tooltipLine1() { return TooltipLine.CURRENT_TOTAL_XP; }

    @ConfigItem(keyName = "tooltipLine2", name = "Line 2", description = "Select the second line", position = 33, section = orbTooltips)
    default TooltipLine tooltipLine2() { return TooltipLine.XP_LEFT_FOR_LEVEL; }

    @ConfigItem(keyName = "tooltipLine3", name = "Line 3", description = "Select the third line", position = 34, section = orbTooltips)
    default TooltipLine tooltipLine3() { return TooltipLine.NONE; }

    @ConfigItem(keyName = "tooltipLine4", name = "Line 4", description = "Select the fourth line", position = 35, section = orbTooltips)
    default TooltipLine tooltipLine4() { return TooltipLine.NONE; }

    @ConfigItem(keyName = "tooltipLine5", name = "Line 5", description = "Select the fifth line", position = 36, section = orbTooltips)
    default TooltipLine tooltipLine5() { return TooltipLine.NONE; }

    // ---------------- Tooltip Colors ----------------
    @ConfigItem(
            keyName = "enableTooltipColors",
            name = "Enable Tooltip Colors",
            description = "Use individual colors for each tooltip line",
            position = 37,
            section = orbTooltips
    )
    default boolean enableTooltipColors() { return false; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine1Color", name = "Line 1 Color", description = "Color for line 1", position = 39, section = orbTooltips)
    default Color tooltipLine1Color() { return Color.ORANGE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine2Color", name = "Line 2 Color", description = "Color for line 2", position = 40, section = orbTooltips)
    default Color tooltipLine2Color() { return Color.ORANGE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine3Color", name = "Line 3 Color", description = "Color for line 3", position = 41, section = orbTooltips)
    default Color tooltipLine3Color() { return Color.ORANGE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine4Color", name = "Line 4 Color", description = "Color for line 4", position = 42, section = orbTooltips)
    default Color tooltipLine4Color() { return Color.ORANGE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine5Color", name = "Line 5 Color", description = "Color for line 5", position = 43, section = orbTooltips)
    default Color tooltipLine5Color() { return Color.ORANGE; }

    // ---------------- Tooltip Value Colors ----------------
    @ConfigItem(
            keyName = "enableTooltipValueColors",
            name = "Enable Tooltip Value Colors",
            description = "Use individual colors for each tooltip line value",
            position = 44,
            section = orbTooltips
    )
    default boolean enableTooltipValueColors() { return false; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine1ValueColor", name = "Line 1 Value Color", description = "Color for line 1 value", position = 46, section = orbTooltips)
    default Color tooltipLine1ValueColor() { return Color.WHITE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine2ValueColor", name = "Line 2 Value Color", description = "Color for line 2 value", position = 47, section = orbTooltips)
    default Color tooltipLine2ValueColor() { return Color.WHITE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine3ValueColor", name = "Line 3 Value Color", description = "Color for line 3 value", position = 48, section = orbTooltips)
    default Color tooltipLine3ValueColor() { return Color.WHITE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine4ValueColor", name = "Line 4 Value Color", description = "Color for line 4 value", position = 49, section = orbTooltips)
    default Color tooltipLine4ValueColor() { return Color.WHITE; }

    @Alpha
    @ConfigItem(keyName = "tooltipLine5ValueColor", name = "Line 5 Value Color", description = "Color for line 5 value", position = 50, section = orbTooltips)
    default Color tooltipLine5ValueColor() { return Color.WHITE; }

    // ---------------- Skill Name and Level Options ----------------
    enum SkillNameOption
    {
        DEFAULT("Default"),               // Default color (ORANGE for name, WHITE for level)
        MATCH_NAME_COLOR("Skill Color"), // Use the skill's color
        CUSTOM_COLOR("Custom Color");     // Use custom alpha color

        private final String displayName;

        SkillNameOption(String displayName)
        {
            this.displayName = displayName;
        }

        @Override
        public String toString()
        {
            return displayName;
        }
    }

    @ConfigItem(
            keyName = "tooltipSkillNameOption",
            name = "Tooltip Skill Name Option",
            description = "Choose how the skill name color is determined in the tooltip",
            position = 51,
            section = orbTooltips
    )
    default SkillNameOption tooltipSkillNameOption()
    {
        return SkillNameOption.DEFAULT;
    }

    @Alpha
    @ConfigItem(
            keyName = "tooltipSkillNameColor",
            name = "Tooltip Skill Name Color",
            description = "Custom color for skill name (used if 'Custom Color' is selected above)",
            position = 52,
            section = orbTooltips
    )
    default Color tooltipSkillNameColor()
    {
        return Color.decode("#FFFC800");
    }

    @ConfigItem(
            keyName = "tooltipSkillLevelOption",
            name = "Tooltip Skill Level Option",
            description = "Choose how the skill level color is determined in the tooltip",
            position = 53,
            section = orbTooltips
    )
    default SkillNameOption tooltipSkillLevelOption()
    {
        return SkillNameOption.DEFAULT;
    }

    @Alpha
    @ConfigItem(
            keyName = "tooltipSkillLevelColor",
            name = "Tooltip Skill Level Color",
            description = "Custom color for skill level (used if 'Custom Color' is selected above)",
            position = 54,
            section = orbTooltips
    )
    default Color tooltipSkillLevelColor()
    {
        return Color.WHITE;
    }

    /* ---------------- Config Sections ---------------- */
    @ConfigSection(
            name = "Per Orb Loading",
            description = "Set Each Orb To Normal / Forced / Blacklist",
            position = 60,
            closedByDefault = true
    )
    String orbMode = "orbMode";

    @ConfigSection(
            name = "Per Orb Priority",
            description = "Set Priority Level For Each Orb",
            position = 90,
            closedByDefault = true
    )
    String orbPriority = "orbPriority";

    enum SkillDisplayMode
    {
        NORMAL,   // Display XP globe normally (default behavior)
        FORCE,    // Always display XP globe regardless of XP gains
        BLACKLIST // Never display XP globe
    }

        /* ---------------- 1. Agility ---------------- */
    @ConfigItem(keyName = "agilityDisplay", name = "Agility", description = "Agility Orb Display Settings", position = 61, section = orbMode)
    default SkillDisplayMode agilityDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "agilityPriority", name = "Agility Priority", description = "Priority for Agility orb (1-24)", position = 62, section = orbPriority)
    @Range(min = 1, max = 24)
    default int agilityPriority() { return 10; }

    /* ---------------- 2. Attack Skill Settings ---------------- */
    @ConfigItem(keyName = "attackDisplay", name = "Attack", description = "Attack Orb Display Settings", position = 63, section = orbMode)
    default SkillDisplayMode attackDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "attackPriority", name = "Attack Priority", description = "Priority for Attack orb (1-24)", position = 64, section = orbPriority)
    @Range(min = 1, max = 24)
    default int attackPriority() { return 1; }

    /* ---------------- 3. Construction Skill Settings ---------------- */
    @ConfigItem(keyName = "constructionDisplay", name = "Construction", description = "Construction Orb Display Settings", position = 65, section = orbMode)
    default SkillDisplayMode constructionDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "constructionPriority", name = "Construction Priority", description = "Priority for Construction orb (1-24)", position = 66, section = orbPriority)
    @Range(min = 1, max = 24)
    default int constructionPriority() { return 8; }

    /* ---------------- 4. Cooking Skill Settings ---------------- */
    @ConfigItem(keyName = "cookingDisplay", name = "Cooking", description = "Cooking Orb Display Settings", position = 67, section = orbMode)
    default SkillDisplayMode cookingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "cookingPriority", name = "Cooking Priority", description = "Priority for Cooking orb (1-24)", position = 68, section = orbPriority)
    @Range(min = 1, max = 24)
    default int cookingPriority() { return 20; }

    /* ---------------- 5. Crafting Skill Settings ---------------- */
    @ConfigItem(keyName = "craftingDisplay", name = "Crafting", description = "Crafting Orb Display Settings", position = 69, section = orbMode)
    default SkillDisplayMode craftingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "craftingPriority", name = "Crafting Priority", description = "Priority for Crafting orb (1-24)", position = 70, section = orbPriority)
    @Range(min = 1, max = 24)
    default int craftingPriority() { return 13; }

    /* ---------------- 6. Defence Skill Settings ---------------- */
    @ConfigItem(keyName = "defenceDisplay", name = "Defence", description = "Defence Orb Display Settings", position = 71, section = orbMode)
    default SkillDisplayMode defenceDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "defencePriority", name = "Defence Priority", description = "Priority for Defence orb (1-24)", position = 72, section = orbPriority)
    @Range(min = 1, max = 24)
    default int defencePriority() { return 3; }

    /* ---------------- 7. Farming Skill Settings ---------------- */
    @ConfigItem(keyName = "farmingDisplay", name = "Farming", description = "Farming Orb Display Settings", position = 73, section = orbMode)
    default SkillDisplayMode farmingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "farmingPriority", name = "Farming Priority", description = "Priority for Farming orb (1-24)", position = 74, section = orbPriority)
    @Range(min = 1, max = 24)
    default int farmingPriority() { return 23; }

    /* ---------------- 8. Firemaking Skill Settings ---------------- */
    @ConfigItem(keyName = "firemakingDisplay", name = "Firemaking", description = "Firemaking Orb Display Settings", position = 75, section = orbMode)
    default SkillDisplayMode firemakingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "firemakingPriority", name = "Firemaking Priority", description = "Priority for Firemaking orb (1-24)", position = 76, section = orbPriority)
    @Range(min = 1, max = 24)
    default int firemakingPriority() { return 21; }

    /* ---------------- 9. Fletching Skill Settings ---------------- */
    @ConfigItem(keyName = "fletchingDisplay", name = "Fletching", description = "Fletching Orb Display Settings", position = 77, section = orbMode)
    default SkillDisplayMode fletchingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "fletchingPriority", name = "Fletching Priority", description = "Priority for Fletching orb (1-24)", position = 78, section = orbPriority)
    @Range(min = 1, max = 24)
    default int fletchingPriority() { return 14; }

    /* ---------------- 10. Fishing Skill Settings ---------------- */
    @ConfigItem(keyName = "fishingDisplay", name = "Fishing", description = "Fishing Orb Display Settings", position = 79, section = orbMode)
    default SkillDisplayMode fishingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "fishingPriority", name = "Fishing Priority", description = "Priority for Fishing orb (1-24)", position = 80, section = orbPriority)
    @Range(min = 1, max = 24)
    default int fishingPriority() { return 19; }

    /* ---------------- 11. Herblore Skill Settings ---------------- */
    @ConfigItem(keyName = "herbloreDisplay", name = "Herblore", description = "Herblore Orb Display Settings", position = 81, section = orbMode)
    default SkillDisplayMode herbloreDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "herblorePriority", name = "Herblore Priority", description = "Priority for Herblore orb (1-24)", position = 82, section = orbPriority)
    @Range(min = 1, max = 24)
    default int herblorePriority() { return 11; }

    /* ---------------- 12. Hitpoints Skill Settings ---------------- */
    @ConfigItem(keyName = "hitpointsDisplay", name = "Hitpoints", description = "Hitpoints Orb Display Settings", position = 83, section = orbMode)
    default SkillDisplayMode hitpointsDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "hitpointsPriority", name = "Hitpoints Priority", description = "Priority for Hitpoints orb (1-24)", position = 84, section = orbPriority)
    @Range(min = 1, max = 24)
    default int hitpointsPriority() { return 9; }

    /* ---------------- 13. Hunter Skill Settings ---------------- */
    @ConfigItem(keyName = "hunterDisplay", name = "Hunter", description = "Hunter Orb Display Settings", position = 85, section = orbMode)
    default SkillDisplayMode hunterDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "hunterPriority", name = "Hunter Priority", description = "Priority for Hunter orb (1-24)", position = 86, section = orbPriority)
    @Range(min = 1, max = 24)
    default int hunterPriority() { return 16; }

    /* ---------------- 14. Magic Skill Settings ---------------- */
    @ConfigItem(keyName = "magicDisplay", name = "Magic", description = "Magic Orb Display Settings", position = 87, section = orbMode)
    default SkillDisplayMode magicDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "magicPriority", name = "Magic Priority", description = "Priority for Magic orb (1-24)", position = 88, section = orbPriority)
    @Range(min = 1, max = 24)
    default int magicPriority() { return 6; }

    /* ---------------- 15. Mining Skill Settings ---------------- */
    @ConfigItem(keyName = "miningDisplay", name = "Mining", description = "Mining Orb Display Settings", position = 89, section = orbMode)
    default SkillDisplayMode miningDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "miningPriority", name = "Mining Priority", description = "Priority for Mining orb (1-24)", position = 90, section = orbPriority)
    @Range(min = 1, max = 24)
    default int miningPriority() { return 17; }

    /* ---------------- 16. Prayer Skill Settings ---------------- */
    @ConfigItem(keyName = "prayerDisplay", name = "Prayer", description = "Prayer Orb Display Settings", position = 91, section = orbMode)
    default SkillDisplayMode prayerDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "prayerPriority", name = "Prayer Priority", description = "Priority for Prayer orb (1-24)", position = 92, section = orbPriority)
    @Range(min = 1, max = 24)
    default int prayerPriority() { return 5; }

    /* ---------------- 17. Ranged Skill Settings ---------------- */
    @ConfigItem(keyName = "rangedDisplay", name = "Ranged", description = "Ranged Orb Display Settings", position = 93, section = orbMode)
    default SkillDisplayMode rangedDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "rangedPriority", name = "Ranged Priority", description = "Priority for Ranged orb (1-24)", position = 94, section = orbPriority)
    @Range(min = 1, max = 24)
    default int rangedPriority() { return 4; }

    /* ---------------- 18. Runecraft Skill Settings ---------------- */
    @ConfigItem(keyName = "runecraftDisplay", name = "Runecraft", description = "Runecraft Orb Display Settings", position = 95, section = orbMode)
    default SkillDisplayMode runecraftDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "runecraftPriority", name = "Runecraft Priority", description = "Priority for Runecraft orb (1-24)", position = 96, section = orbPriority)
    @Range(min = 1, max = 24)
    default int runecraftPriority() { return 7; }

    /* ---------------- 19. Slayer Skill Settings ---------------- */
    @ConfigItem(keyName = "slayerDisplay", name = "Slayer", description = "Slayer Orb Display Settings", position = 97, section = orbMode)
    default SkillDisplayMode slayerDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "slayerPriority", name = "Slayer Priority", description = "Priority for Slayer orb (1-24)", position = 98, section = orbPriority)
    @Range(min = 1, max = 24)
    default int slayerPriority() { return 15; }

    /* ---------------- 20. Smithing Skill Settings ---------------- */
    @ConfigItem(keyName = "smithingDisplay", name = "Smithing", description = "Smithing Orb Display Settings", position = 99, section = orbMode)
    default SkillDisplayMode smithingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "smithingPriority", name = "Smithing Priority", description = "Priority for Smithing orb (1-24)", position = 100, section = orbPriority)
    @Range(min = 1, max = 24)
    default int smithingPriority() { return 18; }

    /* ---------------- 21. Strength Skill Settings ---------------- */
    @ConfigItem(keyName = "strengthDisplay", name = "Strength", description = "Strength Orb Display Settings", position = 101, section = orbMode)
    default SkillDisplayMode strengthDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "strengthPriority", name = "Strength Priority", description = "Priority for Strength orb (1-24)", position = 102, section = orbPriority)
    @Range(min = 1, max = 24)
    default int strengthPriority() { return 2; }

    /* ---------------- 22. Thieving Skill Settings ---------------- */
    @ConfigItem(keyName = "thievingDisplay", name = "Thieving", description = "Thieving Orb Display Settings", position = 103, section = orbMode)
    default SkillDisplayMode thievingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "thievingPriority", name = "Thieving Priority", description = "Priority for Thieving orb (1-24)", position = 104, section = orbPriority)
    @Range(min = 1, max = 24)
    default int thievingPriority() { return 12; }

    /* ---------------- 23. Woodcutting Skill Settings ---------------- */
    @ConfigItem(keyName = "woodcuttingDisplay", name = "Woodcutting", description = "Woodcutting Orb Display Settings", position = 105, section = orbMode)
    default SkillDisplayMode woodcuttingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "woodcuttingPriority", name = "Woodcutting Priority", description = "Priority for Woodcutting orb (1-24)", position = 106, section = orbPriority)
    @Range(min = 1, max = 24)
    default int woodcuttingPriority() { return 22; }

    /* ---------------- 24. Sailing Skill Settings ---------------- */
	@ConfigItem(keyName = "sailingDisplay", name = "Sailing", description = "SAiling Orb Display Settings", position = 107, section = orbMode)
	default SkillDisplayMode sailingDisplay() { return SkillDisplayMode.NORMAL; }

	@ConfigItem(keyName = "sailingPriority", name = "Sailing Priority", description = "Priority for Sailing orb (1-24)", position = 108, section = orbPriority)
	@Range(min = 1, max = 24)
	default int sailingPriority() { return 24; }
}
