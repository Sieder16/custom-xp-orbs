package net.runelite.client.plugins.customxpglobes;

import com.google.inject.Provides;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
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
        tags = {"experience", "levels", "overlay", "custom"},
        enabledByDefault = false
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
    private ConfigManager configManager;

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
        System.out.println("[CustomXpGlobes] [startUp] Plugin Startup Initiated");
        overlayManager.add(overlay);
        xpGlobes.clear();
        globeCache = new CustomXpGlobe[Skill.values().length];
        System.out.println("[CustomXpGlobes] Plugin started, waiting for LOGGED_IN to process skills.");
    }

    @Override
    protected void shutDown()
    {
        System.out.println("[CustomXpGlobes] [shutDown] Plugin Shutdown Initiated");
        overlayManager.remove(overlay);
        resetGlobes();
    }

    private void resetGlobes()
    {
        xpGlobes.clear();
        globeCache = new CustomXpGlobe[Skill.values().length];
        System.out.println("[CustomXpOrbs] [resetGlobeState] All XP globes have been reset/cleared.");
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
                    System.out.println("[CustomXpOrbs] First player movement detected, enabling NORMAL-mode globes.");
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

        System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 1] Received stat change: "
                + skill.getName() + " | XP=" + currentXp + " | Level=" + currentLevel + " | Mode=" + mode);

        if (mode == SkillDisplayMode.BLACKLIST)
        {
            System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 2] Skipping BLACKLIST-mode skill: " + skill.getName());
            return;
        }

        if (mode == SkillDisplayMode.NORMAL && !firstMovementDetected)
        {
            System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 3] Skipping NORMAL-mode globe until first input: " + skill.getName());
            return;
        }

        if (mode == SkillDisplayMode.NORMAL)
        {
            int previousXp = cachedGlobe != null ? cachedGlobe.getCurrentXp() : 0;
            if (currentXp <= previousXp)
            {
                System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 4] Skipping NORMAL-mode globe, XP did not increase: "
                        + skill.getName() + " | XP=" + currentXp);
                return;
            }
        }

        switch (config.maxedSkillDisplay())
        {
            case HIDE_MAXED:
                if (currentLevel >= Experience.MAX_REAL_LEVEL && !isForce)
                {
                    System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 5] Maxed skill hidden: " + skill.getName());
                    return;
                }
                break;
            case SHOW_VIRTUAL:
                if (currentLevel >= Experience.MAX_REAL_LEVEL)
                {
                    currentLevel = Experience.getLevelForXp(currentXp);
                    System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 6] Maxed skill showing virtual level: "
                            + skill.getName() + " | Virtual Level=" + currentLevel);
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

            System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 7] Updated globe for "
                    + skill.getName() + ": XP=" + currentXp + ", Level=" + currentLevel);

            if (!xpGlobes.contains(cachedGlobe))
                addXpGlobe(cachedGlobe, isForce);
        }
        else if (isForce || (mode == SkillDisplayMode.NORMAL && firstMovementDetected))
        {
            cachedGlobe = new CustomXpGlobe(skill, currentXp, currentLevel, Instant.now());
            globeCache[skillIdx] = cachedGlobe;
            addXpGlobe(cachedGlobe, isForce);

            System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 8] Created new globe for "
                    + skill.getName() + ": XP=" + currentXp + ", Level=" + currentLevel);
        }
        else
        {
            System.out.println("[CustomXpOrbs] [onStatChanged] [Stage 9] Skipped creating globe for skill: " + skill.getName());
        }
    }

    private void addXpGlobe(CustomXpGlobe globe, boolean ignoreMax)
    {
        int idx = Collections.binarySearch(xpGlobes, globe, Comparator.comparing(CustomXpGlobe::getSkill));
        if (idx < 0)
            xpGlobes.add(-idx - 1, globe);

        if (!ignoreMax && xpGlobes.stream().filter(g -> getSkillMode(g.getSkill()) != SkillDisplayMode.FORCE).count() > config.maximumShownGlobes())
        {
            xpGlobes.stream()
                    .filter(g -> getSkillMode(g.getSkill()) != SkillDisplayMode.FORCE)
                    .min(Comparator.comparing(CustomXpGlobe::getTime))
                    .ifPresent(oldest -> {
                        xpGlobes.remove(oldest);
                        globeCache[oldest.getSkill().ordinal()] = null;

                        System.out.println("[CustomXpOrbs] [addXpGlobe] Removed oldest XP globe: "
                                + oldest.getSkill().getName());
                    });
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
                System.out.println("[CustomXpOrbs] [removeExpiredXpGlobes] [Only Stage] Removed expired XP globe: "
                        + globe.getSkill().getName()
                        + " | XP=" + globe.getCurrentXp()
                        + " | Level=" + globe.getCurrentLevel());
            }
            return expired;
        });
    }

    public void resetGlobeState()
    {
        xpGlobes.clear();
        globeCache = new CustomXpGlobe[Skill.values().length];
        System.out.println("[CustomXpOrbs] [resetGlobeState] [Only Stage] All XP globes have been reset/cleared.");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            loadForceSkillsAtStartup();
            boolean firstLogin = false;

            System.out.println("[CustomXpOrbs] [onGameStateChanged] [Stage 1]  LOGGED_IN processed, FORCE-mode loaded, firstLogin=false");
        }
        else if (event.getGameState() == GameState.LOGIN_SCREEN ||
                event.getGameState() == GameState.STARTING ||
                event.getGameState() == GameState.CONNECTION_LOST)
        {
            resetGlobeState();
            System.out.println("[CustomXpOrbs] [onGameStateChanged] [Stage 2] Cleared all orbs due to state: " + event.getGameState());
        }
    }

    private void loadForceSkillsAtStartup()
    {
        System.out.println("[CustomXpOrbs] [loadForceSkillsAtStartup][Stage 0] Loading FORCE-mode skills at startup...");

        for (Skill skill : Skill.values())
        {
            SkillDisplayMode mode = getSkillMode(skill);
            System.out.println("[CustomXpOrbs] [loadForceSkillsAtStartup] [Stage 1] Checking skill: "
                    + skill.getName() + " | Mode=" + mode);

            if (mode == SkillDisplayMode.FORCE)
            {
                int idx = skill.ordinal();
                if (globeCache[idx] == null)
                {
                    int xp = client.getSkillExperience(skill);
                    int level = client.getRealSkillLevel(skill);

                    CustomXpGlobe globe = new CustomXpGlobe(skill, xp, level, Instant.now());
                    globeCache[idx] = globe;
                    addXpGlobe(globe, true);

                    System.out.println("[CustomXpOrbs] [loadForceSkillsAtStartup] [Stage 2] Created new FORCE-mode globe for "
                            + skill.getName() + ": XP=" + xp + ", Level=" + level);
                }
                else
                {
                    System.out.println("[CustomXpOrbs] [loadForceSkillsAtStartup] [Stage 3] Globe already exists for FORCE-mode skill: "
                            + skill.getName());
                }
            }
            else
            {
                System.out.println("[CustomXpOrbs] [loadForceSkillsAtStartup] [Stage 4] Not a FORCE-mode skill, skipping: "
                        + skill.getName());
            }
        }

        System.out.println("[CustomXpOrbs] [loadForceSkillsAtStartup] [Stage 5] Finished loading FORCE-mode skills at startup.");
    }

    private void refreshForceSkills()
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
                addXpGlobe(globe, true);

                System.out.println("[CustomXpOrbs] [refreshForceSkills] Created new FORCE-mode globe for "
                        + skill.getName());
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("customxpglobes"))
            return;

        // When a skill mode is changed to FORCE
        refreshForceSkills();
    }
}
