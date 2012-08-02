package rltut.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import rltut.Creature;
import rltut.Item;
import rltut.StuffFactory;
import rltut.FieldOfView;
import rltut.Tile;
import rltut.World;
import rltut.WorldBuilder;
import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {
	private World world;
	private int screenWidth;
	private int screenHeight;
	private Creature player;

	private List<String> messages;

	private FieldOfView fov;
	
	private Screen subscreen;

	public PlayScreen() {
		screenWidth = 80;
		screenHeight = 21;
		messages = new ArrayList<String>();
		createWorld();

		fov = new FieldOfView(world);

		StuffFactory stuffFactory = new StuffFactory(world);
		createCreatures(stuffFactory);
		createItems(stuffFactory);
	}

	private void createItems(StuffFactory factory) {
		for (int z = 0; z < world.depth(); z++) {
			for (int i = 0; i < world.width() * world.height() / 20; i++) {
				factory.newRock(z);
			}
		}
		factory.newVictoryItem(world.depth() - 1);
	}

	private void createCreatures(StuffFactory stuffFactory) {
		player = stuffFactory.newPlayer(messages, fov);

		for (int z = 0; z < world.depth(); z++) {
			for (int i = 0; i < 1; i++) {
				stuffFactory.newFungus(z);
			}
			for (int i = 0; i < 20; i++) {
				stuffFactory.newBat(z);
			}
		}
	}

	private void createWorld() {
		world = new WorldBuilder(80, 45, 4).makeCaves().build();
	}

	private void displayMessages(AsciiPanel terminal, List<String> messages) {
		int top = screenHeight - messages.size();
		for (int i = 0; i < messages.size(); i++) {
			terminal.writeCenter(messages.get(i), top + i);
		}
		messages.clear();
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		int left = getScrollX();
		int top = getScrollY();

		displayTiles(terminal, left, top);
		terminal.write(player.glyph(), player.x - left, player.y - top,
				player.color());

		String stats = String
				.format(" %3d/%3d hp", player.hp(), player.maxHp());
		terminal.write(stats, 1, 23);
		displayMessages(terminal, messages);
		
		if (subscreen != null)
			subscreen.displayOutput(terminal);
	}

	private void displayTiles(AsciiPanel terminal, int left, int top) {
		fov.update(player.x, player.y, player.z, player.visionRadius());

		for (int x = 0; x < screenWidth; x++) {
			for (int y = 0; y < screenHeight; y++) {
				int wx = x + left;
				int wy = y + top;

				if (player.canSee(wx, wy, player.z))
					terminal.write(world.glyph(wx, wy, player.z), x, y,
							world.color(wx, wy, player.z));
				else
					terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y,
							Color.darkGray);

			}

		}

	}

	public int getScrollX() {
		return Math.max(
				0,
				Math.min(player.x - screenWidth / 2, world.width()
						- screenWidth));
	}

	public int getScrollY() {
		return Math.max(
				0,
				Math.min(player.y - screenHeight / 2, world.height()
						- screenHeight));
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		if (subscreen != null) {
			subscreen = subscreen.respondToUserInput(key);
		} else {
			switch (key.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_H:
				player.moveBy(-1, 0, 0);
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_L:
				player.moveBy(1, 0, 0);
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_K:
				player.moveBy(0, -1, 0);
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_J:
				player.moveBy(0, 1, 0);
				break;
			case KeyEvent.VK_Y:
				player.moveBy(-1, -1, 0);
				break;
			case KeyEvent.VK_U:
				player.moveBy(1, -1, 0);
				break;
			case KeyEvent.VK_B:
				player.moveBy(-1, 1, 0);
				break;
			case KeyEvent.VK_N:
				player.moveBy(1, 1, 0);
				break;
			case KeyEvent.VK_D:
				subscreen = new DropScreen(player);
				break;
			}

			switch (key.getKeyChar()) {
			case 'g':
			case ',':
				player.pickup();
				break;
			case '<':
				if (userIsTryingToExit())
					return userExits();
				else
					player.moveBy(0, 0, -1);
				break;
			case '>':
				player.moveBy(0, 0, 1);
				break;
			}
		}

		if (subscreen == null)
			world.update();

		if (player.hp() < 1)
			return new LoseScreen();

		return this;
	}

	private Screen userExits() {
		for (Item item : player.inventory().getItems()) {
			if (item != null && item.name().equals("teddy bear"))
				return new WinScreen();
		}
		return new LoseScreen();
	}

	private boolean userIsTryingToExit() {
		return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
	}
}
