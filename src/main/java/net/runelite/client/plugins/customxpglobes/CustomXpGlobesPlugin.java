package net.runelite.client.plugins.customxpglobes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Custom XP Globes",
        description = "Runtime XP globes overlay with FORCE/BLACKLIST modes",
        tags = {"experience", "levels", "overlay", "custom"}
)
@PluginDependency(XpTrackerPlugin.class)
public class CustomXpGlobesPlugin extends Plugin
{
    private CustomXpGlobe[] globeCache = new CustomXpGlobe[Skill.values().length];

    private boolean firstMovementDetected = false;
    private int lastX = -1;
    private int lastY = -1;

    @Getter
    private final List<CustomXpGlobe> xpGlobes = new ArrayList<>();

    @Inject
    private CustomXpGlobesConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CustomXpGlobesOverlay overlay;

    @Inject
    private Client client;

    @Provides
    CustomXpGlobesConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CustomXpGlobesConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        xpGlobes.clear();
        loadOrRefreshForcedSkills();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        resetGlobeState();
    }

    /**
     * Returns the user-selected priority for a given skill.
     */
    private int getSkillPriority(Skill skill)
    {
        switch (skill)
        {
            case ATTACK: return config.attackPriority();
            case STRENGTH: return config.strengthPriority();
            case DEFENCE: return config.defencePriority();
            case RANGED: return config.rangedPriority();
            case PRAYER: return config.prayerPriority();
            case MAGIC: return config.magicPriority();
            case RUNECRAFT: return config.runecraftPriority();
            case CONSTRUCTION: return config.constructionPriority();
            case HITPOINTS: return config.hitpointsPriority();
            case AGILITY: return config.agilityPriority();
            case HERBLORE: return config.herblorePriority();
            case THIEVING: return config.thievingPriority();
            case CRAFTING: return config.craftingPriority();
            case FLETCHING: return config.fletchingPriority();
            case SLAYER: return config.slayerPriority();
            case HUNTER: return config.hunterPriority();
            case MINING: return config.miningPriority();
            case SMITHING: return config.smithingPriority();
            case FISHING: return config.fishingPriority();
            case COOKING: return config.cookingPriority();
            case FIREMAKING: return config.firemakingPriority();
            case WOODCUTTING: return config.woodcuttingPriority();
            case FARMING: return config.farmingPriority();
            default: return skill.ordinal(); // fallback
        }
    }

    public enum SkillDisplayMode { NORMAL, FORCE, BLACKLIST }

    public SkillDisplayMode getSkillMode(Skill skill)
    {
        CustomXpGlobesConfig.SkillDisplayMode cfgMode;
        switch (skill)
        {
            case ATTACK: cfgMode = config.attackDisplay(); break;
            case STRENGTH: cfgMode = config.strengthDisplay(); break;
            case DEFENCE: cfgMode = config.defenceDisplay(); break;
            case RANGED: cfgMode = config.rangedDisplay(); break;
            case PRAYER: cfgMode = config.prayerDisplay(); break;
            case MAGIC: cfgMode = config.magicDisplay(); break;
            case RUNECRAFT: cfgMode = config.runecraftDisplay(); break;
            case CONSTRUCTION: cfgMode = config.constructionDisplay(); break;
            case AGILITY: cfgMode = config.agilityDisplay(); break;
            case HERBLORE: cfgMode = config.herbloreDisplay(); break;
            case HITPOINTS: cfgMode = config.hitpointsDisplay(); break;
            case THIEVING: cfgMode = config.thievingDisplay(); break;
            case CRAFTING: cfgMode = config.craftingDisplay(); break;
            case FLETCHING: cfgMode = config.fletchingDisplay(); break;
            case SLAYER: cfgMode = config.slayerDisplay(); break;
            case HUNTER: cfgMode = config.hunterDisplay(); break;
            case MINING: cfgMode = config.miningDisplay(); break;
            case SMITHING: cfgMode = config.smithingDisplay(); break;
            case FISHING: cfgMode = config.fishingDisplay(); break;
            case COOKING: cfgMode = config.cookingDisplay(); break;
            case FIREMAKING: cfgMode = config.firemakingDisplay(); break;
            case WOODCUTTING: cfgMode = config.woodcuttingDisplay(); break;
            case FARMING: cfgMode = config.farmingDisplay(); break;
            default: cfgMode = CustomXpGlobesConfig.SkillDisplayMode.NORMAL; break;
        }

        switch (cfgMode)
        {
            case FORCE: return SkillDisplayMode.FORCE;
            case BLACKLIST: return SkillDisplayMode.BLACKLIST;
            case NORMAL:
            default: return SkillDisplayMode.NORMAL;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (!firstMovementDetected)
        {
            if (client.getLocalPlayer() != null)
            {
                int x = client.getLocalPlayer().getWorldLocation().getX();
                int y = client.getLocalPlayer().getWorldLocation().getY();

                if (lastX != -1 && lastY != -1 && (x != lastX || y != lastY))
                {
                    firstMovementDetected = true;
                }

                lastX = x;
                lastY = y;
            }
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        Skill skill = statChanged.getSkill();
        int currentXp = statChanged.getXp();
        int currentLevel = statChanged.getLevel();
        int skillIdx = skill.ordinal();

        SkillDisplayMode mode = getSkillMode(skill);
        CustomXpGlobe cachedGlobe = globeCache[skillIdx];
        boolean isForce = mode == SkillDisplayMode.FORCE;

        if (mode == SkillDisplayMode.BLACKLIST)
        {
            return;
        }

        if (mode == SkillDisplayMode.NORMAL && !firstMovementDetected)
        {
            return;
        }

        if (mode == SkillDisplayMode.NORMAL)
        {
            int previousXp = cachedGlobe != null ? cachedGlobe.getCurrentXp() : 0;
            if (currentXp <= previousXp)
            {
                return;
            }
        }

        switch (config.maxedSkillDisplay())
        {
            case HIDE_MAXED:
                if (currentLevel >= Experience.MAX_REAL_LEVEL && !isForce)
                {
                    return;
                }
                break;
            case SHOW_VIRTUAL:
                if (currentLevel >= Experience.MAX_REAL_LEVEL)
                {
                    currentLevel = Experience.getLevelForXp(currentXp);
                }
                break;
            default:
                break;
        }

        if (cachedGlobe != null)
        {
            cachedGlobe.setCurrentXp(currentXp);
            cachedGlobe.setCurrentLevel(currentLevel);
            cachedGlobe.setTime(Instant.now());

            if (!xpGlobes.contains(cachedGlobe))
                addXpGlobe(cachedGlobe, isForce);
        }
        else if (isForce || (mode == SkillDisplayMode.NORMAL && firstMovementDetected))
        {
            cachedGlobe = new CustomXpGlobe(skill, currentXp, currentLevel, Instant.now());
            globeCache[skillIdx] = cachedGlobe;
            addXpGlobe(cachedGlobe, isForce);
        }
    }

    private void sortAndEnforceOrbs()
    {
        // Recalculate priorities for all globes
        xpGlobes.forEach(globe -> globe.setCachedPriority(getSkillPriority(globe.getSkill())));

        // Sort: forced orbs first, then by cached priority
        xpGlobes.sort((a, b) ->
        {
            SkillDisplayMode aMode = getSkillMode(a.getSkill());
            SkillDisplayMode bMode = getSkillMode(b.getSkill());

            if (config.forceOrbs())
            {
                if (aMode == SkillDisplayMode.FORCE && bMode != SkillDisplayMode.FORCE) return -1;
                if (aMode != SkillDisplayMode.FORCE && bMode == SkillDisplayMode.FORCE) return 1;
            }

            return Integer.compare(a.getCachedPriority(), b.getCachedPriority());
        });

        // Enforce maximum shown orbs
        enforceMaximumOrbs();
    }

    private void addXpGlobe(CustomXpGlobe globe, boolean ignoreMax)
    {
        globe.setCachedPriority(getSkillPriority(globe.getSkill()));
        xpGlobes.add(globe);

        if (!ignoreMax)
        {
            sortAndEnforceOrbs();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("customxpglobes"))
            return;

        loadOrRefreshForcedSkills();

        sortAndEnforceOrbs();
    }

    /**
     * Ensures that the total number of normal orbs does not exceed the allowed maximum,
     * taking into account the number of forced orbs.
     */
    private void enforceMaximumOrbs()
    {
        int forcedCount = (int) xpGlobes.stream()
                .filter(g -> getSkillMode(g.getSkill()) == SkillDisplayMode.FORCE)
                .count();

        int maxNormal = Math.max(config.maximumShownGlobes() - forcedCount, 0);

        // Collect normal orbs sorted oldest first
        List<CustomXpGlobe> normalOrbs = xpGlobes.stream()
                .filter(g -> getSkillMode(g.getSkill()) != SkillDisplayMode.FORCE)
                .sorted(Comparator.comparing(CustomXpGlobe::getTime))
                .collect(Collectors.toList());

        // Remove the oldest normal orbs until the count fits the limit
        while (normalOrbs.size() > maxNormal)
        {
            CustomXpGlobe oldest = normalOrbs.remove(0);
            xpGlobes.remove(oldest);
            globeCache[oldest.getSkill().ordinal()] = null;
        }
    }

    @Schedule(period = 1, unit = ChronoUnit.SECONDS)
    public void removeExpiredXpGlobes()
    {
        Instant expire = Instant.now().minusSeconds(config.xpOrbDuration());
        xpGlobes.removeIf(globe -> {
            boolean expired = getSkillMode(globe.getSkill()) != SkillDisplayMode.FORCE &&
                    globe.getTime().isBefore(expire);
            if (expired)
            {
                globeCache[globe.getSkill().ordinal()] = null;
            }
            return expired;
        });
    }

    private void resetGlobeState()
    {
        xpGlobes.clear();
        globeCache = new CustomXpGlobe[Skill.values().length];
        firstMovementDetected = false; // reset movement detection
        lastX = -1;
        lastY = -1;
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case LOGGED_IN:
            case HOPPING:
                resetGlobeState();
                loadOrRefreshForcedSkills(); // forced orbs always loaded
                break;

            case LOGIN_SCREEN:
            case STARTING:
            case CONNECTION_LOST:
                resetGlobeState(); // clear everything, no orbs
                break;

            default:
                break;
        }
    }

    private void loadOrRefreshForcedSkills()
    {
        for (Skill skill : Skill.values())
        {
            int idx = skill.ordinal();
            SkillDisplayMode mode = getSkillMode(skill);

            if (mode == SkillDisplayMode.FORCE && globeCache[idx] == null)
            {
                int xp = client.getSkillExperience(skill);
                int level = client.getRealSkillLevel(skill);

                CustomXpGlobe globe = new CustomXpGlobe(skill, xp, level, Instant.now());
                globeCache[idx] = globe;
                addXpGlobe(globe, true); // ignoreMax = true for forced orbs
            }
        }
    }

}

