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
            keyName = "showSkillLevel",
            name = "Level Display",
            description = "Enables the level value display inside the orb",
            position = 3,
            section = onScreenOrbs
    )
    default boolean showSkillLevel() {
        return false;
    }

    @ConfigItem(
            keyName = "enableSkillLevelColour",
            name = "Level Color Match",
            description = "Matches The Display Level To The Skill Colour",
            position = 4,
            section = onScreenOrbs
    )
    default boolean enableSkillLevelColour() {
        return false;
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

    enum MaxedSkillDisplay {
        NORMAL,           // Show all skills including 99
        HIDE_MAXED,       // Hide maxed skills
        SHOW_VIRTUAL      // Show virtual levels over 99
    }

    @ConfigItem(
            keyName = "maxedSkillDisplay",
            name = "Maxed Skill Display",
            description = "Controls how maxed skills are displayed in XP globes",
            position = 6,
            section = onScreenOrbs
    )
    default MaxedSkillDisplay maxedSkillDisplay() {
        return MaxedSkillDisplay.NORMAL;
    }

    @ConfigItem(
            keyName = "Orb duration",
            name = "Duration of orbs",
            description = "Change the duration the XP orbs are visible.",
            position = 7,
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
            position = 10,
            closedByDefault = true
    )
    String customizeOrbs = "Customize Orbs";

    @ConfigItem(
            keyName = "enableCustomArcColor",
            name = "Enable custom arc color",
            description = "Enables the custom coloring of the globe's arc instead of using the skill's default color.",
            position = 11,
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
            position = 12,
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
            position = 13,
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
            position = 14,
            section = customizeOrbs
    )
    default Color progressOrbBackgroundColor() {
        return new Color(128, 128, 128, 127);
    }

    @ConfigItem(
            keyName = "Progress arc width",
            name = "Progress arc width",
            description = "Change the stroke width of the progress arc.",
            position = 15,
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
            position = 16,
            section = customizeOrbs
    )
    @Units(Units.PIXELS)
    default int xpOrbSize() {
        return 40;
    }

    /* ---------------- Orb Tooltips ---------------- */
    @ConfigSection(
            name = "Orb Tooltips",
            description = "Settings for Orb Tooltips",
            position = 20,
            closedByDefault = true
    )
    String orbTooltips = "Orb Tooltips";

    @ConfigItem(
            keyName = "enableTooltips",
            name = "Enable tooltips",
            description = "Configures whether or not to show tooltips.",
            position = 21,
            section = orbTooltips
    )
    default boolean enableTooltips() {
        return true;
    }  // Tooltips 1

    @ConfigItem(
            keyName = "showXpLeft",
            name = "Show XP left",
            description = "Shows XP left inside the globe tooltip box.",
            position = 22,
            section = orbTooltips
    )
    default boolean showXpLeft() {
        return true;
    }

    @ConfigItem(
            keyName = "showActionsLeft",
            name = "Show actions left",
            description = "Shows the number of actions left inside the globe tooltip box.",
            position = 23,
            section = orbTooltips
    )
    default boolean showActionsLeft() {
        return true;
    }

    @ConfigItem(
            keyName = "showXpHour",
            name = "Show XP/hr",
            description = "Shows XP per hour inside the globe tooltip box.",
            position = 24,
            section = orbTooltips
    )
    default boolean showXpHour() {
        return true;
    }

    @ConfigItem(
            keyName = "showTimeTilGoal",
            name = "Show time til goal",
            description = "Shows the amount of time until goal level in the globe tooltip box.",
            position = 25,
            section = orbTooltips
    )
    default boolean showTimeTilGoal() {
        return true;
    }

    /* ---------------- Per Orb Display Settings ---------------- */
    @ConfigSection(
            name = "Per Orb Display Settings",
            description = "Switch between Normal, Forced & Blacklist Modes",
            position = 40,
            closedByDefault = true
    )
    String orbSection = "Per Orb Display Settings";

    enum SkillDisplayMode
    {
        NORMAL,   // Display XP globe normally (default behavior)
        FORCE,    // Always display XP globe regardless of XP gains
        BLACKLIST // Never display XP globe
    }

    @ConfigItem(keyName = "agilityDisplay", name = "Agility", description = "Display mode for Agility XP globe", position = 41, section = orbSection)
    default SkillDisplayMode agilityDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "attackDisplay", name = "Attack", description = "Display mode for Attack XP globe", position = 42, section = orbSection)
    default SkillDisplayMode attackDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "constructionDisplay", name = "Construction", description = "Display mode for Construction XP globe", position = 43, section = orbSection)
    default SkillDisplayMode constructionDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "cookingDisplay", name = "Cooking", description = "Display mode for Cooking XP globe", position = 44, section = orbSection)
    default SkillDisplayMode cookingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "craftingDisplay", name = "Crafting", description = "Display mode for Crafting XP globe", position = 45, section = orbSection)
    default SkillDisplayMode craftingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "defenceDisplay", name = "Defense", description = "Display mode for Defense XP globe", position = 46, section = orbSection)
    default SkillDisplayMode defenceDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "farmingDisplay", name = "Farming", description = "Display mode for Farming XP globe", position = 47, section = orbSection)
    default SkillDisplayMode farmingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "firemakingDisplay", name = "Firemaking", description = "Display mode for Firemaking XP globe", position = 48, section = orbSection)
    default SkillDisplayMode firemakingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "fletchingDisplay", name = "Fletching", description = "Display mode for Fletching XP globe", position = 49, section = orbSection)
    default SkillDisplayMode fletchingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "fishingDisplay", name = "Fishing", description = "Display mode for Fishing XP globe", position = 50, section = orbSection)
    default SkillDisplayMode fishingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "herbloreDisplay", name = "Herblore", description = "Display mode for Herblore XP globe", position = 51, section = orbSection)
    default SkillDisplayMode herbloreDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "hitpointsDisplay", name = "Hitpoints", description = "Display mode for Hitpoints XP globe", position = 52, section = orbSection)
    default SkillDisplayMode hitpointsDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "hunterDisplay", name = "Hunter", description = "Display mode for Hunter XP globe", position = 53, section = orbSection)
    default SkillDisplayMode hunterDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "magicDisplay", name = "Magic", description = "Display mode for Magic XP globe", position = 54, section = orbSection)
    default SkillDisplayMode magicDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "miningDisplay", name = "Mining", description = "Display mode for Mining XP globe", position = 55, section = orbSection)
    default SkillDisplayMode miningDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "prayerDisplay", name = "Prayer", description = "Display mode for Prayer XP globe", position = 56, section = orbSection)
    default SkillDisplayMode prayerDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "rangedDisplay", name = "Ranged", description = "Display mode for Ranged XP globe", position = 57, section = orbSection)
    default SkillDisplayMode rangedDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "runecraftDisplay", name = "Runecraft", description = "Display mode for Runecraft XP globe", position = 58, section = orbSection)
    default SkillDisplayMode runecraftDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "slayerDisplay", name = "Slayer", description = "Display mode for Slayer XP globe", position = 59, section = orbSection)
    default SkillDisplayMode slayerDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "smithingDisplay", name = "Smithing", description = "Display mode for Smithing XP globe", position = 60, section = orbSection)
    default SkillDisplayMode smithingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "strengthDisplay", name = "Strength", description = "Display mode for Strength XP globe", position = 61, section = orbSection)
    default SkillDisplayMode strengthDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "thievingDisplay", name = "Thieving", description = "Display mode for Thieving XP globe", position = 62, section = orbSection)
    default SkillDisplayMode thievingDisplay() { return SkillDisplayMode.NORMAL; }

    @ConfigItem(keyName = "woodcuttingDisplay", name = "Woodcutting", description = "Display mode for Woodcutting XP globe", position = 63, section = orbSection)
    default SkillDisplayMode woodcuttingDisplay() { return SkillDisplayMode.NORMAL; }


}