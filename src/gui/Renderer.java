package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Sokoban.SokobanNode;
import Sokoban.SokobanPuzzle;

public class Renderer extends JPanel {
	private Dimension windowSize;
	private SokobanNode node;
	private int inset = 50, FPS = 10, FPS_INV, size = 50;
	private JFrame frame;
	private int lengthy, lengthx;

	public Renderer(SokobanPuzzle sp) {
		lengthy = sp.getPuzzle().length;
		lengthx = sp.getPuzzle()[0].length;
		windowSize = new Dimension(size * lengthx + inset * 2, size * lengthy + inset * 2);

		FPS_INV = 1000 / FPS;

		frame = new JFrame("Sokoban A* puzzle");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(windowSize);
		frame.setResizable(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(new Point((screen.getSize().width - windowSize.width) / 2,
				(screen.getSize().height - windowSize.height) / 2));
		frame.add(this);
	}

	@Override
	public void setVisible(boolean visible) {
		frame.setVisible(true);
	}

	public void update(SokobanNode node) {
		this.node = node;
		repaint();
		try {
			Thread.sleep(FPS_INV);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, windowSize.width, windowSize.height);
		if (node == null) {
			return;
		}

		for (int y = 0; y < lengthy; y++) {
			for (int x = 0; x < lengthx; x++) {
				g.setColor(Color.WHITE);
				switch (node.getPuzzle()[y][x]) {
				case SokobanPuzzle.EMPTY:
					g.setColor(Color.WHITE);
					break;
				case SokobanPuzzle.WALL:
					g.setColor(Color.DARK_GRAY);
					break;
				case SokobanPuzzle.BOX:
					g.setColor(Color.GREEN);
					break;
				case SokobanPuzzle.SOKOBAN:
					g.setColor(Color.CYAN);
					g.fillOval(inset + size * x + 5, inset + size * y + 5, size - 10, size - 10);
					break;
				case SokobanPuzzle.TARGET:
					g.setColor(Color.RED);
					break;
				case SokobanPuzzle.TARGET_SOKOBAN:
					g.setColor(Color.CYAN);
					g.fillOval(inset + size * x + 5, inset + size * y + 5, size - 10, size - 10);
					g.setColor(Color.RED);
					break;
				case SokobanPuzzle.TARGET_BOX:
					g.setColor(Color.ORANGE);
					break;
				}
				if (node.getPuzzle()[y][x] != SokobanPuzzle.SOKOBAN) {
					g.fillRect(inset + size * x, inset + size * y, size, size);
				}
				g.setColor(Color.BLACK);
				g.drawRect(inset + size * x, inset + size * y, size, size);
			}
		}
	}
}
