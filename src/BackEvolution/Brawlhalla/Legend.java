package BackEvolution.Brawlhalla;

public class Legend {
	Weapon[] equipables;
	public boolean isEquipabble(Weapon weapon) {
		for (Weapon e: equipables)if (e.getName().equals(weapon.getName()))return true;
		return false;
	}
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
