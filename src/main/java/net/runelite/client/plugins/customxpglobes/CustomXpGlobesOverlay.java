package net.runelite.client.plugins.customxpglobes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.SkillColor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;

public class CustomXpGlobesOverlay extends Overlay
{
    private static final int MINIMUM_STEP = 10;
    private static final int PROGRESS_RADIUS_START = 90;
    private static final int PROGRESS_RADIUS_REMAINDER = 0;
    private static final int PROGRESS_BACKGROUND_SIZE = 5;
    private static final int TOOLTIP_RECT_SIZE_X = 150;
    private static final Color DARK_OVERLAY_COLOR = new Color(0, 0, 0, 180);
    private static final String FLIP_ACTION = "Flip";
    private static final double GLOBE_ICON_RATIO = 0.65;

    private final Client client;
    private final CustomXpGlobesPlugin plugin;
    private final CustomXpGlobesConfig config;
    private final XpTrackerService xpTrackerService;
    private final TooltipManager tooltipManager;
    private final SkillIconManager iconManager;
    private final Tooltip xpTooltip = new Tooltip(new PanelComponent());

    @Inject
    private CustomXpGlobesOverlay(
            Client client,
            CustomXpGlobesPlugin plugin,
            CustomXpGlobesConfig config,
            XpTrackerService xpTrackerService,
            SkillIconManager iconManager,
            TooltipManager tooltipManager)
    {
        super(plugin);
        this.iconManager = iconManager;
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.xpTrackerService = xpTrackerService;
        this.tooltipManager = tooltipManager;
        this.xpTooltip.getComponent().setPreferredSize(new Dimension(TOOLTIP_RECT_SIZE_X, 0));
        // setPosition(OverlayPosition.TOP_CENTER);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        final Instant now = Instant.now();

        // Filter out expired NORMAL-mode or BLACKLIST orbs
        final List<CustomXpGlobe> xpGlobes = plugin.getXpGlobes().stream()
                .filter(globe -> {
                    CustomXpGlobesPlugin.SkillDisplayMode mode = plugin.getSkillMode(globe.getSkill());
                    return mode != CustomXpGlobesPlugin.SkillDisplayMode.BLACKLIST &&
                            (mode != CustomXpGlobesPlugin.SkillDisplayMode.NORMAL ||
                                    globe.getTime().plusSeconds(config.xpOrbDuration()).isAfter(now));
                })
                .collect(Collectors.toList());

        final int queueSize = xpGlobes.size();
        if (queueSize == 0)
            return null;

        final int progressArcOffset = (int) Math.ceil(Math.max(PROGRESS_BACKGROUND_SIZE, config.progressArcStrokeWidth()) / 2.0);
        final int maxPerLine = config.orbsPerLine();
        int curDrawPosition = progressArcOffset;
        int lineOffset = 0;
        int countInLine = 0;

        for (final CustomXpGlobe xpGlobe : xpGlobes)
        {
            int startXp = xpTrackerService.getStartGoalXp(xpGlobe.getSkill());
            int goalXp = xpTrackerService.getEndGoalXp(xpGlobe.getSkill());

            if (config.alignOrbsVertically())
            {
                renderProgressCircle(graphics, xpGlobe, startXp, goalXp,
                        progressArcOffset + lineOffset, curDrawPosition, getBounds());
            }
            else
            {
                renderProgressCircle(graphics, xpGlobe, startXp, goalXp,
                        curDrawPosition, progressArcOffset + lineOffset, getBounds());
            }

            curDrawPosition += MINIMUM_STEP + config.xpOrbSize();
            countInLine++;

            if (countInLine >= maxPerLine)
            {
                countInLine = 0;
                lineOffset += MINIMUM_STEP + config.xpOrbSize();
                curDrawPosition = progressArcOffset;
            }
        }

        final int numLines = (int) Math.ceil((double) queueSize / maxPerLine);
        if (config.alignOrbsVertically())
        {
            return new Dimension(config.xpOrbSize() + progressArcOffset * 2 + lineOffset,
                    (config.xpOrbSize() + MINIMUM_STEP) * numLines);
        }
        else
        {
            return new Dimension((config.xpOrbSize() + MINIMUM_STEP) * numLines,
                    config.xpOrbSize() + progressArcOffset * 2 + lineOffset);
        }
    }



    private double getSkillProgress(int startXp, int currentXp, int goalXp)
    {
        double xpGained = currentXp - startXp;
        double xpGoal = goalXp - startXp;

        return ((xpGained / xpGoal) * 100);
    }

    private double getSkillProgressRadius(int startXp, int currentXp, int goalXp)
    {
        return -(3.6 * getSkillProgress(startXp, currentXp, goalXp)); //arc goes backwards
    }

