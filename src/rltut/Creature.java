package rltut;

import java.awt.Color;

public class Creature {
	private World world;
	public int x;
	public int y;

	private char glyph;
	private Color color;

	private CreatureAi ai;

	private int maxHp;
	private int hp;

	private int attackValue;
	private int defenseValue;

	public Creature(World world, char glyph, Color color, int maxHp,
			int attack, int defense) {
		this.world = world;
		this.glyph = glyph;
		this.color = color;

		this.maxHp = maxHp;
		this.hp = maxHp;

		this.attackValue = attack;
		this.defenseValue = defense;
	}

	private void attack(Creature other) {
		int amount = Math.max(0, attackValue() - other.defenseValue());

		amount = (int) (Math.random() * amount) + 1;

		other.modifyHp(-amount);
		
		notify("You attack the '%s' for %d damage.", other.glyph, amount);
		other.notify("The '%s' attacks you for %d damage.", glyph, amount);
	}

	public int attackValue() {
		return attackValue;
	}

	public boolean canEnter(int wx, int wy) {
		return world.tile(wx, wy).isGround() && world.creature(wx, wy) == null;
	}

	public Color color() {
		return color;
	}

	public int defenseValue() {
		return defenseValue;
	}

	public void dig(int wx, int wy) {
		world.dig(wx, wy);
	}

	public char glyph() {
		return glyph;
	}

	public int hp() {
		return hp;
	}

	public int maxHp() {
		return maxHp;
	}

	public void modifyHp(int amount) {
		hp += amount;

		if (hp < 1)
			world.remove(this);
	}

	public void moveBy(int mx, int my) {
		Creature other = world.creature(x + mx, y + my);

		if (other == null)
			ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
		else
			attack(other);
	}

	public void setCreatureAi(CreatureAi ai) {
		this.ai = ai;
	}

	public void update() {
		ai.onUpdate();
	}
	
	public void notify(String message, Object ... params) {
		ai.onNotify(String.format(message,  params));
	}

}
