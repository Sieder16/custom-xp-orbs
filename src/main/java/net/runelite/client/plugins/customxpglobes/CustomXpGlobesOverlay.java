package net.runelite.client.plugins.customxporbs;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.SkillColor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.plugins.customxporbs.CustomXpGlobesConfig.TooltipLine;


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

        // Filter active orbs
        List<CustomXpGlobe> xpGlobes = plugin.getXpGlobes().stream()
                .filter(globe -> {
                    CustomXpGlobesPlugin.SkillDisplayMode mode = plugin.getSkillMode(globe.getSkill());
                    return mode != CustomXpGlobesPlugin.SkillDisplayMode.BLACKLIST &&
                            (mode != CustomXpGlobesPlugin.SkillDisplayMode.NORMAL ||
                                    globe.getTime().plusSeconds(config.xpOrbDuration()).isAfter(now));
                })
                .collect(Collectors.toList());

        if (xpGlobes.isEmpty())
            return null;

        final int orbSize = config.xpOrbSize();
        final int strokeOffset = (int) Math.ceil(Math.max(PROGRESS_BACKGROUND_SIZE, config.progressArcStrokeWidth()) / 2.0);
        final int step = MINIMUM_STEP + orbSize;
        final int maxPerLine = config.orbsPerLine();

        int curX = strokeOffset;
        int curY = strokeOffset;
        int countInLine = 0;

        int maxX = 0;
        int maxY = 0;

        boolean vertical = config.alignOrbsVertically(); // new flag

        for (int i = 0; i < xpGlobes.size(); i++)
        {
            // Center last line/or column
            int lineIndex = vertical ? i / maxPerLine : i / maxPerLine;
            int lastLineCount = xpGlobes.size() % maxPerLine;
            if (lastLineCount == 0) lastLineCount = maxPerLine;

            if (countInLine == 0 && lastLineCount < maxPerLine && lineIndex == (xpGlobes.size() / maxPerLine))
            {
                if (vertical)
                {
                    curY += ((maxPerLine - lastLineCount) * step) / 2;
                }
                else
                {
                    curX += ((maxPerLine - lastLineCount) * step) / 2;
                }
            }

            CustomXpGlobe xpGlobe = xpGlobes.get(i);
            int startXp = xpTrackerService.getStartGoalXp(xpGlobe.getSkill());
            int goalXp = xpTrackerService.getEndGoalXp(xpGlobe.getSkill());

            renderProgressCircle(graphics, xpGlobe, startXp, goalXp, curX, curY, getBounds());

            // Track overlay size
            maxX = Math.max(maxX, curX);
            maxY = Math.max(maxY, curY);

            countInLine++;
            if (countInLine >= maxPerLine)
            {
                countInLine = 0;
                if (vertical)
                {
                    curX += step;
                    curY = strokeOffset;
                }
                else
                {
                    curX = strokeOffset;
                    curY += step;
                }
            }
            else
            {
                if (vertical)
                    curY += step;
                else
                    curX += step;
            }
        }

        return new Dimension(maxX + orbSize + strokeOffset, maxY + orbSize + strokeOffset);
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
            return;

        int yOffset = -config.iconVerticalOffset(); // positive config moves icon up, negative moves down
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
            return;

        final int orbSize = config.xpOrbSize();
        String skillLevel = String.valueOf(xpGlobe.getCurrentLevel());

        int fontSize = Math.max(12, orbSize / 3);
        graphics.setFont(new Font("Tahoma", Font.PLAIN, fontSize));

        FontMetrics fm = graphics.getFontMetrics();
        int textWidth = fm.stringWidth(skillLevel);
        int textHeight = fm.getHeight();

        int textX = x + (orbSize / 2) - (textWidth / 2);
        int textY = y + (orbSize / 2) + (textHeight / 2) - 2 - config.levelVerticalOffset();  // apply offset

        Color fillColor = config.useCustomLevelColor() ? config.levelArcColor() : SkillColor.find(xpGlobe.getSkill()).getColor();
        Color borderColor = config.levelBorderColor();
        int borderWidth = config.levelBorderWidth();

        // Draw outline
        graphics.setColor(borderColor);
        for (int dx = -borderWidth; dx <= borderWidth; dx++)
        {
            for (int dy = -borderWidth; dy <= borderWidth; dy++)
            {
                if (dx == 0 && dy == 0) continue;
                graphics.drawString(skillLevel, textX + dx, textY + dy);
            }
        }

        graphics.setColor(fillColor);
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
        if (!config.enableTooltips())
            return;

        // Reset the timer on the globe so it doesn't disappear while hovered
        mouseOverSkill.setTime(Instant.now());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        final PanelComponent xpTooltip = (PanelComponent) this.xpTooltip.getComponent();
        xpTooltip.getChildren().clear();

        // Add the skill name and current level at the top
        xpTooltip.getChildren().add(LineComponent.builder()
                .left(mouseOverSkill.getSkill().getName())
                .right(String.valueOf(mouseOverSkill.getCurrentLevel()))
                .build());

        // Collect tooltip lines from config
        TooltipLine[] configuredLines = new TooltipLine[] {
                config.tooltipLine1(),
                config.tooltipLine2(),
                config.tooltipLine3(),
                config.tooltipLine4(),
                config.tooltipLine5()
        };

        // Keep only unique lines, preserving order
        List<TooltipLine> lines = new ArrayList<>();
        for (TooltipLine line : configuredLines)
        {
            if (!lines.contains(line))
                lines.add(line);
        }

        // Render tooltip lines based on selection
        for (TooltipLine line : lines)
        {
            switch (line)
            {
                case CURRENT_TOTAL_XP:
                    xpTooltip.getChildren().add(LineComponent.builder()
                            .left("Current Total XP:")
                            .leftColor(Color.ORANGE)
                            .right(decimalFormat.format(mouseOverSkill.getCurrentXp()))
                            .build());
                    break;

                case XP_LEFT_FOR_LEVEL:
                    if (goalXp > mouseOverSkill.getCurrentXp())
                    {
                        int xpLeft = goalXp - mouseOverSkill.getCurrentXp();
                        xpTooltip.getChildren().add(LineComponent.builder()
                                .left("XP Till Level:")
                                .leftColor(Color.ORANGE)
                                .right(decimalFormat.format(xpLeft))
                                .build());
                    }
                    break;

                case ACTIONS_LEFT_BEFORE_LEVEL:
                    if (goalXp > mouseOverSkill.getCurrentXp())
                    {
                        int actionsLeft = xpTrackerService.getActionsLeft(mouseOverSkill.getSkill());
                        if (actionsLeft != Integer.MAX_VALUE)
                        {
                            xpTooltip.getChildren().add(LineComponent.builder()
                                    .left("Actions Left:")
                                    .leftColor(Color.RED) // Red color
                                    .right(decimalFormat.format(actionsLeft))
                                    .build());
                        }
                    }
                    break;

                case XP_PER_HOUR:
                    if (goalXp > mouseOverSkill.getCurrentXp())
                    {
                        int xpHr = xpTrackerService.getXpHr(mouseOverSkill.getSkill());
                        if (xpHr != 0)
                        {
                            xpTooltip.getChildren().add(LineComponent.builder()
                                    .left("XP Per Hour:")
                                    .leftColor(Color.ORANGE)
                                    .right(decimalFormat.format(xpHr))
                                    .build());
                        }
                    }
                    break;

                case TIME_TILL_LEVEL:
                    if (goalXp > mouseOverSkill.getCurrentXp())
                    {
                        String timeLeft = xpTrackerService.getTimeTilGoal(mouseOverSkill.getSkill());
                        xpTooltip.getChildren().add(LineComponent.builder()
                                .left("Time Till Level:")
                                .leftColor(Color.ORANGE)
                                .right(timeLeft)
                                .build());
                    }
                    break;
            }
        }
        tooltipManager.add(this.xpTooltip);
    }
}