    private void renderProgressCircle(Graphics2D graphics, CustomXpGlobe skillToDraw, int startXp, int goalXp, int x, int y, Rectangle bounds)
    {
        double radiusCurrentXp = getSkillProgressRadius(startXp, skillToDraw.getCurrentXp(), goalXp);
        double radiusToGoalXp = 360; //draw a circle

        Ellipse2D backgroundCircle = drawEllipse(graphics, x, y);

        // draw orb icon
        drawSkillImage(graphics, skillToDraw, x, y);

        // draw skill level
        drawSkillLevel(graphics, skillToDraw, x, y);

        Point mouse = client.getMouseCanvasPosition();
        int mouseX = mouse.getX() - bounds.x;
        int mouseY = mouse.getY() - bounds.y;

        // If mouse is hovering the globe
        if (backgroundCircle.contains(mouseX, mouseY))
        {
            // Fill a darker overlay circle
            graphics.setColor(DARK_OVERLAY_COLOR);
            graphics.fill(backgroundCircle);

            drawProgressLabel(graphics, skillToDraw, startXp, goalXp, x, y);

            if (config.enableTooltips())
            {
                drawTooltip(skillToDraw, goalXp);
            }
        }

        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        drawProgressArc(
                graphics,
                x, y,
                config.xpOrbSize(), config.xpOrbSize(),
                PROGRESS_RADIUS_REMAINDER, radiusToGoalXp,
                PROGRESS_BACKGROUND_SIZE,
                config.progressOrbOutLineColor()
        );
        drawProgressArc(
                graphics,
                x, y,
                config.xpOrbSize(), config.xpOrbSize(),
                PROGRESS_RADIUS_START, radiusCurrentXp,
                config.progressArcStrokeWidth(),
                config.enableCustomArcColor() ? config.progressArcColor() : SkillColor.find(skillToDraw.getSkill()).getColor());
    }

    private void drawProgressLabel(Graphics2D graphics, CustomXpGlobe globe, int startXp, int goalXp, int x, int y)
    {
        if (goalXp <= globe.getCurrentXp())
        {
            return;
        }

        // Convert to int just to limit the decimal cases
        String progress = (int) (getSkillProgress(startXp, globe.getCurrentXp(), goalXp)) + "%";

        final FontMetrics metrics = graphics.getFontMetrics();
        int drawX = x + (config.xpOrbSize() / 2) - (metrics.stringWidth(progress) / 2);
        int drawY = y + (config.xpOrbSize() / 2) + (metrics.getHeight() / 2);

        OverlayUtil.renderTextLocation(graphics, new Point(drawX, drawY), progress, Color.WHITE);
    }

    private void drawProgressArc(Graphics2D graphics, int x, int y, int w, int h, double radiusStart, double radiusEnd, int strokeWidth, Color color)
    {
        Stroke stroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics.setColor(color);
        graphics.draw(new Arc2D.Double(
                x, y,
                w, h,
                radiusStart, radiusEnd,
                Arc2D.OPEN));
        graphics.setStroke(stroke);
    }

    private Ellipse2D drawEllipse(Graphics2D graphics, int x, int y)
    {
        graphics.setColor(config.progressOrbBackgroundColor());
        Ellipse2D ellipse = new Ellipse2D.Double(x, y, config.xpOrbSize(), config.xpOrbSize());
        graphics.fill(ellipse);
        graphics.draw(ellipse);
        return ellipse;
    }

    private void drawSkillImage(Graphics2D graphics, CustomXpGlobe xpGlobe, int x, int y)
    {
        final int orbSize = config.xpOrbSize();
        final BufferedImage skillImage = getScaledSkillIcon(xpGlobe, orbSize);

        if (skillImage == null)
        {
            return;
        }

        // Shift orb icon up by 5 pixels if skill level display is enabled
        int yOffset = config.showSkillLevel() ? -5 : 0;

        // Draw the skill icon (centered in the orb)
        graphics.drawImage(
                skillImage,
                x + (orbSize / 2) - (skillImage.getWidth() / 2),
                y + (orbSize / 2) - (skillImage.getHeight() / 2) + yOffset,
                null
        );
    }

