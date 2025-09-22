package net.runelite.client.plugins.customxporbs;

import java.awt.image.BufferedImage;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;

@Getter
@Setter
class CustomXpGlobe
{
	private Skill skill;
	private int currentXp;
	private int currentLevel;
	private Instant time;
	private int size;
    private transient BufferedImage skillIcon;
    private int cachedPriority;

    CustomXpGlobe(Skill skill, int currentXp, int currentLevel, Instant time)
	{
		this.skill = skill;
		this.currentXp = currentXp;
		this.currentLevel = currentLevel;
		this.time = time;
	}

}
