package org.montezuma.megacart;

public interface ProjectListener
{
	public void onGamesBytes(int currentGamesBytes);
	
	public void onGamesCount(int currentGamesCount);
	
	public void onSaveItem(int selectedRow);
	
	public void onTestItem512K(int selectedRow);
	
	public void onTestItem1MB(int selectedRow);
    
    public void onTestItem2MB(int selectedRow);
	
	public void onTestItem4MB(int selectedRow);
}
