package com.zian.scott.mygame;

import java.util.ArrayList;
import java.util.List;

public class AI {
	
	private World world;
	private Player player;
	private int board[][];
	private Parser parser;

	public AI(World world, Parser parser){
		this.world = world;
		this.player = world.players.get(1);
		this.board = world.board;
		this.parser = parser;
	}
	
	public void move(){
		int x = player.x;
		int y = player.y;
		
		int[] decisions = new int[4];
		
		decisions[0] = decide(x,y-1); //up
		decisions[1] = decide(x-1,y); //left
		decisions[2] = decide(x,y+1); //down
		decisions[3] = decide(x+1,y); //right
		
		List<Integer> twos = new ArrayList<Integer>();
		List<Integer> ones = new ArrayList<Integer>();
		List<Integer> zeros = new ArrayList<Integer>();
		List<Integer> neg = new ArrayList<Integer>();
		Integer dir = 0;
		for(int d: decisions){
			if(d==2)
				twos.add(dir);
			else if(d==1)
				ones.add(dir);
			else if(d==0)
				zeros.add(dir);
			else
				neg.add(dir);
			dir++;
		}
		
		if(!twos.isEmpty()){
			int choice = (int) Math.random() * (twos.size());
			if(choice >= twos.size()) choice--;
			player.direction = twos.get(choice);
			return;
		}
		if(!ones.isEmpty()){
			int choice = (int) (Math.random() * (ones.size()));
			if(choice >= ones.size()) choice--;
			player.direction = ones.get(choice);
			return;
		}
		if(!zeros.isEmpty()){
			int choice = (int) (Math.random() * (zeros.size()));
			if(choice >= zeros.size()) choice--;
			player.direction = zeros.get(choice);
			return;
		}
		if(!neg.isEmpty()){
			int choice = (int) (Math.random() * (zeros.size()));
			if(choice >= neg.size()) choice--;
			player.direction = neg.get(choice);
			return;
		}
		
	}
	
	private int decide(int x,int y){
		try{
			if (board[x][y] == 1) // red
				return 2;
			else if (board[x][y] == 0) // empty
				return 1;
			else  // blue
				return 0;
		} catch(Exception e){
			return -1;
		}
	}
	
	public void activate(){
		int use = (int) (Math.random() * (10));
		
		if(use>4) return; 		
		
		if(!player.powerUpList.isEmpty()){
			int choice = (int) (Math.random() * (player.powerUpList.size()));
			if(choice >= player.powerUpList.size()) choice--;
			String s = "Player1" + " activate "+player.powerUpList.get(choice).toString();
        	parser.parse(s);
        	player.powerUpList.remove(choice);
			
		}
	}

}
