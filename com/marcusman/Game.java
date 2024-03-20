package com.marcusman;

import com.marcusman.graphics.AnimatedSprite;
import com.marcusman.utils.GameObject;
import com.marcusman.graphics.gui.GUI;
import com.marcusman.graphics.gui.GUIButton;
import com.marcusman.input.KeyBoardListener;
import com.marcusman.logic.Map;
import com.marcusman.input.MouseEventListener;
import com.marcusman.logic.Player;
import com.marcusman.utils.Rectangle;
import com.marcusman.graphics.RenderHandler;
import com.marcusman.graphics.gui.SDKButton;
import com.marcusman.graphics.Sprite;
import com.marcusman.graphics.SpriteSheet;
import com.marcusman.logic.Tiles;

import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.File;

public class Game extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public static int alpha = 0xFFFF00DC;
	public static final int TILE_SIZE=16;
	
	private Canvas canvas = new Canvas();
	private RenderHandler renderer;

	private SpriteSheet sheet;
	private SpriteSheet playerSheet;

	private int selectedTileID = 2;
	private int selectedLayer = 0;

	private Tiles tiles;
	private Map map;

	private GameObject[] objects;
	private KeyBoardListener keyListener = new KeyBoardListener();
	private MouseEventListener mouseListener = new MouseEventListener(this);

	private Player player;

	private int xZoom = 3;
	private int yZoom = 3;

	private volatile GameStatus status;

	private static final int TARGET_FPS=60;
	private static final long MS_PER_FRAME = TimeUnit.SECONDS.toMillis(1)/TARGET_FPS;
	
	public Game() {
		
		status = GameStatus.STOPPED;

		// Make our program shutdown when we exit out.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the position and size of our frame.
		setBounds(0, 0, 1000, 800);

		// Put our frame in the center of the screen.
		setLocationRelativeTo(null);

		// Add our graphics compoent
		add(canvas);

		// Make our frame visible.
		setVisible(true);

		// Create our object for buffer strategy.
		canvas.createBufferStrategy(3);

		renderer = new RenderHandler(getWidth(), getHeight());

		// Load Assets
		BufferedImage sheetImage = loadImage("/res/Tiles1.png");
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(TILE_SIZE, TILE_SIZE);

		BufferedImage playerSheetImage = loadImage("/res/Player.png");
		playerSheet = new SpriteSheet(playerSheetImage);
		playerSheet.loadSprites(20, 26);

		// Player Animated Sprites
		AnimatedSprite playerAnimations = new AnimatedSprite(playerSheet, 5);

		try {
			// Load Tiles
			tiles = new Tiles(new File(Game.class.getResource("/res/Tiles.txt").toURI()), sheet);

			// Load Map
			map = new Map(new File(Game.class.getResource("/res/Map.txt").toURI()), tiles);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Load SDK GUI
		GUIButton[] buttons = new GUIButton[tiles.size()];
		Sprite[] tileSprites = tiles.getSprites();

		for (int i = 0; i < buttons.length; i++) {
			Rectangle tileRectangle = new Rectangle(0, i * (TILE_SIZE * xZoom + 2), TILE_SIZE * xZoom, TILE_SIZE * yZoom);

			buttons[i] = new SDKButton(this, i, tileSprites[i], tileRectangle);
		}

		GUI gui = new GUI(buttons, 5, 5, true);

		// Load Objects
		objects = new GameObject[2];
		player = new Player(playerAnimations, xZoom, yZoom);
		objects[0] = player;
		objects[1] = gui;

		// Add Listeners
		canvas.addKeyListener(keyListener);
		canvas.addFocusListener(keyListener);
		canvas.addMouseListener(mouseListener);
		canvas.addMouseMotionListener(mouseListener);
		canvas.addMouseWheelListener(mouseListener);

		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				int newWidth = canvas.getWidth();
				int newHeight = canvas.getHeight();

				if (newWidth > renderer.getMaxWidth())
					newWidth = renderer.getMaxWidth();

				if (newHeight > renderer.getMaxHeight())
					newHeight = renderer.getMaxHeight();

				renderer.getCamera().w = newWidth;
				renderer.getCamera().h = newHeight;
				canvas.setSize(newWidth, newHeight);
				pack();
			}

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}
		});
		canvas.requestFocus();

	}
	double counter=0;
	
	public void update() {
		
			for (int i = 0; i < objects.length; i++)
				objects[i].update(this);
			
	}

	private BufferedImage loadImage(String path) {
		try {
			BufferedImage loadedImage = ImageIO.read(Game.class.getResource(path));
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage, 0, 0, null);

			return formattedImage;
		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public void leftClick(int x, int y) {

		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		boolean stoppedChecking = false;

		for (int i = 0; i < objects.length; i++)
			if (!stoppedChecking)
				stoppedChecking = objects[i].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);

		if (!stoppedChecking) {
			x = (int) Math.floor((x + renderer.getCamera().x) / ((float)TILE_SIZE * xZoom));
			y = (int) Math.floor((y + renderer.getCamera().y) / ((float)TILE_SIZE * yZoom));
			map.setTile(selectedLayer, x, y, selectedTileID);
		}
	}

	public void rightClick(int x, int y) {
		x = (int) Math.floor((x + renderer.getCamera().x) / ((float)TILE_SIZE * xZoom));
		y = (int) Math.floor((y + renderer.getCamera().y) / ((float)TILE_SIZE * yZoom));
		map.removeTile(selectedLayer, x, y);
	}

	public void render() {
		BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);

		map.render(renderer, objects, xZoom, yZoom);

		renderer.render(graphics);

		graphics.dispose();
		bufferStrategy.show();
		renderer.clear();
	}

	public void changeTile(int tileID) {
		selectedTileID = tileID;
	}

	public int getSelectedTile() {
		return selectedTileID;
	}

	public void setSelectedTile(int id) {
		this.selectedTileID = id;
	}

	public KeyBoardListener getKeyListener() {
		return keyListener;
	}

	public MouseEventListener getMouseListener() {
		return mouseListener;
	}

	public RenderHandler getRenderer() {
		return renderer;
	}

	public Map getMap() {
		return map;
	}

	public int getXZoom() {
		return xZoom;
	}

	public int getYZoom() {
		return yZoom;
	}

	public int getSelectedLayer() {
		return selectedLayer;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setSelectedLayer(int selectedLayer) {
		this.selectedLayer = selectedLayer;
	}

	public void start() {
		if(status.equals(GameStatus.RUNNING)) {
			throw new IllegalStateException("The game is already running!");
		} else {
			status = GameStatus.RUNNING;
			Thread gameThread = new Thread(this::gameLoop);
			gameThread.start();
		}
	}

	public void stop() {
		status = GameStatus.STOPPED;
	}
	
	private void gameLoop() {
		long prev = System.currentTimeMillis();
		long lag=0L;
		int frameCount=0;
		long lastFPSTime = System.currentTimeMillis();
		
		while(isGameRunning()) {
			long cur = System.currentTimeMillis();
			long elapsed = cur - prev;
			
			prev = cur;
			lag += elapsed;
			while(lag >= MS_PER_FRAME) {
				update();
				lag -= MS_PER_FRAME;
			}
			render();
			frameCount++;
			
			if(System.currentTimeMillis() - lastFPSTime >= TimeUnit.SECONDS.toMillis(1)) {
				float fps = (float) frameCount / ((System.currentTimeMillis() - lastFPSTime) / 1000);
				System.out.println("FPS: " +fps);
				frameCount=0;
				lastFPSTime=System.currentTimeMillis();
			}
		}
	}
	
	public boolean isGameRunning() {
		return status.equals(GameStatus.RUNNING);
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
}
