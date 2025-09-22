package net.runelite.client.plugins.customxpglobes;

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
            name = "Vertical orbs",
            description = "Aligns the orbs vertically instead of horizontally.",
            position = 5,
            section = onScreenOrbs
    )
    default boolean alignOrbsVertically() {
        return false;
    }

    @ConfigItem(
            keyName = "forceOrbs",
            name = "Force Orbs before normal orbs",
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
    default int xpOrbDuration()
    {
        return 10;
    }

    /* ---------------- Customize Orbs ---------------- */
    @ConfigSection(
            name = "Customize Orbs",
            description = "Settings for Customizing Orbs",
            position = 9,
            closedByDefault = true
    )
    String customizeOrbs = "Customize Orbs";

    @ConfigItem(
            keyName = "enableCustomArcColor",
            name = "Enable custom arc color",
            description = "Enables the custom coloring of the globe's arc instead of using the skill's default color.",
            position = 10,
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
            position = 11,
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
            position = 12,
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
            position = 13,
            section = customizeOrbs
    )
    default Color progressOrbBackgroundColor() {
        return new Color(128, 128, 128, 127);
    }

    @ConfigItem(
            keyName = "Progress arc width",
            name = "Progress arc width",
            description = "Change the stroke width of the progress arc.",
            position = 14,
            section = customizeOrbs
    )
    @Units(Units.PIXELS)
    default int progressArcStrokeWidth() {
        return 2;
    }

    @ConfigItem(
            keyName = "Orb size",
            name = "Size of orbs",
            description = "Change the size of the XP orbs.",
            position = 15,
            section = customizeOrbs
    )
    @Units(Units.PIXELS)
    default int xpOrbSize() {
        return 40;
    }

    @ConfigItem(
            keyName = "showSkillLevel",
            name = "Enable Level Display",
            description = "Show or hide the skill level inside XP orbs",
            position = 16,
            section = customizeOrbs
    )
    default boolean showSkillLevel() {
        return true;
    }

    @ConfigItem(
            keyName = "useCustomLevelColor",
            name = "Custom Level Color",
            description = "If enabled, uses the configured level color; otherwise uses the skill color",
            position = 17,
            section = customizeOrbs
    )
    default boolean useCustomLevelColor() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "levelArcColor",
            name = "Level arc color",
            description = "Color of the skill level text inside the orb",
            position = 18,
            section = customizeOrbs
    )
    default Color levelArcColor() {
        return Color.YELLOW;
    }

    @Alpha
    @ConfigItem(
            keyName = "levelBorderColor",
            name = "Level border color",
            description = "Color of the border around the skill level text",
            position = 19,
            section = customizeOrbs
    )
    default Color levelBorderColor() {
        return Color.BLACK;
    }

    @ConfigItem(
            keyName = "levelBorderWidth",
            name = "Level border width",
            description = "Width of the border around the skill level text",
            position = 20,
            section = customizeOrbs
    )
    default int levelBorderWidth() {
        return 1;
    }

    @Range(min = -100, max = 100) // allows -100 to +100
    @ConfigItem(
            keyName = "iconVerticalOffset",
            name = "Icon Vertical Offset",
            description = "Move the skill icon up (positive) or down (negative) inside the orb (pixels).",
            position = 21,
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
            position = 22,
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

    public enum TooltipLine
    {
        CURRENT_TOTAL_XP("Current Total XP"),
        XP_LEFT_FOR_LEVEL("XP Left For Level"),
        ACTIONS_LEFT_BEFORE_LEVEL("Actions Left Before Level"),
        XP_PER_HOUR("XP Per Hour"),
        TIME_TILL_LEVEL("Time Till Level");

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

    @ConfigItem(
            keyName = "enableTooltips",
            name = "Enable tooltips",
            description = "Configures whether or not to show tooltips.",
            position = 31,
            section = orbTooltips
    )
    default boolean enableTooltips() {
        return true;
    }

    // Tooltip line dropdowns
    @ConfigItem(
            keyName = "tooltipLine1",
            name = "Line 1",
            description = "Select the first line of the tooltip",
            position = 32,
            section = orbTooltips
    )
    default TooltipLine tooltipLine1() {
        return TooltipLine.CURRENT_TOTAL_XP;
    }

    @ConfigItem(
            keyName = "tooltipLine2",
            name = "Line 2",
            description = "Select the second line of the tooltip",
            position = 33,
            section = orbTooltips
    )
    default TooltipLine tooltipLine2() {
        return TooltipLine.XP_LEFT_FOR_LEVEL;
    }

    @ConfigItem(
            keyName = "tooltipLine3",
            name = "Line 3",
            description = "Select the third line of the tooltip",
            position = 34,
            section = orbTooltips
    )
    default TooltipLine tooltipLine3() {
        return TooltipLine.ACTIONS_LEFT_BEFORE_LEVEL;
    }

    @ConfigItem(
            keyName = "tooltipLine4",
            name = "Line 4",
            description = "Select the fourth line of the tooltip",
            position = 35,
            section = orbTooltips
    )
    default TooltipLine tooltipLine4() {
        return TooltipLine.XP_PER_HOUR;
    }

    @ConfigItem(
            keyName = "tooltipLine5",
            name = "Line 5",
            description = "Select the fifth line of the tooltip",
            position = 36,
            section = orbTooltips
    )
    default TooltipLine tooltipLine5() {
        return TooltipLine.TIME_TILL_LEVEL;
    }

    enum SkillDisplayMode
    {
        NORMAL,   // Display XP globe normally (default behavior)
        FORCE,    // Always display XP globe regardless of XP gains
        BLACKLIST // Never display XP globe
    }

    /* ---------------- Config Sections ---------------- */
    @ConfigSection(
            name = "Per Orb Loading",
            description = "Set Each Orb To Normal / Forced / Blacklist",
            position = 40,
            closedByDefault = true
    )
    String orbMode = "orbMode";

    @ConfigSection(
            name = "Per Orb Priority",
            description = "Set Priority Level For Each Orb",
            position = 70,
            closedByDefault = true
    )
    String orbPriority = "orbPriority";

    /* ---------------- 1. Agility ---------------- */
    @ConfigItem(keyName = "agilityDisplay", name = "Agility", description = "Agility Orb Display Settings", position = 41, section = orbMode)
    default SkillDisplayMode agilityDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "agilityPriority", name = "Agility Priority", description = "Priority for Agility orb (1-23)", position = 42, section = orbPriority)
    @Range(min = 1, max = 23)
    default int agilityPriority() { return 10; }

    /* ---------------- 2. Attack Skill Settings ---------------- */
    @ConfigItem(keyName = "attackDisplay", name = "Attack", description = "Attack Orb Display Settings", position = 43, section = orbMode)
    default SkillDisplayMode attackDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "attackPriority", name = "Attack Priority", description = "Priority for Attack orb (1-23)", position = 44, section = orbPriority)
    @Range(min = 1, max = 23)
    default int attackPriority() { return 1; }

    /* ---------------- 3. Construction Skill Settings ---------------- */
    @ConfigItem(keyName = "constructionDisplay", name = "Construction", description = "Construction Orb Display Settings", position = 45, section = orbMode)
    default SkillDisplayMode constructionDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "constructionPriority", name = "Construction Priority", description = "Priority for Construction orb (1-23)", position = 46, section = orbPriority)
    @Range(min = 1, max = 23)
    default int constructionPriority() { return 8; }

    /* ---------------- 4. Cooking Skill Settings ---------------- */
    @ConfigItem(keyName = "cookingDisplay", name = "Cooking", description = "Cooking Orb Display Settings", position = 47, section = orbMode)
    default SkillDisplayMode cookingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "cookingPriority", name = "Cooking Priority", description = "Priority for Cooking orb (1-23)", position = 48, section = orbPriority)
    @Range(min = 1, max = 23)
    default int cookingPriority() { return 20; }

    /* ---------------- 5. Crafting Skill Settings ---------------- */
    @ConfigItem(keyName = "craftingDisplay", name = "Crafting", description = "Crafting Orb Display Settings", position = 49, section = orbMode)
    default SkillDisplayMode craftingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "craftingPriority", name = "Crafting Priority", description = "Priority for Crafting orb (1-23)", position = 50, section = orbPriority)
    @Range(min = 1, max = 23)
    default int craftingPriority() { return 13; }

    /* ---------------- 6. Defence Skill Settings ---------------- */
    @ConfigItem(keyName = "defenceDisplay", name = "Defence", description = "Defence Orb Display Settings", position = 51, section = orbMode)
    default SkillDisplayMode defenceDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "defencePriority", name = "Defence Priority", description = "Priority for Defence orb (1-23)", position = 52, section = orbPriority)
    @Range(min = 1, max = 23)
    default int defencePriority() { return 3; }

    /* ---------------- 7. Farming Skill Settings ---------------- */
    @ConfigItem(keyName = "farmingDisplay", name = "Farming", description = "Farming Orb Display Settings", position = 53, section = orbMode)
    default SkillDisplayMode farmingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "farmingPriority", name = "Farming Priority", description = "Priority for Farming orb (1-23)", position = 54, section = orbPriority)
    @Range(min = 1, max = 23)
    default int farmingPriority() { return 23; }

    /* ---------------- 8. Firemaking Skill Settings ---------------- */
    @ConfigItem(keyName = "firemakingDisplay", name = "Firemaking", description = "Firemaking Orb Display Settings", position = 55, section = orbMode)
    default SkillDisplayMode firemakingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "firemakingPriority", name = "Firemaking Priority", description = "Priority for Firemaking orb (1-23)", position = 56, section = orbPriority)
    @Range(min = 1, max = 23)
    default int firemakingPriority() { return 21; }

    /* ---------------- 9. Fletching Skill Settings ---------------- */
    @ConfigItem(keyName = "fletchingDisplay", name = "Fletching", description = "Fletching Orb Display Settings", position = 57, section = orbMode)
    default SkillDisplayMode fletchingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "fletchingPriority", name = "Fletching Priority", description = "Priority for Fletching orb (1-23)", position = 58, section = orbPriority)
    @Range(min = 1, max = 23)
    default int fletchingPriority() { return 14; }

    /* ---------------- 10. Fishing Skill Settings ---------------- */
    @ConfigItem(keyName = "fishingDisplay", name = "Fishing", description = "Fishing Orb Display Settings", position = 59, section = orbMode)
    default SkillDisplayMode fishingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "fishingPriority", name = "Fishing Priority", description = "Priority for Fishing orb (1-23)", position = 60, section = orbPriority)
    @Range(min = 1, max = 23)
    default int fishingPriority() { return 19; }

    /* ---------------- 11. Herblore Skill Settings ---------------- */
    @ConfigItem(keyName = "herbloreDisplay", name = "Herblore", description = "Herblore Orb Display Settings", position = 61, section = orbMode)
    default SkillDisplayMode herbloreDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "herblorePriority", name = "Herblore Priority", description = "Priority for Herblore orb (1-23)", position = 62, section = orbPriority)
    @Range(min = 1, max = 23)
    default int herblorePriority() { return 11; }

    /* ---------------- 12. Hitpoints Skill Settings ---------------- */
    @ConfigItem(keyName = "hitpointsDisplay", name = "Hitpoints", description = "Hitpoints Orb Display Settings", position = 63, section = orbMode)
    default SkillDisplayMode hitpointsDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "hitpointsPriority", name = "Hitpoints Priority", description = "Priority for Hitpoints orb (1-23)", position = 64, section = orbPriority)
    @Range(min = 1, max = 23)
    default int hitpointsPriority() { return 9; }

    /* ---------------- 13. Hunter Skill Settings ---------------- */
    @ConfigItem(keyName = "hunterDisplay", name = "Hunter", description = "Hunter Orb Display Settings", position = 65, section = orbMode)
    default SkillDisplayMode hunterDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "hunterPriority", name = "Hunter Priority", description = "Priority for Hunter orb (1-23)", position = 66, section = orbPriority)
    @Range(min = 1, max = 23)
    default int hunterPriority() { return 16; }

    /* ---------------- 14. Magic Skill Settings ---------------- */
    @ConfigItem(keyName = "magicDisplay", name = "Magic", description = "Magic Orb Display Settings", position = 67, section = orbMode)
    default SkillDisplayMode magicDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "magicPriority", name = "Magic Priority", description = "Priority for Magic orb (1-23)", position = 68, section = orbPriority)
    @Range(min = 1, max = 23)
    default int magicPriority() { return 6; }

    /* ---------------- 15. Mining Skill Settings ---------------- */
    @ConfigItem(keyName = "miningDisplay", name = "Mining", description = "Mining Orb Display Settings", position = 69, section = orbMode)
    default SkillDisplayMode miningDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "miningPriority", name = "Mining Priority", description = "Priority for Mining orb (1-23)", position = 70, section = orbPriority)
    @Range(min = 1, max = 23)
    default int miningPriority() { return 17; }

    /* ---------------- 16. Prayer Skill Settings ---------------- */
    @ConfigItem(keyName = "prayerDisplay", name = "Prayer", description = "Prayer Orb Display Settings", position = 71, section = orbMode)
    default SkillDisplayMode prayerDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "prayerPriority", name = "Prayer Priority", description = "Priority for Prayer orb (1-23)", position = 72, section = orbPriority)
    @Range(min = 1, max = 23)
    default int prayerPriority() { return 5; }

    /* ---------------- 17. Ranged Skill Settings ---------------- */
    @ConfigItem(keyName = "rangedDisplay", name = "Ranged", description = "Ranged Orb Display Settings", position = 73, section = orbMode)
    default SkillDisplayMode rangedDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "rangedPriority", name = "Ranged Priority", description = "Priority for Ranged orb (1-23)", position = 74, section = orbPriority)
    @Range(min = 1, max = 23)
    default int rangedPriority() { return 4; }

    /* ---------------- 18. Runecraft Skill Settings ---------------- */
    @ConfigItem(keyName = "runecraftDisplay", name = "Runecraft", description = "Runecraft Orb Display Settings", position = 75, section = orbMode)
    default SkillDisplayMode runecraftDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "runecraftPriority", name = "Runecraft Priority", description = "Priority for Runecraft orb (1-23)", position = 76, section = orbPriority)
    @Range(min = 1, max = 23)
    default int runecraftPriority() { return 7; }

    /* ---------------- 19. Slayer Skill Settings ---------------- */
    @ConfigItem(keyName = "slayerDisplay", name = "Slayer", description = "Slayer Orb Display Settings", position = 77, section = orbMode)
    default SkillDisplayMode slayerDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "slayerPriority", name = "Slayer Priority", description = "Priority for Slayer orb (1-23)", position = 78, section = orbPriority)
    @Range(min = 1, max = 23)
    default int slayerPriority() { return 15; }

    /* ---------------- 20. Smithing Skill Settings ---------------- */
    @ConfigItem(keyName = "smithingDisplay", name = "Smithing", description = "Smithing Orb Display Settings", position = 79, section = orbMode)
    default SkillDisplayMode smithingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "smithingPriority", name = "Smithing Priority", description = "Priority for Smithing orb (1-23)", position = 80, section = orbPriority)
    @Range(min = 1, max = 23)
    default int smithingPriority() { return 18; }

    /* ---------------- 21. Strength Skill Settings ---------------- */
    @ConfigItem(keyName = "strengthDisplay", name = "Strength", description = "Strength Orb Display Settings", position = 81, section = orbMode)
    default SkillDisplayMode strengthDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "strengthPriority", name = "Strength Priority", description = "Priority for Strength orb (1-23)", position = 82, section = orbPriority)
    @Range(min = 1, max = 23)
    default int strengthPriority() { return 2; }

    /* ---------------- 22. Thieving Skill Settings ---------------- */
    @ConfigItem(keyName = "thievingDisplay", name = "Thieving", description = "Thieving Orb Display Settings", position = 83, section = orbMode)
    default SkillDisplayMode thievingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "thievingPriority", name = "Thieving Priority", description = "Priority for Thieving orb (1-23)", position = 84, section = orbPriority)
    @Range(min = 1, max = 23)
    default int thievingPriority() { return 12; }

    /* ---------------- 23. Woodcutting Skill Settings ---------------- */
    @ConfigItem(keyName = "woodcuttingDisplay", name = "Woodcutting", description = "Woodcutting Orb Display Settings", position = 85, section = orbMode)
    default SkillDisplayMode woodcuttingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "woodcuttingPriority", name = "Woodcutting Priority", description = "Priority for Woodcutting orb (1-23)", position = 86, section = orbPriority)
    @Range(min = 1, max = 23)
    default int woodcuttingPriority() { return 22; }

}