    private void drawSkillLevel(Graphics2D graphics, CustomXpGlobe xpGlobe, int x, int y)
    {
        if (!config.showSkillLevel())
        {
            return; // do nothing if disabled
        }

        final int orbSize = config.xpOrbSize();
        String skillLevel = String.valueOf(xpGlobe.getCurrentLevel());

        int fontSize = Math.max(12, orbSize / 3);
        graphics.setFont(new Font("Tahoma", Font.PLAIN, fontSize));

        FontMetrics fm = graphics.getFontMetrics();
        int textWidth = fm.stringWidth(skillLevel);

        // Center horizontally
        int textX = x + (orbSize / 2) - (textWidth / 2);

        // Bottom inside orb
        int padding = 4;
        int textY = y + orbSize - padding;

        Color levelColor = config.enableSkillLevelColour()
                ? SkillColor.find(xpGlobe.getSkill()).getColor()
                : Color.YELLOW;

        if (config.enableSkillLevelColour())
        {
            // Draw black outline around text (4 directions)
            graphics.setColor(Color.BLACK);
            graphics.drawString(skillLevel, textX - 1, textY - 1);
            graphics.drawString(skillLevel, textX - 1, textY + 1);
            graphics.drawString(skillLevel, textX + 1, textY - 1);
            graphics.drawString(skillLevel, textX + 1, textY + 1);
        }

        // Draw main text
        graphics.setColor(levelColor);
        graphics.drawString(skillLevel, textX, textY);
    }



    private BufferedImage getScaledSkillIcon(CustomXpGlobe xpGlobe, int orbSize)
    {
        // Cache the previous icon if the size hasn't changed
        if (xpGlobe.getSkillIcon() != null && xpGlobe.getSize() == orbSize)
        {
            return xpGlobe.getSkillIcon();
        }

        BufferedImage icon = iconManager.getSkillImage(xpGlobe.getSkill());
        if (icon == null)
        {
            return null;
        }

        final int size = orbSize - config.progressArcStrokeWidth();
        final int scaledIconSize = (int) (size * GLOBE_ICON_RATIO);
        if (scaledIconSize <= 0)
        {
            return null;
        }

        icon = ImageUtil.resizeImage(icon, scaledIconSize, scaledIconSize, true);

        xpGlobe.setSkillIcon(icon);
        xpGlobe.setSize(orbSize);
        return icon;
    }

    private void drawTooltip(CustomXpGlobe mouseOverSkill, int goalXp)
    {
        // reset the timer on XpGlobe to prevent it from disappearing while hovered over it
        mouseOverSkill.setTime(Instant.now());

        String skillName = mouseOverSkill.getSkill().getName();
        String skillLevel = Integer.toString(mouseOverSkill.getCurrentLevel());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        String skillCurrentXp = decimalFormat.format(mouseOverSkill.getCurrentXp());

        final PanelComponent xpTooltip = (PanelComponent) this.xpTooltip.getComponent();
        xpTooltip.getChildren().clear();

        xpTooltip.getChildren().add(LineComponent.builder()
                .left(skillName)
                .right(skillLevel)
                .build());

        xpTooltip.getChildren().add(LineComponent.builder()
                .left("Current XP:")
                .leftColor(Color.ORANGE)
                .right(skillCurrentXp)
                .build());

        if (goalXp > mouseOverSkill.getCurrentXp())
        {
            if (config.showActionsLeft())
            {
                int actionsLeft = xpTrackerService.getActionsLeft(mouseOverSkill.getSkill());
                if (actionsLeft != Integer.MAX_VALUE)
                {
                    String actionsLeftString = decimalFormat.format(actionsLeft);
                    xpTooltip.getChildren().add(LineComponent.builder()
                            .left("Actions left:")
                            .leftColor(Color.ORANGE)
                            .right(actionsLeftString)
                            .build());
                }
            }

            if (config.showXpLeft())
            {
                int xpLeft = goalXp - mouseOverSkill.getCurrentXp();
                String skillXpToLvl = decimalFormat.format(xpLeft);
                xpTooltip.getChildren().add(LineComponent.builder()
                        .left("XP left:")
                        .leftColor(Color.ORANGE)
                        .right(skillXpToLvl)
                        .build());
            }

            if (config.showXpHour())
            {
                int xpHr = xpTrackerService.getXpHr(mouseOverSkill.getSkill());
                if (xpHr != 0)
                {
                    String xpHrString = decimalFormat.format(xpHr);
                    xpTooltip.getChildren().add(LineComponent.builder()
                            .left("XP per hour:")
                            .leftColor(Color.ORANGE)
                            .right(xpHrString)
                            .build());
                }
            }

            if (config.showTimeTilGoal())
            {
                String timeLeft = xpTrackerService.getTimeTilGoal(mouseOverSkill.getSkill());
                xpTooltip.getChildren().add(LineComponent.builder()
                        .left("Time left:")
                        .leftColor(Color.ORANGE)
                        .right(timeLeft)
                        .build());
            }
        }

        tooltipManager.add(this.xpTooltip);
    }
}
