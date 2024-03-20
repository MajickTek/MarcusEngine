package com.marcusman.input;

import com.marcusman.Game;
import com.marcusman.utils.GameAudio;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.net.URISyntaxException;

public class MouseEventListener implements MouseListener, MouseMotionListener, MouseWheelListener 
{
	private Game game;

	public MouseEventListener(Game game) 
	{
		this.game = game;
	}

	public void mouseClicked(MouseEvent event)
	{

	}

	public void mouseDragged(MouseEvent event)
	{

	}

	public void mouseEntered(MouseEvent event)
	{

	}

	public void mouseExited(MouseEvent event)
	{

	}

	public void mouseMoved(MouseEvent event)
	{

	}

	public void mousePressed(MouseEvent event)
	{
		if(event.getButton() == MouseEvent.BUTTON1)
			game.leftClick(event.getX(), event.getY());

		if(event.getButton() == MouseEvent.BUTTON3)
			game.rightClick(event.getX(), event.getY());
		
		if(event.getButton() == 5) {
			try {
				GameAudio.play(new File(Game.class.getResource("/res/explode.wav").toURI()), game.getPlayer().getRectangle().x, game.getPlayer().getRectangle().y, 0, 0);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void mouseReleased(MouseEvent event)
	{
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		
	}
}
